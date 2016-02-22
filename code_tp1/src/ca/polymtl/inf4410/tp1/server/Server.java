package ca.polymtl.inf4410.tp1.server;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import ca.polymtl.inf4410.tp1.shared.ServerInterface;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;

import java.security.MessageDigest;

import java.util.HashMap;

public class Server implements ServerInterface {

	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}

	private HashMap<String, Integer> locked;

	public Server() {
		super();
		locked = new HashMap<String, Integer>();
	}

	private void run() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try {
			ServerInterface stub = (ServerInterface) UnicastRemoteObject
					.exportObject(this, 0);

			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("server", stub);
			System.out.println("Server ready.");
		} catch (ConnectException e) {
			System.err
					.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
			System.err.println();
			System.err.println("Erreur: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
	}

	/*
	 * Trouve le plus grand id dans 'id_list.txt' et en génère un plus grand  
	 */
	@Override
	public int generateclientid() throws Exception {
		File file = new File("id_list.txt");
		if (!file.exists()) {
			file.createNewFile();
		}

		BufferedReader reader = new BufferedReader(new FileReader(file));

		// Generate new ID
		String line; int new_id = 0;
		while ((line = reader.readLine()) != null) {
			int current_id = Integer.parseInt(line);
			if(current_id > new_id) {
				new_id = current_id;
			}
		}	

		new_id++;
		reader.close();

		// Append new ID to file
		BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
		writer.write(Integer.toString(new_id) + "\n");
		writer.close();

		return new_id;
	}

	/*
	 * Crée un nouveau fichier, retourne sans rien faire si le fichier existe déjà
	 */
	@Override
	public String create(String name) throws Exception {
		File file = new File("server_files/" + name);
		if (file.exists()) {
			return "Fichier existe déjà";
		}

		file.createNewFile();
		return name + " ajouté";
	}

	/*
	 * Renvoie la liste des noms de fichiers avec le propriétaire du verrou si
	 * tel est le cas
	 */
	@Override
	public String list() throws Exception {
		File[] list = getFilesList();

		String ret = "";
		for (int i = 0; i < list.length; i++) {
			if (list[i].isFile()) {
				if (locked.get(list[i].getName()) == null) {
					ret += list[i].getName() + " - Non verouillé\n";
				}
				else {
					int id = locked.get(list[i].getName());
					ret += list[i].getName() + " - Verouillé par " + id + "\n";
				}
			}
		}

		ret += list.length + " fichier(s)";
		return ret;
	}

	/*
	 * Calcule le checksum MD5 du fichier spécifié. Si le checksum est différent de
	 * celui qui est passé, le fichier à jour est retourné
	 */
	@Override
	public File get(String name, byte[] checksum) throws Exception {
		System.err.println("BLaP");
		File file = new File("server_files/" + name);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		MessageDigest md = MessageDigest.getInstance("MD5");
		String line;
		while ((line = reader.readLine()) != null) {
			md.update(line.getBytes());
		}	

		if (!MessageDigest.isEqual(md.digest(), checksum)) {
			return file;
		}
		else {
			return null;
		}
	}

	/*
	 * Vérouille le fichier spécifié s'il ne l'est pas déjà, puis retourne
	 * la dernière version du fichier au client
	 */
	@Override
	public File lock(String name, int clientid, byte[] checksum) throws Exception {
		if(locked.get(name) != null) {
			System.err.println("File is already locked");
			return null;
		}

		locked.put(name, clientid);
		return get(name, checksum);
	}

	@Override
	public File[] syncLocalDir(){
		return getFilesList();
	}

	private File[] getFilesList(){
		File folder = new File("server_files");
		File[] list = folder.listFiles();
		return list;
	}
}

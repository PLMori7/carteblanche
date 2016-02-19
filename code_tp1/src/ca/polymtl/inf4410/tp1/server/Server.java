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

public class Server implements ServerInterface {

	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}

	public Server() {
		super();
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

		try {
			System.out.println("New id: " + generateclientid());
			if (create("test.txt")) {
				System.out.println("New file");
			}
			else {
				System.out.println("File already exists");
			}
			System.out.println("List: ");
			System.out.println(list());
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}

	}

	/*
	 * Méthode accessible par RMI. Additionne les deux nombres passés en
	 * paramètre.
	 */
	@Override
	public int execute(int a, int b) throws RemoteException {
		return a + b;
	}

	/*
	 * Autre méthode accessible par RMI. Elle prend un tableau afin de pouvoir
	 * lui envoyer des arguments de taille variable.
	 */
	@Override
	public void execute(byte[] arg) throws RemoteException {
		return;
	}

	/*
	 * Trouve le plus grand id dans 'id.txt' et en génère un plus grand  
	 */
	public int generateclientid() throws Exception {
		File file = new File("id.txt");
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
	public boolean create(String name) throws Exception {
		File file = new File("files/" + name);
		if (file.exists()) {
			return false;
		}

		file.createNewFile();
		return true;
	}

	/*
	 * Renvoie la liste des noms de fichiers avec le propriétaire du verrou si
	 * tel est le cas
	 */
	public String list() throws Exception {
		File folder = new File("files");
		File[] list = folder.listFiles();	

		String ret = "";
		for (int i = 0; i < list.length; i++) {
			if (list[i].isFile()) {
				ret += list[i].getName() + " - Non verouillé\n";
			}
		}

		return ret;
	}
}

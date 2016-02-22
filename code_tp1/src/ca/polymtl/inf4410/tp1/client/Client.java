package ca.polymtl.inf4410.tp1.client;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.FileInputStream;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.security.MessageDigest;

import ca.polymtl.inf4410.tp1.shared.ServerInterface;

public class Client {
	public static void main(String[] args) {
		String command = null;
		String arg = null;

		if (args.length == 1) {
			command = args[0];
		}
		if (args.length > 1) {
			command = args[0];
			arg = args[1];
		}

		Client client = new Client();
		client.run(command, arg);
	}

	private ServerInterface localServerStub = null;
	private int id = 0;

	public Client() {
		super();

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		localServerStub = loadServerStub("127.0.0.1");
	}

	private void run(String command, String arg) {
		try {
			// Récupère le ID du client ou le crée s'il est inexistant
			File id_file = new File("id_client.txt");
			if (id_file.exists()) {
				BufferedReader reader = new BufferedReader(new FileReader(id_file));
				String line = reader.readLine();
				id = Integer.parseInt(line);
				reader.close();
			}
			else {
				id_file.createNewFile();
				id = localServerStub.generateclientid();
				BufferedWriter writer = new BufferedWriter(new FileWriter(id_file));
				writer.write(Integer.toString(id) + "\n");
				writer.close();
			}
		}
		catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}

		switch (command) {
			case "create":
				if (arg == null) {
					System.out.println("Veuillez spécifier un nom de fichier");
					return;
				}

				try {
					System.out.println(localServerStub.create(arg));
				}
				catch (Exception e) {
					System.err.println("Erreur: " + e.getMessage());
				}
				break;

			case "list":
				try {
					System.out.println(localServerStub.list());
				}
				catch (Exception e) {
					System.err.println("Erreur: " + e.getMessage());
				}
				break;

			case "get":
				try {
					File file = new File("client_files/" + arg);
					byte[] checksum = calculateChecksum(file);
			 
					// Recupére le fichier retourné par le serveur et synchroniser
					File ret = localServerStub.get(arg, checksum);
					if (ret != null) {
						sync(ret, arg);
						System.out.println(arg + " synchronisé");
					}
					else {
						System.out.println(arg + " déjà à jour");
					}
				}
				catch (Exception e) {
					System.err.println("Erreur: " + e.getMessage());
				}
				break;

			case "lock":
				try {
					File file = new File("client_files/" + arg);
					if (!file.exists()) {
						System.err.println("Fichier inexistant. Possédez-vous une copie locale?");
						break;
					}

					byte[] checksum = calculateChecksum(file);

					// Recupére le fichier retourné par le serveur et synchronise
					File ret = localServerStub.lock(arg, id, checksum);
					if (ret != null) {
						sync(ret, arg);
					}

					System.out.println(arg + " verrouillé");
				}
				catch (Exception e) {
					System.err.println("Erreur: " + e.getMessage());
				}
				break;
			case "syncLocalDir":
				try {
						File[] ret = localServerStub.syncLocalDir();
					for (int i=0; i<ret.length; i++) {
						sync(ret[i], ret[i].getName());
					}
				}
				catch (Exception e) {
					System.err.println("Erreur: " + e.getMessage());
				}
				break;
			case "push":
				try {
					System.err.println("BLOOP");
					File file = new File("client_files/" + arg);
					System.err.println("BLAP");
					System.out.println(localServerStub.push(arg, file, id));
					System.err.println("BLeepP");
				}
				catch (Exception e) {
					System.err.println("Erreur: " + e.getMessage());
				}
				break;
		}
	}

	private ServerInterface loadServerStub(String hostname) {
		ServerInterface stub = null;

		try {
			Registry registry = LocateRegistry.getRegistry(hostname);
			stub = (ServerInterface) registry.lookup("server");
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage()
					+ "' n'est pas défini dans le registre.");
		} catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}

		return stub;
	}

	private byte[] calculateChecksum(File file) throws Exception {
		if (!file.exists()) {
			return null;
		}

		BufferedReader reader = new BufferedReader(new FileReader(file));
		MessageDigest md = MessageDigest.getInstance("MD5");

		String line;
		while ((line = reader.readLine()) != null) {
			md.update(line.getBytes());
		}	

		reader.close();
		return md.digest();
	}

	private void sync(File source, String dest) throws Exception {
		if (!source.exists()) {
			return;
		}

		InputStream is = new FileInputStream(source);
		Path path = Paths.get("client_files/" + dest);
		Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
		is.close();
	}
}

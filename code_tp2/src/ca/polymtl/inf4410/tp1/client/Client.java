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

			command = args[0];
			arg = args[1];

		Client client = new Client();
		client.run(command, arg);
	}

	private ServerInterface localServerStub = null;

	public Client() {
		super();

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		localServerStub = loadServerStub("127.0.0.1");
	}

	private void run(String command, String arg) {
		switch (command) {
			case "fib":
				System.out.println("FIB BOYS");
				if (arg == null) {
					System.out.println("Veuillez spécifier une valeur d'opérande.");
					return;
				}

				try {
					System.out.println("CLIENT BEFORE FIB BOYS");
					System.out.println(localServerStub.fibonacci(Integer.parseInt(arg)));
				}
				catch (Exception e) {
					System.err.println("Erreur: " + e.getMessage());
				}
				break;

			case "prime":
				System.out.println("PRIME BOYS");
				if (arg == null) {
					System.out.println("Veuillez spécifier une valeur d'opérande.");
					return;
				}
				try {
					System.out.println("CLIENT BEFORE PRIME BOYS");
					System.out.println(localServerStub.primeFactor(Integer.parseInt(arg)));
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
}

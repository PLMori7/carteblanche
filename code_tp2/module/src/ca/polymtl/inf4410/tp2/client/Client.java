package ca.polymtl.inf4410.tp2.client;

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
import java.util.ArrayList;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.security.MessageDigest;

import ca.polymtl.inf4410.tp2.shared.ServerInterface;
import ca.polymtl.inf4410.tp2.shared.Operation;
import ca.polymtl.inf4410.tp2.shared.ComputingServerOverloadException;

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
				if (arg == null) {
					System.out.println("Veuillez spécifier une valeur d'opérande.");
					return;
				}

				try {
					System.out.println(localServerStub.fibonacci(Integer.parseInt(arg)));
				}
				catch (Exception e) {
					System.err.println("Erreur: " + e.getMessage());
				}
				break;

			case "prime":
				if (arg == null) {
					System.out.println("Veuillez spécifier une valeur d'opérande.");
					return;
				}
				try {
					System.out.println(localServerStub.primeFactor(Integer.parseInt(arg)));
				}
				catch (Exception e) {
					System.err.println("Erreur: " + e.getMessage());
				}
				break;
			case "test":
				if (arg != null) {
					System.out.println("Cette commande ne nécéssite pas d'argument.");
					return;
				} else {
					Operation op1 = new Operation("fib", 4);
					Operation op2 = new Operation("prime", 9);
					Operation op3 = new Operation("fib", 13);
					Operation op4 = new Operation("square", 16);
					Operation op5 = new Operation("prime", 7919);

					ArrayList<Operation> taskList = new ArrayList<Operation>();
					taskList.add(op1);
					taskList.add(op2);
					taskList.add(op3);
					taskList.add(op4);
					taskList.add(op5);
					try {
						localServerStub.handleTasks(taskList, true);
					} catch (Exception e) {
						System.err.println("Erreur: " + e.getMessage());
					}
				}
				break;
		}
	}

	private ServerInterface loadServerStub(String hostname) {
		ServerInterface stub = null;

		try {
			Registry registry = LocateRegistry.getRegistry(hostname, 5000);
			String[] serverList = registry.list();
			for (int i=0;i<serverList.length;i++){
				System.out.println(serverList[i]);
			}
			stub = (ServerInterface) registry.lookup("RWICHU");
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

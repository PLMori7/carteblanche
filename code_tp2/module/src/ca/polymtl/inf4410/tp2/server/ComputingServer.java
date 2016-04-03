package ca.polymtl.inf4410.tp2.server;

import java.util.HashMap;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import ca.polymtl.inf4410.tp2.shared.ServerInterface;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.security.MessageDigest;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import ca.polymtl.inf4410.tp2.shared.ServerInterface;
import ca.polymtl.inf4410.tp2.shared.Operations;
import ca.polymtl.inf4410.tp2.shared.Operation;

public class ComputingServer implements ServerInterface {
	private ComputingServerConfig serverConfig = null;

	public static void main(String[] args) {
		ComputingServer server = new ComputingServer();
		if (args.length != 3) {
			System.err.println("Veuillez entrez les bons arguments dans l'ordre suivant : \n"
					+ "./server   PORT   TASK_SIZE_CAPACITY   MALICE_LEVEL.");
			return;
		} else {
			int port = Integer.parseInt(args[0]);
			int capacity = Integer.parseInt(args[1]);
			int malice = Integer.parseInt(args[2]);

			if ((port > 5050) || (port < 5000)) {
				System.err.println("Erreur: Veuillez entrez un port entre 5000 et 5050.");
			} else if ((malice > 10) || (malice < 0)) {
				System.err.println("Erreur: Veuillez entrez un niveau de malice entre 1 et 10.");
			} else {
				server.run(port, capacity, malice);
			}
		}
	}

	private HashMap<String, Integer> locked;

	public ComputingServer() {
		super();
		locked = new HashMap<String, Integer>();
	}

	private void run(int port, int capacity, int malice) {
		this.serverConfig = new ComputingServerConfig(port, capacity, malice);
		System.out.println("Server Port: " + serverConfig.getPort());
		System.out.println("Server max task size: " + serverConfig.getTaskSizeCapacity());
		System.out.println("Server malice lvl: " + serverConfig.getMaliceLevel());
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
 * Calcul le nieme terme de la suite de Fibonacci
 */
	@Override
	public int handleTasks(ArrayList<Operation> pendingOperations) throws Exception {
		if (pendingOperations.size() > this.serverConfig.getTaskSizeCapacity()) {
			System.err.println("La taille de la liste de tâches dépasse la capacité du serveur.");
		} else {
			for(Operation operation: pendingOperations) {
				System.out.println("Operation : " + operation.getType() + " " + operation.getOperand());
				switch(operation.getType()){
					case "fib":
						System.out.println("Result : " + fibonacci(operation.getOperand()));
						break;
					case "prime":
						System.out.println("Result : " + primeFactor(operation.getOperand()));
						break;
				}
			}
		}

		return 0;
	}


	/*
	 * Calcul le nieme terme de la suite de Fibonacci
	 */
	@Override
	public String fibonacci(int operand) throws Exception {
//		try {
			return operand + "ieme de Fibonacci : " + Operations.fib(operand);
//		} catch (Exception e) {
//			System.err.println("Erreur: " + e.getMessage());
//		}
	}

	/*
	 * Calcul le plus grand facteur premier
	 */
	@Override
	public String primeFactor(int operand) throws Exception {
//		try {
			return "Plus grand facteur premier de " + operand + " est : " + Operations.prime(operand);
//		} catch (Exception e) {
//			System.err.println("Erreur: " + e.getMessage());
//		}
	}
}

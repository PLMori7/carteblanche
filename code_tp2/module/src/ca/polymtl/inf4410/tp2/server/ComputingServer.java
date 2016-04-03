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
import java.util.Random;
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
		if (args.length != 4) {
			System.err.println("Veuillez entrez les bons arguments dans l'ordre suivant : \n"
					+ "./server   NAME PORT   TASK_SIZE_CAPACITY   MALICE_LEVEL.");
			return;
		} else {
			String name = args[0];
			int port = Integer.parseInt(args[1]);
			int capacity = Integer.parseInt(args[2]);
			int malice = Integer.parseInt(args[3]);

			if ((port > 5050) || (port < 5000)) {
				System.err.println("Erreur: Veuillez entrez un port entre 5000 et 5050.");
				return;
			} else if ((malice > 10) || (malice < 0)) {
				System.err.println("Erreur: Veuillez entrez un niveau de malice entre 1 et 10.");
				return;
			} else {
				server.run(name, port, capacity, malice);
			}
		}
	}

	private HashMap<String, Integer> locked;

	public ComputingServer() {
		super();
		locked = new HashMap<String, Integer>();
	}

	private void run(String name, int port, int capacity, int malice) {
		this.serverConfig = new ComputingServerConfig(name, port, capacity, malice);
		System.out.println("Server Name: " + serverConfig.getName());
		System.out.println("Server Port: " + serverConfig.getPort());
		System.out.println("Server max task size: " + serverConfig.getTaskSizeCapacity());
		System.out.println("Server malice lvl: " + serverConfig.getMaliceLevel());
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try {
			ServerInterface stub = (ServerInterface) UnicastRemoteObject
					.exportObject(this, serverConfig.getPort());

			Registry registry = LocateRegistry.getRegistry("127.0.0.1", 5000);
			registry.rebind(serverConfig.getName(), stub);
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
	 * Traite la liste de tâches envoyée par le Dispatcher.
	 */
	@Override
	public int handleTasks(ArrayList<Operation> pendingOperations) throws Exception {
		int result = 0;
		if (serverOverloaded(pendingOperations.size())) {
			throw new Exception("Le serveur est trop chargé pour accepter cette tâche.");
		} else {
			for(Operation operation: pendingOperations) {
				System.out.println("Opération: " + operation.getType() + " " + operation.getOperand());
				switch(operation.getType()){
					case "fib":
						System.out.println("\tRésultat: " + fibonacci(operation.getOperand()));
						result += fibonacci(operation.getOperand());
						break;
					case "prime":
						System.out.println("\tRésultat: " + primeFactor(operation.getOperand()));
						result += primeFactor(operation.getOperand());
						break;
					default:
						System.out.println("\t" + operation.getType() + " n'est pas une opération valide (doit être \"fib\" ou \"prime\").");
						break;
				}
			}

		}
		if (this.serverConfig.getMaliceLevel() == 0) {
			System.out.println("Le serveur n'est pas malicieux.");
		} else {
			System.out.println("Alerte! Serveur malicieux d'indice: " + this.serverConfig.getMaliceLevel() + "!");
			Random rand = new Random();
			float malice = (rand.nextFloat() * 9) + 1;
			System.out.println("Valeur malice: " + malice);
			if ((malice <= this.serverConfig.getMaliceLevel()) && (malice >0)) {
				System.out.println("Valeur compromise!");
				result += (rand.nextFloat() * 99) +1;
			}
		}
		System.out.println("Résultat des opérations: " + result);
		System.out.println("Résultat des opérations après modulo: " + (result % 5000));
		return result;
	}


	/*
	 * Calcul le nieme terme de la suite de Fibonacci
	 */
	@Override
	public int fibonacci(int operand) throws Exception {
			return Operations.fib(operand);
	}

	/*
	 * Calcul le plus grand facteur premier
	 */
	@Override
	public int primeFactor(int operand) throws Exception {
			return Operations.prime(operand);
	}

	/*
	 * Vérifie si le serveur est en mesure de traiter la liste de tâches
	 */
	public boolean serverOverloaded(int taskSize) {
		if (taskSize > this.serverConfig.getTaskSizeCapacity()) {
			float refusalRate = (((float) taskSize - (float) this.serverConfig.getTaskSizeCapacity()) / (9 * (float) this.serverConfig.getTaskSizeCapacity())) * 100;
			System.err.println("Tâche trop volumineuse, taux de refus: " + refusalRate);
			Random rand = new Random();
			float randomSuccess = rand.nextFloat() * 100;
			System.err.println("Valeur : " + randomSuccess);
			if (randomSuccess >= refusalRate) {
				System.err.println("Tâche va être exécutée!");
				return false;
			} else {
				System.err.println("La taille de la liste de tâches dépasse la capacité du serveur.");
				return true;
			}

		} else {
			return false;
		}

	}
}

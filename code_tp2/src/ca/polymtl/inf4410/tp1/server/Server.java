package ca.polymtl.inf4410.tp1.server;

import java.util.HashMap;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
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
import java.io.InputStream;
import java.io.FileInputStream;

import java.security.MessageDigest;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import ca.polymtl.inf4410.tp1.shared.ServerInterface;
import ca.polymtl.inf4410.tp1.shared.Operations;

public class Server implements ServerInterface {
	Operations op;
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
	 * Calcul le nieme terme de la suite de Fibonacci
	 */
	@Override
	public String fibonacci(int operand) throws Exception {
//		try {
			return operand + "ieme de Fibonacci : " + op.fib(operand);
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
			return "Plus grand facteur premier de " + operand + " est : " + op.prime(operand);
//		} catch (Exception e) {
//			System.err.println("Erreur: " + e.getMessage());
//		}
	}
}

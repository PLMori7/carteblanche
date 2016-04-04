package ca.polymtl.inf4410.tp2.server;

import java.io.*;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.security.MessageDigest;

import java.util.ArrayList;

import ca.polymtl.inf4410.tp2.shared.ServerInterface;
import ca.polymtl.inf4410.tp2.shared.Operation;

public class Dispatcher {

	private ArrayList<Operation> mPendingOperations;
	private ArrayList<ServerInterface> mServers;

	public void Dispatcher(String dataPath) {
		loadServerStubs("127.0.0.1");
		try {
			run(dataPath);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void run(String dataPath) throws Exception {
		File data = new File(dataPath);
		readInstructions(data);
	}

	/*
    TODO:
    ArrayList of workers, add/del/get workers, start/stop workers
    Maybe make DispatcherConfig class to add stubs to before creating actual dispatcher (what's the point?)
    DispatcherSafe DispatcherUnsafe for different dispatch behaviour (not implemented)
	*/


	public Dispatcher(File operations) throws Exception {
		readInstructions(operations);
		System.out.println(printRemainingOperations());

		/*
		TODO:
		Create ArrayList of workers from stubs
		 */
	}


	public String dispatch() {
		/*
		TODO:
		SAFE MODE:
		Add instructions to workers until full (maybe create a thread for each worker that keeps it full)
		Add up results % 5000 and return

		UNSAFE MODE:
		Add all instructions to all workers
		Pick most popular answer
		Return
		 */

		return "lofuckingl";
	}

	private void readInstructions (File operations) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(operations));
		String line;

		while ((line = br.readLine()) != null) {
			String[] command = line.split(" ");
			mPendingOperations.add(new Operation(command[0], Integer.parseInt(command[1])));
		}
	}

	private String printRemainingOperations () {
		String ret = "";
		for (Operation op : mPendingOperations) {
			ret += op.getType() + " " + op.getOperand() + "\n";
		}

		return ret;
	}

	private void loadServerStubs (String hostname) {
		try {
			Registry registry = LocateRegistry.getRegistry(hostname);
			String[] servers = registry.list();

			for (String name : servers) {
				mServers.add((ServerInterface) registry.lookup(name));
			}
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage()
					+ "' n'est pas défini dans le registre.");
		} catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}

	}

	private class DispatcherRunnable implements Runnable {
		private int mId;
		private int mStart;
		private int mEnd;

		public DispatcherRunnable(int start, int end, int id) {
			mStart = start;
			mEnd = end;
			mId = id;
		}

		@Override
		public void run() {
		}
	}
}


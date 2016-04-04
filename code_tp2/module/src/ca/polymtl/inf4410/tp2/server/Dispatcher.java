package ca.polymtl.inf4410.tp2.server;

import java.io.*;
import java.nio.channels.InterruptedByTimeoutException;
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
import java.util.Collections;

import ca.polymtl.inf4410.tp2.shared.ComputingServerOverloadException;
import ca.polymtl.inf4410.tp2.shared.ServerInterface;
import ca.polymtl.inf4410.tp2.shared.Operation;

public class Dispatcher {

	private ArrayList<Operation> mPendingOperations;
	private ArrayList<ServerInterface> mServers;
	private ArrayList<DispatcherRunnable> mWorkers;
    private ArrayList<Thread> mThreads;

	private String mDataPath;
    private Boolean mSafe;
    private String mHost;
    private int mPort;

	public static void main(String args[]) {
        if (args.length == 4) {
            String dataPath = args[0];
            Boolean safe = args[1].equals("1");
            String host = args[2];
            int port = Integer.parseInt(args[3]);

            Dispatcher dispatcher = new Dispatcher(dataPath, safe, host, port);
            try {
                if (safe) {
                    dispatcher.runSafe();
                }
                else {
                    dispatcher.runUnsafe();
                }
            }
            catch (Exception e) {
                System.out.println("Error while running dispatcher: " + e.getMessage());
		    }
        }
        else {
            System.out.println("USAGE: java -jar dispatcher.jar [path] [safe 0|1] [hostname] [port]");
        }
	}

	private Dispatcher(String dataPath, boolean safe, String host, int port) {
        mPendingOperations = new ArrayList<>();
        mServers = new ArrayList<>();
		mWorkers = new ArrayList();
        mThreads = new ArrayList<>();

        mDataPath = dataPath;
        mSafe = safe;
        mHost = host;
        mPort = port;

		loadServerStubs();
	}

	private void runSafe() throws Exception {
		File data = new File(mDataPath);
		readInstructions(data);

        int nbOpPerThread = (int) Math.ceil((double) mPendingOperations.size() / mServers.size());
        for (int i = 0; i < mServers.size(); i++) {
			int startIndex = i * nbOpPerThread;
			int endIndex = (i+1) * nbOpPerThread;

			if (endIndex > mPendingOperations.size()) {
				endIndex = mPendingOperations.size() - 1;
			}

            DispatcherRunnable worker = new DispatcherRunnable(startIndex, endIndex, i, mServers.get(i));
			mWorkers.add(worker);

            Thread t = new Thread(worker);
            t.start();
			mThreads.add(t);
        }

		for (Thread t : mThreads) {
			t.join();
		}

		int result = 0;
		for (DispatcherRunnable worker : mWorkers) {
			for (Integer i: worker.getResults()) {
				result += i;
				result %= 5000;
			}
		}

		System.out.println("Final result: " + result);
	}

    private void runUnsafe() throws Exception {
		File data = new File(mDataPath);
		readInstructions(data);

        for (int i = 0; i < mServers.size(); i++) {
            DispatcherRunnable worker = new DispatcherRunnable(0, mPendingOperations.size(), i, mServers.get(i));
			mWorkers.add(worker);

            Thread t = new Thread(worker);
            t.start();
			mThreads.add(t);
        }

		for (Thread t : mThreads) {
			t.join();
		}

		int result = 0;
		for (int i = 0; i < mPendingOperations.size(); i++) {
			ArrayList<Integer> resultsForCurrentOperation = new ArrayList<>();
			int mostFrequentResult = 0; int highestFrequency = 0;
			for (DispatcherRunnable worker: mWorkers) {
				int currentResult = worker.getResults().get(i);
				resultsForCurrentOperation.add(currentResult);

				int frequency = Collections.frequency(resultsForCurrentOperation, currentResult);
				if (frequency > highestFrequency) {
					mostFrequentResult = currentResult;
					highestFrequency = frequency;
				}
			}

			result += mostFrequentResult;
			result %= 5000;
		}

		System.out.println("Final result: " + result);
	}

	private void readInstructions (File operations) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(operations));
		String line;

		while ((line = br.readLine()) != null) {
			String[] command = line.split(" ");
			mPendingOperations.add(new Operation(command[0], Integer.parseInt(command[1])));
		}
	}

	private void loadServerStubs () {
		try {
			Registry registry = LocateRegistry.getRegistry(mHost, mPort);
			String[] serverList = registry.list();

			for (String name : serverList) {
				mServers.add((ServerInterface) registry.lookup(name));
			}
		}
        catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage()
					+ " n'est pas défini dans le registre.");
		}
        catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage()
                    + " le registre est local et vous n'avez pas acces");
		}
        catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage()
                    + " tentative de commmunication a echoue");
		}
	}

	private class DispatcherRunnable implements Runnable {
		private int mId;
		private int mStart;
		private int mEnd;

		private ServerInterface mDedicatedServer;
		private ArrayList<Integer> mResults;

		private DispatcherRunnable(int start, int end, int id, ServerInterface dedicatedServer) {
			mStart = start;
			mEnd = end;
			mId = id;

			mDedicatedServer = dedicatedServer;
			mResults = new ArrayList<>();
		}

		@Override
		public void run() {
			try {
				mResults.addAll(mDedicatedServer.handleTasks(new ArrayList<>(mPendingOperations.subList(mStart, mEnd))));
			}
			catch (ComputingServerOverloadException e) {
				while (mStart < mEnd) {
                    try {
                        mResults.addAll(mDedicatedServer.handleTasks(new ArrayList<>(mPendingOperations.subList(mStart, mStart + 1))));
                    }
                    catch (ComputingServerOverloadException stillOverloaded) {
                        mStart--;
                    }
                    catch (Exception generalException) {
                        System.out.println("wtf?");
                    }

                    mStart++;
                }
			}
			catch (Exception e) {
				System.out.println("wtf?");
			}
		}

		public ArrayList<Integer> getResults() {
			return mResults;
		}
	}
}


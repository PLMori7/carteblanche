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

import ca.polymtl.inf4410.tp2.shared.ComputingServerOverloadException;
import ca.polymtl.inf4410.tp2.shared.ServerInterface;
import ca.polymtl.inf4410.tp2.shared.Operation;

public class Dispatcher {

	private ArrayList<Operation> mPendingOperations;
	private ArrayList<ServerInterface> mServers;
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

        int nbOpPerThread = mPendingOperations.size() / mServers.size();
        for (int i = 0; i < mServers.size(); i++) {
            DispatcherRunnable worker = new DispatcherRunnable(i * nbOpPerThread, (i+1) * nbOpPerThread - 1, i, mServers.get(i));
            Thread t = new Thread(worker);
            t.start();
            mThreads.add(t);
        }
	}

    private void runUnsafe() throws Exception {
        System.out.println("Unsafe mode is not supported yet.");
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
        private int mResult;

		private ServerInterface mDedicatedServer;

		private DispatcherRunnable(int start, int end, int id, ServerInterface dedicatedServer) {
			mStart = start;
			mEnd = end;
			mId = id;
            mDedicatedServer = dedicatedServer;
		}

		@Override
		public void run() {
			try {
                System.out.println("Worker " + mId + " calculating operations " + mStart + "-" + mEnd);
				mResult = mDedicatedServer.handleTasks(new ArrayList<>(mPendingOperations.subList(mStart, mEnd)));
                mResult %= 5000;
			}
			catch (ComputingServerOverloadException e) {
				while (mStart < mEnd) {
                    System.out.println("Worker " + mId + " calculating operation " + mStart);
                    try {
                        mResult += mDedicatedServer.handleTasks(new ArrayList<>(mPendingOperations.subList(mStart, mStart + 1)));
                        mResult %= 5000;
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

            System.out.println("Worker " + mId + " done. Result: " + mResult);
		}
	}
}


package ca.polymtl.inf4410.tp1.client;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import ca.polymtl.inf4410.tp1.shared.ServerInterface;

public class Client {
	public static void main(String[] args) {
		String distantHostname = null;
		int size = 0;

		if (args.length > 0) {
			distantHostname = args[0];
			size = Integer.parseInt(args[1]);
		}

		Client client = new Client(distantHostname, size);
		client.run();
	}

	FakeServer localServer = null; // Pour tester la latence d'un appel de
									// fonction normal.
	private ServerInterface localServerStub = null;
	private ServerInterface distantServerStub = null;
	private byte[] data = null;

	public Client(String distantServerHostname, int size) {
		super();

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		localServer = new FakeServer();
		localServerStub = loadServerStub("127.0.0.1");

		data = new byte[(int)Math.pow(10, size)];	

		if (distantServerHostname != null) {
			distantServerStub = loadServerStub(distantServerHostname);
		}
	}

	private void run() {
		appelNormal();

		if (localServerStub != null) {
			appelRMILocal();
		}

		if (distantServerStub != null) {
			appelRMIDistant();
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

	private void appelNormal() {
		long start = System.nanoTime();
		localServer.execute(data);
		long end = System.nanoTime();

		System.out.println("Appel normal - Temps écoulé: " + (end - start) + " ns");
	}

	private void appelRMILocal() {
		try {
			long start = System.nanoTime();
			localServerStub.execute(data);
			long end = System.nanoTime();

			System.out.println("Appel local - Temps écoulé: " + (end - start) + " ns");
		}
		catch (Exception e) {
			System.out.println("woops");
		}
	}

	private void appelRMIDistant() {
		try {
			long start = System.nanoTime();
			distantServerStub.execute(data);
			long end = System.nanoTime();

			System.out.println("Appel distant - Temps écoulé: " + (end - start) + " ns");
		}
		catch (Exception e) {
			System.out.println("woops");
		}
	}
}

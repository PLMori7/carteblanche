package ca.polymtl.inf4410.tp1.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

import java.io.File;

public interface ServerInterface extends Remote {
	int generateclientid() throws Exception;
	String create(String name) throws Exception;
	String list() throws Exception;
	File get(String name, byte[] checksum) throws Exception;
	File lock(String name, int clientid, byte[] checksum) throws Exception;
	File[] syncLocalDir() throws Exception;
	String push(String name, File content, int clientid) throws Exception;
}

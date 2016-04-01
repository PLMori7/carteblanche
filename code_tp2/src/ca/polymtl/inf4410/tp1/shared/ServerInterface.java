package ca.polymtl.inf4410.tp1.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

import java.io.File;

public interface ServerInterface extends Remote {
	String fibonacci(int operand) throws Exception;
	String primeFactor(int operand) throws Exception;
}

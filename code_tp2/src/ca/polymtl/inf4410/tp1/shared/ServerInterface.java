package ca.polymtl.inf4410.tp1.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

import java.io.File;

public interface ServerInterface extends Remote {
	int fib(int operand) throws Exception;
	int prime(int operand) throws Exception;
}

package ca.polymtl.inf4410.tp2.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.io.File;

public interface ServerInterface extends Remote {
	int fibonacci(int operand) throws Exception;
	int primeFactor(int operand) throws Exception;
	ArrayList<Integer> handleTasks(ArrayList<Operation> pendingOperations) throws Exception;
}

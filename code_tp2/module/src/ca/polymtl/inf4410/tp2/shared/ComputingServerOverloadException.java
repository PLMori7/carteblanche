package ca.polymtl.inf4410.tp2.shared;
import java.rmi.RemoteException;

public class ComputingServerOverloadException extends RemoteException {

    public ComputingServerOverloadException(String message){
        super(message);
    }

}
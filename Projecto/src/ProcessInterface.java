import java.rmi.*;

public interface ProcessInterface extends Remote {
    void Election(String callerID) throws RemoteException;
}

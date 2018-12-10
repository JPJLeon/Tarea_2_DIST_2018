import java.rmi.*;

public interface ProcessInterface extends Remote {
    void Election(int callerMaxID, int callerID) throws Exception;
}

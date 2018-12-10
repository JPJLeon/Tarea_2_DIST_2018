import java.rmi.*;

public interface ProcessInterface extends Remote {
    void Election(int callerMaxID, int callerID, int initID) throws Exception;
}

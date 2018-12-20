import java.rmi.*;

public interface ProcessInterface extends Remote {
    public void Election(int callerMaxID, int callerID, int initID) throws Exception;
    public void Echo(int callerMaxID, int callerID, int initID) throws Exception;
    public void SendText(int callerID, String text) throws Exception;
}

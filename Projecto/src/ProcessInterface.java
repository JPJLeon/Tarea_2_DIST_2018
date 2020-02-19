import java.rmi.*;

// Interface remota del servidor
// Define los metodos que seran accedidos remotamente y sus argumentos.
public interface ProcessInterface extends Remote {
    public void Election(int callerMaxID, int callerID, int initID) throws Exception;
    public void Echo(int callerMaxID, int callerID, int initID) throws Exception;
    public void NotificarVecinos() throws Exception;
    public void ReducirCounter() throws Exception;
}

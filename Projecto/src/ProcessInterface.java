import java.rmi.*;
import java.util.concurrent.CompletableFuture;

public interface ProcessInterface extends Remote {
    default void Election(int callerMaxID, int callerID, int initID) throws Exception {
    }
}

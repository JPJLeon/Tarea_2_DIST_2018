import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.net.*;

public class Process extends UnicastRemoteObject implements ProcessInterface {
    String ID;
    String[] neighborID;
    String[] elected;

    int PORT = 9990;

    Registry registry = LocateRegistry.createRegistry(PORT);


    public Process(String ID) throws RemoteException{
        this.ID = ID;

        try {
        } catch(Exception e){throw new RemoteException("can't get inet address.");}
    }

    public void Election(String callerID) throws RemoteException{
        System.out.print("Me han llamado :O");
    }

}



/*

Los Procesos hacen llamadas de Election a sus vecinos.
Comunicacion via llamadas remotas.

subClase ProcessRMIServer


public void Election(String callerID):
    String[] elected;
    Por neighborIDs not elected;
        Proccess neighbor = GetRMIProccess(idVecinos) //Hace un naminglookup segun el String ID
        Elected.push(neighborIDs)
        neighbor.Election(self.ID)



Llamada Remota
Product c1 = (Product)Naming.lookup("rmi://yourserver.com/toaster")
 */


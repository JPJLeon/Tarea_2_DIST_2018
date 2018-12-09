import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.net.*;

public class Process extends UnicastRemoteObject implements ProcessInterface {
    public String ID;
    public String[] neighborID;
    public String[] elected;

    public Registry registry;
    public int port;


    public Process(String ID, String[] neighborID) throws RemoteException{
        System.setSecurityManager(new RMISecuritymanager());
        this.ID = ID;
        this.neighborID = neighborID;
        this.port = 9990

        try {
            Naming.rebind("rmi://localhost/"+this.ID, this);
        } catch(Exception e){throw new RemoteException("can't get inet address.");}
    }

    public void Election(String callerID) throws RemoteException{
        System.out.print("Estoy Llamando !!");
        //Esto se DEBE hacer por cada vecino
        try {
            ProcessInterface neighborRmi = (ProcessInterface)(Naming.lookup("rmi://localhost/"+neighborID));
            neighborRmi.Election(ID);
        } catch (Exception e){e.printStackTrace();}

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


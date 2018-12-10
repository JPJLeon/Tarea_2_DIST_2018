import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.net.*;

public class Process extends UnicastRemoteObject implements ProcessInterface {
    public String ID;
    public String[] neighborID;
    public String[] elected;

    public static Registry registry;

    //Constructor, crea el servidor RMI de este proceso.
    public Process(String ID, String[] neighborID, int port) throws RemoteException{
        //Llamada al constructor y los metodos de la clase base (UnicastRemoteObject)
        super();
        this.ID = ID;
        this.neighborID = neighborID;
        try{
            LocateRegistry.createRegistry(port);
            Naming.rebind(ID, this);
        } catch(Exception e) {
            e.printStackTrace();
        }
        System.out.print("Proceso nuvo creado :)\n");
    }

    public void Election(String callerID) throws RemoteException{
        //Esto se DEBE hacer por cada vecino
        try {
            if (neighborID != null) {
                System.out.print(this.ID + ": Estoy Llamando !!\n");
                ProcessInterface neighborRmi = (ProcessInterface) (Naming.lookup(neighborID[0]));
                neighborRmi.Election(this.ID);
            } else {
                System.out.print(this.ID + ": No tengo vecinos - "+callerID+"\n");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

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


import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.net.*;

public class Process extends UnicastRemoteObject implements ProcessInterface {
    public int ID;
    public int[] neighborID;
    public int neighborMaxID;
    public boolean elected;
    public boolean echoing;
    public int maxID = -1;

    private ProcessInterface[] neighborRMI;

    //Inicializador, se crea el servidor RMI de este proceso.
    public Process(int ID, int[] neighborID) throws Exception{
        //Llamada al constructor y los metodos de la clase base (UnicastRemoteObject)
        super();
        this.ID = ID;
        this.neighborID = neighborID;
        int port = 1200 + ID;
        try{
            LocateRegistry.createRegistry(port);
            Naming.rebind(String.valueOf(ID),this);
        } catch (Exception e){e.printStackTrace();}
        System.out.print("Proceso nuevo creado :)\n");
    }

    //Election no esta terminado, no usar.
    public void Election(int callerMaxID, int callerID) throws Exception{
        if (!elected || callerMaxID > this.maxID){
            elected = true;
            if (callerMaxID > this.maxID){
                this.maxID = callerMaxID;
            }
            for (int i = 0; i < neighborID.length; i++) {
                if (neighborID[i] == callerID) {
                    continue;
                }
                System.out.print(this.ID + ": Estoy llamando a "+neighborID[i]+"!!\n");
                ProcessInterface neighborRmi = (ProcessInterface) (Naming.lookup(String.valueOf(neighborID[i])));
                neighborRmi.Election(this.maxID, this.ID);
            }
        } else {
            System.out.print(this.ID + "Creo que el coord. es "+this.maxID+"\n");
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


import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;

import java.net.*;

public class Process extends UnicastRemoteObject implements ProcessInterface {
    private int ID;
    private boolean elected;
    private boolean echoing;
    public int maxID = -1;
    public int maxIDcounter = 0;

    public int[] neighborID;
    private ProcessInterface[] neighborRMI;

    //Inicializador, se crea el servidor RMI de este proceso.
    public Process(int ID, int[] neighborID) throws Exception {
        //Llamada al constructor y los metodos de la clase base (UnicastRemoteObject)
        super();
        this.ID = ID;
        this.maxID = ID;
        this.neighborID = neighborID;
        int port = 1200 + ID;
        try {
            LocateRegistry.createRegistry(port);
            Naming.rebind(String.valueOf(ID), this);
        } catch (Exception e) { e.printStackTrace(); }
        System.out.print("Proceso " + ID + " nuevo creado\n");
    }

    public void lookForNeigh() throws Exception {
        this.neighborRMI = new ProcessInterface[neighborID.length];
        for (int i = 0; i < neighborID.length; i++) {
            //Obtenemos los RMI de nuestros vecinos para uso posterior.
            this.neighborRMI[i] = (ProcessInterface) Naming.lookup(String.valueOf(this.neighborID[i]));
        }
    }

    //ELECTION NO ESTA TERMINADA; NO USAR.
    //Algoritmo de mensajes de exploracion/eleccion.
    public void Election(int callerMaxID, int callerID, int initID) throws Exception {
        boolean newMax = false;
        if (callerMaxID > this.maxID) {
            //Tu ID maxima es mayor a la mia, le avisare a todos mis vecinos menos a ti.
            this.maxID = callerMaxID;
            for (int i = 0; i < neighborID.length; i++) {
                //No llamare a quien me llamo.
                if (neighborID[i] == callerID) {
                    continue;
                }
                System.out.print(this.ID + ": Mandando mensajes con nueva MaxID " + this.maxID + " a " + neighborID[i] + "\n");
                neighborRMI[i].Election(this.maxID, this.ID, initID);
            }
        } else if (callerMaxID < this.maxID){
            //Tu ID maxima es menor a la mia, le avisare a todos.
            maxIDcounter = 0;
            for (int i = 0; i < neighborID.length; i++){
                System.out.print(this.ID + ": Mandando mensajes con mi MaxID " + this.maxID + " a " + neighborID[i] + "\n");
                neighborRMI[i].Election(this.maxID,this.maxID, initID);
            }
        }
        if (callerMaxID == this.maxID){ //Si callerMaxID == this.maxID
            maxIDcounter += 1;
            System.out.print(this.ID + ": Me ha llamado " + callerID +" con mi misma maxID " + callerMaxID +", cuento "+maxIDcounter+"\n");
            if (maxIDcounter == neighborID.length){
                System.out.print(this.ID+ ": CREO que todos concordamos en "+ this.maxID +"\n");
            }
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


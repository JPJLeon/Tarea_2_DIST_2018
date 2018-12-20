import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.Timer;
import java.util.TimerTask;

import java.net.*;

public class Process extends UnicastRemoteObject implements ProcessInterface {
    private int ID;
    private boolean elected;
    private boolean echoing;
    public int maxID = -1;
    private int maxIDcounter = 0;
    private int echoCounter = 0;

    private int aliveCounter = 0;
    private boolean escuchando = false;
    private boolean candidato;
    public int[] neighborID;
    public String rutaArchivoCifrado;
    public String ipServidor;
    private ProcessInterface[] neighborRMI;

    //Inicializador, se crea el servidor RMI de este proceso.
    public Process(int ID, int[] neighborID, boolean candidato) throws Exception {
        //Llamada al constructor y los metodos de la clase base (UnicastRemoteObject)
        super();
        this.ID = ID;
        this.maxID = ID;
        this.neighborID = neighborID;
        this.candidato = candidato;
        this.echoing = false;

        int port = 1200 + ID;
        try {
            LocateRegistry.createRegistry(port);
            Naming.rebind(String.valueOf(ID), this);
        } catch (Exception e) { e.printStackTrace(); }
        System.out.print("Proceso " + ID + " nuevo creado\n");

    }

    //constructor proceso candidato inicial
    public Process(int ID, int[] neighborID, boolean candidato, String rutaArchivoCifrado, String ipServidor) throws Exception {
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
        Election(-1, this.ID, this.ID);
        //notificar al resto del proceso representante

        //createTimerTaskRepresentante(neighborRMI, neighborID, ID);

    }

    public void lookForNeigh() throws Exception {
        this.neighborRMI = new ProcessInterface[neighborID.length];
        for (int i = 0; i < neighborID.length; i++) {
            //Obtenemos los RMI de nuestros vecinos para uso posterior.
            this.neighborRMI[i] = (ProcessInterface) Naming.lookup(String.valueOf(this.neighborID[i]));
        }

    }

    //Algoritmo de mensajes de exploracion/eleccion.
    public void Election(int callerMaxID, int callerID, int initID) throws Exception {
        echoCounter = 0;
        boolean newMax = false;
        lookForNeigh();
        if (callerMaxID > this.maxID) {
            //Tu ID maxima es mayor a la mia, le avisare a todos mis vecinos menos a ti.
            System.out.print(this.ID + ": Proceso " +callerID + " me ha mandado una nueva maxID: " + callerMaxID +"\n");
            this.maxID = callerMaxID;

            for (int i = 0; i < neighborID.length; i++) {
                //No llamare a quien me llamo.
                if (neighborID[i] == callerID) {
                    continue;
                }
                System.out.print(this.ID + ": Mandando mensajes con nueva MaxID " + this.maxID + " a " + neighborID[i] + "\n");
                neighborRMI[i].Election(this.maxID, this.ID, initID);
                if(this.maxID != callerMaxID) break;
            }
        } else if (callerMaxID < this.maxID){
            //Tu ID maxima es menor a la mia, le avisare a todos.
            System.out.print(this.ID + ": Proceso " + callerID + " me ha mandado una maxID: " + callerMaxID +", la mia es: " +this.maxID + "\n"); //por lo que se la mando a todos
            maxIDcounter = 0;
            for (int i = 0; i < neighborID.length; i++){
                System.out.print(this.ID + ": Mandando mensajes con mi MaxID " + this.maxID + " a " + neighborID[i] + "\n");
                neighborRMI[i].Election(this.maxID,this.ID, initID);
                if(this.maxID != this.ID) break;
            }
        } else if (callerMaxID == this.maxID){ //Si callerMaxID == this.maxID
            maxIDcounter += 1;
            System.out.print(this.ID + ": Me ha llamado " + callerID +" con mi misma maxID " + callerMaxID +", cuento "+maxIDcounter+"\n");

            if (maxIDcounter == neighborID.length){
                //Creo que concuerdo con mis vecinos, mandare Echos
                this.echoing = true;
                System.out.print(this.ID+ ":Comunicare que CREO que todos concordamos en "+ this.maxID +"\n");
                for (int i = 0; i < neighborID.length; i++) {
                    neighborRMI[i].Echo(this.maxID, this.ID, initID);
                }
            }
        }
    }

    //Solo una Election deberia poder crear un nuevo Echo.
    //Pero multiples Echos pueden crear otros Echos en nodos Electos.
    public void Echo(int callerMaxID, int callerID, int initID) throws Exception{
        if(neighborID.length == this.maxIDcounter && echoCounter < this.neighborID.length){
            //Un vecino me mando un Echo, lo recordare si todos mis vecinos me respondieron eleccion.
            echoCounter += 1;
            System.out.print(this.ID + ": He recibido un Echo de " + callerID +" cuento " + this.echoCounter +"\n");
        }

        //Si todos mis vecinos me hicieron Echo y yo soy el init, tenemos el maxID.
        if (this.ID == initID && this.echoCounter == this.neighborID.length){
            System.out.print(this.ID + ": El representante es " + callerMaxID +"\n");
        }
        //Si todos mis vecinos me mandaron un elect, y he recibido echo de todos mis vecinos
        //menos de 1, entro en Echo.
        if (maxIDcounter == neighborID.length && echoCounter == neighborID.length-1){
            if (callerMaxID == this.maxID) {
                System.out.print(this.ID + ": Concuerdo con el Echo de MaxID: " + this.maxID +"\n");
                for (int i = 0; i < neighborID.length; i++){
                    if (neighborID[i] == callerID){
                        continue;
                    }
                    neighborRMI[i].Echo(this.maxID, this.ID, initID);
                }
            }
        }
    }

    public void NotificarVecinos() throws Exception{
        this.aliveCounter +=1;
        System.out.println(this.ID + ": proceso ha sido notificado por el representante, aumentando el contador a: " + this.aliveCounter);
    }

    public void ReducirCounter() throws Exception{
        this.aliveCounter -=1;
        System.out.println(this.ID + ": ha pasado algo de tiempo, reduciendo mi contador a: " + this.aliveCounter);

        if(aliveCounter < 0){
            Election(-1, this.ID, this.ID);
        }

    }

    //esto debe ser ejectutado por el representante una vez escogido y notificado
    public void createTimerTaskRepresentante(ProcessInterface[] neighborRMI, int[] neighborID, int ID) throws Exception{
        try {
            TimerTask timerTask = new TimerTask(){
                public void run(){ //Ejecucion
                    for(int i = 0; i < neighborRMI.length; i++){
                        System.out.println(ID + ": notificando al proceseo " + neighborID[i] + " de que sigo vivo");
                        try{ neighborRMI[i].NotificarVecinos();}
                        catch(Exception e){e.printStackTrace();}
                    }
                }
            };
            Timer timer = new Timer();
            //Inicia la tarea, desde 0seg, cada 3seg
            timer.scheduleAtFixedRate(timerTask, 0, 5000);
        } catch(Exception ex) {ex.printStackTrace();
        }

    }

    //esto debe ser ejecutado por los vecinos del representante una vez escogido y notificado
    public void createTimerTaskVecino(ProcessInterface interfazPadre){
        try {
            TimerTask timerTask = new TimerTask(){
                public void run(){ //Ejecucion
                    try{interfazPadre.ReducirCounter();}
                    catch (Exception e){e.printStackTrace();}
                }
            };
            Timer timer = new Timer();
            //Inicia la tarea, desde 0seg, cada 3seg
            timer.scheduleAtFixedRate(timerTask, 0, 10000);
        } catch(Exception ex) {ex.printStackTrace();
        }
    }

} //Class end


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


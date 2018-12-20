import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;

public class Process extends UnicastRemoteObject implements ProcessInterface {
    private int ID;
    public int maxID = -1;
    public boolean Representative;
    private List<Integer> max_received_neighbors = new ArrayList<Integer>(); //Lista de los vecinos que ME ENVIARON el maxID
    private List<Integer> max_send_neighbors = new ArrayList<Integer>(); //Lista de los vecinos que LE ENVIE el maxID
    private boolean text_received = false;


    private boolean initiator;
    public int[] neighborID;
    public String rutaArchivoCifrado;
    public String ipServidor;
    private ProcessInterface[] neighborRMI;

    //Inicializador, se crea el servidor RMI de este proceso.
    public Process(int ID, int[] neighborID, boolean initiator) throws Exception {
        //Llamada al constructor y los metodos de la clase base (UnicastRemoteObject)
        super();
        this.ID = ID;
        this.maxID = ID;
        this.neighborID = neighborID;
        this.initiator = initiator;
        this.Representative = false;
        int port = 1200 + ID;
        try {
            LocateRegistry.createRegistry(port);
            Naming.rebind(String.valueOf(ID), this);
        } catch (Exception e) { e.printStackTrace(); }
        System.out.print("Proceso " + ID + " nuevo creado\n");
    }

    //constructor proceso candidato inicial
    //NO TERMINADA
    public Process(int ID, int[] neighborID, boolean initiator, String rutaArchivoCifrado, String ipServidor) throws Exception {
        //Llamada al constructor y los metodos de la clase base (UnicastRemoteObject)
        super();
        this.ID = ID;
        this.maxID = ID;
        this.neighborID = neighborID;
        this.initiator = initiator;
        this.rutaArchivoCifrado = rutaArchivoCifrado;
        this.ipServidor = ipServidor;
        this.Representative = false;
        int port = 1200 + ID;
        try {
            LocateRegistry.createRegistry(port);
            Naming.rebind(String.valueOf(ID), this);
        } catch (Exception e) { e.printStackTrace(); }
        System.out.print("Proceso " + ID + " creado\n");
        Election(-1, this.ID, this.ID);

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
        lookForNeigh();
        //Tu ID max es mayor a la mia, le avisare a todos mis vecinos menos a ti.
        if (callerMaxID > this.maxID) {
            System.out.print(this.ID + ": Proceso " +callerID + " me ha mandado una nueva MaxID:" + callerMaxID +"\n");
            max_received_neighbors = new ArrayList<Integer>(); //Lista vacia de los vecinos que ME ENVIARON el maxID
            max_send_neighbors = new ArrayList<Integer>(); //Lista vacia de los vecinos que LE ENVIE el maxID
            this.maxID = callerMaxID;

            for (int i = 0; i < neighborID.length; i++) {
                //No llamare a quien me llamo.
                if (neighborID[i] == callerID) {
                    max_received_neighbors.add(callerID); // Guardo quien me envio nuevo maxID
                    if(max_received_neighbors.size() == neighborID.length){
                        //Comienzo Eco
                        System.out.print(this.ID+ ": Todos mis vecinos me enviaron el mismo MaxID\n");
                        System.out.print(this.ID+ ": Comienzo ECO con MaxID:"+ this.maxID +"\n");
                        for (int j = 0; j < neighborID.length; j++) {
                            neighborRMI[j].Echo(this.maxID, this.ID, initID);
                        }
                        break;
                    }
                    continue;
                }
                max_send_neighbors.add(neighborID[i]);
                System.out.print(this.ID + ": Mandando nueva MaxID:" + this.maxID + " a Proceso " + neighborID[i] + "\n");
                neighborRMI[i].Election(this.maxID, this.ID, initID);
                //if(this.maxID != callerMaxID) break;
            }
        }
        //Tu ID maxima es menor a la mia, le avisare a todos los vecinos, MENOS a los que ya les avise
        else if (callerMaxID < this.maxID){
            if(!this.initiator){
                System.out.print(this.ID + ": Proceso " + callerID + " me ha mandado una MaxID:" + callerMaxID +" menor, la mia es: " +this.maxID + "\n"); //por lo que se la mando a todos
            }
            for (int i = 0; i < neighborID.length; i++){
                // Si quien me llamo me envio el maxID o ya se lo envie a este vecino, no le envio mi maxID
                if(max_received_neighbors.contains(neighborID[i]) || max_send_neighbors.contains(neighborID[i])){
                   continue;
                }
                System.out.print(this.ID + ": Mandando mensaje con mi MaxID:" + this.maxID + " a Proceso " + neighborID[i] + "\n");
                neighborRMI[i].Election(this.maxID,this.ID, initID);
                //if(this.maxID != this.ID) break;
            }
        }
        //Si callerMaxID == this.maxID se aumenta en 1 el contador, cuando el contador es igual al nro de vecinos, inicia ECO
        else if (callerMaxID == this.maxID){
            max_received_neighbors.add(callerID); // Guardo quien me envio nuevo maxID
            System.out.print(this.ID + ": Proceso " + callerID +" ha mandado mi misma MaxID:" + callerMaxID +", cuento " + max_received_neighbors.size() + "\n");

            if (max_received_neighbors.size() == neighborID.length){
                //Comienzo Eco
                System.out.print(this.ID+ ":Todos mis vecinos me enviaron el mismo MaxID, comienzo ECO con MaxID:"+ this.maxID +"\n");
                for (int i = 0; i < neighborID.length; i++) {
                    neighborRMI[i].Echo(this.maxID, this.ID, initID);
                }
            }
        }
    }

    //Retorna el MaxID al futuro representante
    public void Echo(int callerMaxID, int callerID, int initID) throws Exception{
        // Si el MaxID recibido es menor al que poseo, anulo el eco
        if(callerMaxID < this.maxID){
            return;
        }

        // Si el MaxID recibido es mayor al que poseo, esto NO deberia pasar
        else if (callerMaxID > this.maxID){
            System.out.print(this.ID + ": Error en el algoritmo \n");
        }

        // Si el MaxID recibido es igual al que poseo, continuo haciendo ECO hasta llegar al representante

        if(callerMaxID == this.maxID) {
            for(int i = 0; i < neighborID.length; i++){
                // Si recibi un ECO de un proceso vecino, NO le envio el ECO
                if(max_send_neighbors.contains(neighborID[i])){
                    continue;
                }
                if(callerMaxID == this.ID){
                    Representative = true;
                    System.out.print(this.ID + ": Soy el Representante, arrodillense!!! \n");
                    return;
                }
                System.out.print(this.ID + ": Proceso " + callerID + " me envio ECO de MaxID:" + this.maxID + "\n");
                System.out.print(this.ID + ": Propago ECO de MaxID:" + this.maxID +" , a Proceso " + neighborID[i] + "\n");
                neighborRMI[i].Echo(this.maxID, this.ID, initID);
            }
        }
    }

    //Crea timer para enviar texto en caso de ser el representante
    public void timerRepresentative() throws Exception {
        TimerTask timerTask = new TimerTask(){
            public void run(){ //Ejecucion
                try {
                    if(Representative){
                        Representative rep = new Representative(ipServidor, rutaArchivoCifrado);
                        rep.key = rep.search_key();
                        System.out.print("La llave publica es: \n" + rep.key + "\n");
                        try {
                            rep.line = rep.read_file();
                            System.out.print("El texto cifrado es: \n" + rep.line + "\n");
                            rep.line = rep.decipher(rep.line, rep.key);
                            System.out.print("El texto descifrado es: \n" + rep.line + "\n");
                            SendText(ID, rep.line);
                            System.out.print("El texto se envio a los demas procesos");
                        } catch(Exception  e) {
                            e.printStackTrace();
                        }
                    }
                    Representative = false;
                }catch(Exception ex) {ex.printStackTrace();}
            }
        };
        Timer timer = new Timer();
        //Inicia la tarea, desde 5seg, cada 5seg
        timer.scheduleAtFixedRate(timerTask, 5000, 5000);
    }

    //Envio de mensaje descifrado a todos los procesos
    public void SendText(int callerID, String text) throws Exception{
        if(ID != callerID){
            // Si recibi un mensaje lo imprimo
            System.out.print(ID + ": El texto decifrado es: " + text + "\n");
        }
        if(!text_received){
            for(int i = 0; i < neighborID.length; i++){
                neighborRMI[i].SendText(ID, text);
            }
        }
        text_received = true;
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


import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class ProcessMain{
    public static void main(String[] args) throws IOException {
        //Otorga todos los permisos al proceso
        System.setProperty("java.security.policy", "policy");

        int ID = Integer.parseInt(args[0]);
        int maxID = ID;

        String[] tempNeighbor = args[1].split(","); //se separa la lista con los IDs vecinos
        int[] neighborID = new int[tempNeighbor.length];
        for(int i = 0; i < tempNeighbor.length; i++){
            neighborID[i] = Integer.parseInt(tempNeighbor[i]);
        }

        boolean initiator = Boolean.parseBoolean(args[2]);
        if(initiator){
            String rutaArchivoCifrado = args[3];
            String ipServidor = args[4];

            try {
                Process proceso = new Process(ID, neighborID, initiator, rutaArchivoCifrado, ipServidor);
                //Crea el tarea programada de los datos.
                TimerTask timerTask = new TimerTask(){
                    public void run(){ //Ejecucion
                        try {
                            if(proceso.Representative){
                                System.out.print("Soy el representante, comenzare la destruccion " + proceso.Representative + "\n");
                            }
                        }catch(Exception ex) {ex.printStackTrace();}
                    }
                };
                Timer timer = new Timer();
                //Inicia la tarea, desde 5seg, cada 5seg
                timer.scheduleAtFixedRate(timerTask, 5000, 5000);
            }
            catch (Exception e){
                e.printStackTrace();
            }

        } else {
            try {
                Process proceso = new Process(ID, neighborID, initiator);
                //Crea el tarea programada de los datos.
                TimerTask timerTask = new TimerTask(){
                    public void run(){ //Ejecucion
                        try {
                            if(proceso.Representative){
                                System.out.print("Soy el representante, comenzare la destruccion " + proceso.Representative + "\n");
                                proceso.Representative = false;
                            }
                        }catch(Exception ex) {ex.printStackTrace();}
                    }
                };
                Timer timer = new Timer();
                //Inicia la tarea, desde 5seg, cada 5seg
                timer.scheduleAtFixedRate(timerTask, 5000, 5000);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

import java.io.IOException;

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

        boolean candidato = Boolean.parseBoolean(args[2]);
        if(candidato){
            String rutaArchivoCifrado = args[3];
            String ipServidor = args[4];

            try {
                Process proceso = new Process(ID, neighborID, candidato, rutaArchivoCifrado, ipServidor);
            }
            catch (Exception e){
                e.printStackTrace();
            }

        } else {
            try {
                Process proceso = new Process(ID, neighborID, candidato);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

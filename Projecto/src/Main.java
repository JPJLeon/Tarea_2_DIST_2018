import java.rmi.*;

public class Main {
    public static void main(String[] args){
        //Otorga todos los permisos al proceso
        System.setProperty("java.security.policy", "policy");
        int[] neigh1 = {2,6};
        int[] neigh2 = {1,2};
        int[] neigh3 = {6,1};

        try {
            System.out.print(java.net.InetAddress.getLocalHost().getHostAddress()+"\n");
            Process process1 = new Process(1,neigh1);
            Process process2 = new Process(6,neigh2);
            Process process3 = new Process(2,neigh3);

            process2.Election(6, 6, 6);

            //Representante
            if(process1.ID == 20){

                System.out.print("");
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}

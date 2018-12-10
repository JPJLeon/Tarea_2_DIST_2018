import java.rmi.*;

public class Main {
    public static void main(String[] args){
        //Otorga todos los permisos al proceso
        System.setProperty("java.security.policy", "policy");

        String[] neigh1 = {"21"};
        String[] neigh2 = {"20","22"};
        try {
            System.out.print(java.net.InetAddress.getLocalHost().getHostAddress());
            Process process1 = new Process("20", neigh1,1099);
            Process process2 = new Process("21",null,1110);

            process1.Election(null);

            //Representante
            if(process1.ID == "20"){

                System.out.print("");
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}

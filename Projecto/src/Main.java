import java.rmi.*;

public class Main {
    public static void main(String[] args){
        System.setProperty("java.security.policy", "policy");
        int[] neigh1 = {21};
        int[] neigh2 = {20,22};
        int[] neigh3 = {21};

        try {
            System.out.print(java.net.InetAddress.getLocalHost().getHostAddress());
            Process process1 = new Process(20,neigh1);
            Process process2 = new Process(21,neigh2);
            Process process3 = new Process(22,neigh3);

            //process2.Election(0, process2.ID);

        } catch (Exception e){e.printStackTrace();}
    }
}

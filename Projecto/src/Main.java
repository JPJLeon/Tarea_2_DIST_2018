import java.rmi.*;

public class Main {
    public static void main(String[] args){
        //Otorga todos los permisos al proceso
        System.setProperty("java.security.policy", "policy");
        int[] neigh1 = {2,6,5};
        int[] neigh2 = {6,1};
        int[] neigh3 = {5,7};
        int[] neigh4 = {5};
        int[] neigh5 = {1,3,4};
        int[] neigh6 = {2,1};
        int[] neigh7 = {3};

        try {
            System.out.print(java.net.InetAddress.getLocalHost().getHostAddress()+"\n");
            Process process1 = new Process(1,neigh1);
            Process process2 = new Process(2,neigh2);
            Process process3 = new Process(3,neigh3);
            Process process4 = new Process(4,neigh4);
            Process process5 = new Process(5,neigh5);
            Process process6 = new Process(6,neigh6);
            Process process7 = new Process(7,neigh7);

            process1.lookForNeigh();
            process2.lookForNeigh();
            process3.lookForNeigh();
            process4.lookForNeigh();
            process5.lookForNeigh();
            process6.lookForNeigh();
            process7.lookForNeigh();

            process5.Election(-1, 5, 5);
            System.out.print(process1.maxID+" "+process2.maxID+" "+process3.maxID+" "+process4.maxID+" "+process5.maxID+" "+process6.maxID+" "+process7.maxID);

        } catch(Exception e){
        	e.printStackTrace();
        }
    }
}

import java.rmi.Naming;

public class Main {
    public static void main(String[] args){
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        System.out.print("Hola");
        String[] neigh1 = {"21"};
        try {
            Process process1 = new Process("20", neigh1);
            Process process2 = new Process("21", new String[0]);

        } catch (Exception e){e.printStackTrace();}
    }
}

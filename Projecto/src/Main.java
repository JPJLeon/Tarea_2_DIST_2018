public class Main {
    public static void main(String[] args){
        System.out.print("Hola");
        try {
            Process process1 = new Process("20");
            Process process2 = new Process("21");
        } catch (Exception e){e.printStackTrace();}
    }
}

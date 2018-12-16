import java.rmi.*;
import java.io.*;

public class Representative {

    public String host;
    public String file_path;
    public String key;
    public String line;
    public String group = "grupo_14";

    // Crea nueva instancia de Representante
    public Representative(String host, String file_path) {
        this.file_path = file_path;
        this.host = host;
    }

    // Ejecuta metodo remoto en Server para obtener key
    public String search_key() {
        try {
            InterfaceServer serverRMI = (InterfaceServer) Naming.lookup("//" + host + "/PublicKey");
            return serverRMI.getKey(group);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // Funcion decifra texto de archivo ejecutando metodo remoto en Server con la key respectiva
    public String decipher(String textcipher, String key) {
        try {
            InterfaceServer serverRMI = (InterfaceServer) Naming.lookup("//" + host + "/PublicKey");
            return serverRMI.decipher(group, textcipher, key);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // Funcion lee la primera linea del archivo ubicado en file_path (instanciado en el constructor)
    public String read_file() throws FileNotFoundException, IOException {
        FileReader file = new FileReader(file_path);
        BufferedReader buffer = new BufferedReader(file);
        line = buffer.readLine();
        buffer.close();
        file.close();
        return line;
    }

    // Busca la key para decifrar el msj
    public static void main(String[] args) {
        Representative rep = new Representative("10.10.2.214", "/home/asantiba/Tarea_2_DIST_2018/proyecto_linux/Archivos cifrados/cifrado_grupo_14.txt");
        rep.key = rep.search_key();
        System.out.print("La llave publica es: \n" + rep.key + "\n");
        try {
            rep.line = rep.read_file();
            System.out.print("El texto cifrado es: \n" + rep.line + "\n");
            rep.line = rep.decipher(rep.line, rep.key);
            System.out.print("El texto descifrado es: \n" + rep.line + "\n");

            System.out.print("El texto se envió a los demás procesos");
        } catch(Exception  e) {
            e.printStackTrace();
        }
    }
}


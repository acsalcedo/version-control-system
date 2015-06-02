/*
Clase Cliente de SCV distribuido
Dexripcion:
*/

import java.rmi.*;
import java.io.*;
public class Cliente {

    File[] archivosProtegidos = new File[10];
    int nroArchivos = 0;

    public boolean agregarArchivo(String ruta){
        //BufferedReader entrada = new BufferedReader(new FileReader(dirArchivo));

    }

    public void menu(){

    }


    public static void main(String[] args) {
        String host = null;
        int port =0;

        if (!((0 < args.length) && (args.length < 3))) {
            System.err.print("Parametros incorrectos: ");
            System.err.println("ClienteSMVD <hostName> <port>");
            System.exit(1);
        }
        host = args[0];
        port = Integer.parseInt(args[1]);

        OpClienteServidor c = (OpClienteServidor)
        Naming.lookup("rmi://" + host + ":" + port+ "/ServicioSCVD");
    }


}
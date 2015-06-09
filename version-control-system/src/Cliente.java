/**
Clase Cliente de SCV distribuido
Se implementa el Cliente. Se agrega los archivos y directorios sujetos
    a control de versiones y se envia por RMI al servidor principal.
Ejecución : java Cliente <ndServidor> <NroPuerto>.
El ndServidor, por pruebas es localhost.
@author Daniel Leones 09-10977
*/

import java.rmi.*;
import java.io.*;
import java.util.*;
public class Cliente {

    private Coleccion archivosProtegidos;
    String ndServidor;
    int puerto;

    public Cliente(String ndServidor, int puerto) {
        this.ndServidor = ndServidor;
        this.puerto = puerto;
    }

    public boolean agregarDirectorioProtegido(String camino) {
        archivosProtegidos = new Coleccion(camino);
        return true;
    }

    public boolean agregarArchivo(String nombreArchivo) {
        if (archivosProtegidos != null){
            Documento doc = new Documento(nombreArchivo);
            archivosProtegidos.agregarDocumento(doc);
            return true;
        }
        return false;
    }

    public Coleccion obtArchivosProtegidos() {
        return archivosProtegidos;
    }

    public static void main(String[] args) {
        String host = null;
        int puerto = 0;

        if (!((0 < args.length) && (args.length < 3))) {
            System.err.print("Parametros incorrectos: ");
            System.err.println("ClienteSMVD <hostName> <puerto>");
            System.exit(1);
        }

        try {
            host = args[0];
            puerto = Integer.parseInt(args[1]);
            Cliente cli = new Cliente(args[0],Integer.parseInt(args[1]));

            // Busca al objeto que ofrece el servicio con nombre
            // CalculatorService en el Registry que se encuentra en
            // el ndServidor <ndServidor> y puerto <port>

            OpClienteServidor operaciones = (OpClienteServidor)
            Naming.lookup("rmi://" +host+ ":" +puerto+ "/ServicioSCVD");

            String workingDirectory = System.getProperty("user.dir");
            // Hacer commit
            cli.agregarDirectorioProtegido("pruebassss");
            cli.agregarArchivo(workingDirectory +"/../pruebas/archivo1.txt");
            cli.agregarArchivo(workingDirectory +"/../pruebas/archivo2.txt");
            operaciones.commit(cli.obtArchivosProtegidos());

        } catch (Exception e) {
            System.out.println("Cliente Exception: "+e.getMessage());
        }

    }




}
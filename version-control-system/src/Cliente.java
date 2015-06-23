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

    public static void crearRepositorio(Coleccion documentos) {

        if (!documentos.coleccionVacia()) {

            Collection<Documento> archivos = documentos.obtDocumentos();
            String nomDirectorio = "clientes/" +documentos.obtNombreProyecto();

            File directorio = new File(nomDirectorio);

            if (directorio.exists())
                System.out.println("Ya existe el repositorio.");
            else {
                if (directorio.mkdirs())
                    System.out.println("Se creo la carpeta del repositorio.");
            }
            try {
                for (Documento doc: archivos) {

                    FileOutputStream salida = new FileOutputStream(nomDirectorio + '/'+doc.obtNombre());
                    salida.write(doc.obtContenidoByte());
                    salida.close();

                    System.out.println("Se creo el archivo: " + doc.obtNombre());
                }
            } catch (Exception e) {
                System.out.println("Cliente: " + e.getMessage());
            }
        } else
            System.out.println("El repositorio esta vacio.");

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
            // Coleccion en el Registry que se encuentra en
            // el ndServidor <ndServidor> y puerto <port>

            OpClienteServidor operaciones = (OpClienteServidor)
            Naming.lookup("rmi://" +host+ ":" +puerto+ "/ServicioSCVD");

            String workingDirectory = System.getProperty("user.dir");
            String repository = "pruebas";

            // Hacer commit
            
            cli.agregarDirectorioProtegido("mitest");
            cli.agregarArchivo(workingDirectory +"/pruebas/archivo1.txt");
            cli.agregarArchivo(workingDirectory +"/pruebas/archivo2.txt");
            System.out.println("mi directorio: " +workingDirectory);
            operaciones.commit(cli.obtArchivosProtegidos());

            //Coleccion docs = operaciones.checkout(repository);

            //crearRepositorio(docs);

        } catch (Exception e) {
            System.out.println("Cliente Exception: "+e.getMessage());
        }

    }




}
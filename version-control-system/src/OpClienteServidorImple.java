
import java.util.*;
import java.io.*;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OpClienteServidorImple
    extends
    java.rmi.server.UnicastRemoteObject
    implements OpClienteServidor {

    private Coleccion temp; // copia de coleccion enviada por commit
    private OpServidorServidor operacionesInternas;
    private int nroReplicas;

    public OpClienteServidorImple(OpServidorServidor operacionesInternas)
            throws java.rmi.RemoteException {
        super();
        temp = new Coleccion();
        this.operacionesInternas = operacionesInternas;
    }

    /*
        Metodo commit
        Envia una Coleccion para almacenarla provisionalmente en temp.
       Por otra parte, crea una directorio y escribe los archivos enviados
       en èl.
    */
    public boolean commit(Coleccion archivos_enviar)
        throws java.rmi.RemoteException {

        Collection<Documento> archivos = archivos_enviar.obtDocumentos();
        String nomDirectorio = archivos_enviar.obtNombreProyecto();
        byte[] buffer;

        if (archivos_enviar != null) {
            File directorio = new File(nomDirectorio);
            if (!directorio.exists()) {
                if (directorio.mkdir()) {
                    System.out.println("Directorio: "
                                       +nomDirectorio+ " creado");
                } else {
                    System.out.println("No se pudo crear directorio: "
                                       +nomDirectorio);
                }
            }
            //System.out.println("Que pasa");
            operacionesInternas.replicarGrupal(archivos_enviar);
            //System.out.println("Que paso algo?");
           try {
                // Agrega los archivos contenidos en la coleccion
                // Escribe los documentos en el sistema de archivos del servidor
                for ( Documento doc : archivos ) {
                    temp.agregarDocumento(doc);
                    System.out.println(nomDirectorio+'/'+doc.obtNombre());
                    File nuevo = new File(nomDirectorio+'/'+doc.obtNombre());

                    //directorio.renameTo(nuevo);
                    BufferedOutputStream salida = new
                      BufferedOutputStream( new FileOutputStream(nuevo));

                    buffer = doc.obtContenidoByte();

                    salida.write(buffer,0,buffer.length);
                    salida.flush();
                    salida.close();
                }
                temp.setNombreProyecto(archivos_enviar.obtNombreProyecto());

                return true;
            } catch (Exception e) {
                System.out.println("ServPrincipal: " +e.getMessage());
            }
        }
        return false;
    }

    public Coleccion update(String nombreRepo) throws java.rmi.RemoteException {

        return operacionesInternas.buscarRepo(nombreRepo);

    }

<<<<<<< HEAD
    public String checkout(String nombreRepo) throws java.rmi.RemoteException {

        
        return operacionesInternas.buscarHost(nombreRepo);
        
         
//        Path path = Paths.get(nombreRepo);
//
//        if (!Files.exists(path)) {
//            System.out.println("El repositorio " + nombreRepo + " no existe.");
//            return null;
//        } else {
//
//            Coleccion docs = new Coleccion(nombreRepo);
//            File carpeta = new File(nombreRepo);
//            File[] archivos = carpeta.listFiles();
//
//            for (int i = 0; i < archivos.length; i++) {
//                if (archivos[i].isFile()) {
//                    String nombreArchivo = archivos[i].getName();
//                    System.out.println(nombreArchivo);
//                    docs.agregarDocumento(path.toString()+'/'+nombreArchivo);
//                }
//            }
//            return docs;
//        }
       // return null;
    }

    public void listarArchivos() throws java.rmi.RemoteException {

=======
    public Coleccion checkout(String nombreRepo)  throws java.rmi.RemoteException {

        return operacionesInternas.buscarRepo(nombreRepo);
    }


    public String listarArchivos(String nombreRepo) throws java.rmi.RemoteException {
        Path path = Paths.get(nombreRepo);
        String listaArch = "";

        if (!Files.exists(path)) {
            System.out.println("El repositorio " + nombreRepo + " no existe.");
            return null;
        } else {
            File carpeta = new File(nombreRepo);
            File[] archivos = carpeta.listFiles();
            listaArch = "Nombre de repositorio: " + nombreRepo;

            for (int i = 0; i < archivos.length; i++) {
                if (archivos[i].isFile()) {
                    listaArch += "\n" + archivos[i].getName();
                }
            }
            listaArch += "\n";  
            return listaArch;
        }
>>>>>>> multicast
    }

    /*
        Devuelve una copia de la ultima coleccion procesada
    */
    public Coleccion obtColeccion(){
        return temp;
    }

}


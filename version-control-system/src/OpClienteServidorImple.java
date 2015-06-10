
import java.util.*;
import java.io.*;

public class OpClienteServidorImple
    extends
    java.rmi.server.UnicastRemoteObject
    implements OpClienteServidor {

    private Coleccion temp; // copia de coleccion enviada por commit

    public OpClienteServidorImple() throws java.rmi.RemoteException {
        super();
        temp = new Coleccion();
    }

    /*
        Metodo commit
        Envia una Coleccion para almacenarla provisionalmente en temp.
       Por otra parte, crea una directorio y escribe los archivos enviados
       en èl.
    */
    public boolean commit(Coleccion archivos_enviar )
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

    public void update() throws java.rmi.RemoteException {

    }

    public void checkout()  throws java.rmi.RemoteException {

    }

    public void listarArchivos() throws java.rmi.RemoteException {

    }

    /*
        Devuelve una copia de la ultima coleccion enviada
    */
    public Coleccion obtColeccion(){
        return temp;
    }


}   


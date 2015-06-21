/*


*/

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.file.*;
import java.util.*;

public class ServAlmacenamiento {

    // DireccionIp Multicast por defecto "224.0.0.3"
    static InetAddress direccionIPMulticast;
    static MulticastSocket socketEscucha;

    public static boolean existeRepo(String nombreRepo) {
        
        Path path = Paths.get(nombreRepo);

        if (!Files.exists(path)) {
            System.out.println("El repositorio " + nombreRepo + " no existe.");
            return false;
        }
        return true;
    }
    public static boolean construirReplica(Coleccion archivos_recibidos) {
        String nomDirectorio = "repositorios";
        byte[] buffer;

        if (archivos_recibidos != null) {
            nomDirectorio += "/" + archivos_recibidos.obtNombreProyecto();
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
                for ( Documento doc : archivos_recibidos.obtDocumentos() ) {
                    System.out.println(nomDirectorio+'/'+doc.obtNombre());
                    File nuevo = new File(nomDirectorio+'/'+doc.obtNombre());

                    BufferedOutputStream salida = new
                      BufferedOutputStream( new FileOutputStream(nuevo));

                    buffer = doc.obtContenidoByte();

                    salida.write(buffer,0,buffer.length);
                    salida.flush();
                    salida.close();
                }
                return true;
            } catch (Exception e) {
                System.out.println("ServAlmacenamiento: " +e.getMessage());
            }
        }
        return false;
    }

    public static void main(String args[])
        throws UnknownHostException, InterruptedException {

        if (args.length < 1) {
            System.err.println("Parametros incorrectos. java ServAlmacenamiento"
                               +" <puerto> <direccionIPMulticast>");
            System.exit(1);
        }

        System.out.println("Nro Puerto: " +args[0]);
        int puerto = Integer.parseInt(args[0]);
        byte[] tamanoPaqueteEntrante = new byte[1000];

        DatagramPacket paqueteEntrante = new
            DatagramPacket(tamanoPaqueteEntrante,tamanoPaqueteEntrante.length);

        try {
            if (args.length != 2)
                direccionIPMulticast = InetAddress.getByName("224.0.0.3");

            socketEscucha = new MulticastSocket(puerto);
            socketEscucha.joinGroup(direccionIPMulticast);

            while (true) {
                socketEscucha.receive(paqueteEntrante);
                System.out.println("Recibiendo mensajes");
                String temp = new String(tamanoPaqueteEntrante, 0,
                                         tamanoPaqueteEntrante.length);
                //System.out.println("Longitud de mensaje: " +temp.trim());
                //System.out.printf("Longitud de mensaje (entero): %d\n", Integer.parseInt(temp.trim() ) );
                byte[] paquete = new byte[Integer.parseInt(temp.trim())];
                paqueteEntrante = new DatagramPacket(paquete, paquete.length);
                socketEscucha.receive(paqueteEntrante);

                // Convertir
                ByteArrayInputStream bs= new ByteArrayInputStream(paquete); // bytes es el byte[]
                ObjectInputStream is = new ObjectInputStream(bs);
                Object o = is.readObject();
                
                System.out.println(o.getClass().toString());
                                
                if (o.getClass() == String.class) {
                    
                    String nombreRepo = (String) o;
                    System.out.println(nombreRepo);
                    
                    boolean existe = existeRepo(nombreRepo);
                        
                    ByteArrayOutputStream bs2 = new ByteArrayOutputStream();
                    ObjectOutputStream os = new ObjectOutputStream (bs2);
                    os.writeObject(existe); 
                    os.close();
                    byte[] msg =  bs2.toByteArray();

                    int num =  msg.length;

                    byte[] longitud = Integer.toString(num).getBytes();
                    DatagramPacket longitudEnvio = new DatagramPacket(longitud, longitud.length,
                                    direccionIPMulticast, puerto);
                    DatagramPacket existePaquete = new DatagramPacket(msg,msg.length,
                                                direccionIPMulticast, puerto);
                    
                    socketEscucha.send(longitudEnvio);
                    socketEscucha.send(existePaquete);


                    System.out.println("existe: " + existe);
                    
 
                } else if (o.getClass() == Coleccion.class){
                    Coleccion archivos = (Coleccion) o;
                    construirReplica(archivos);

                } else {
                    System.out.println("Ignorar");
                }
                
                is.close();
            }
        } catch(Exception e) {
           System.out.println("ServAlmacenamiento :"+e.getMessage());
                e.printStackTrace();
        }
   }

}
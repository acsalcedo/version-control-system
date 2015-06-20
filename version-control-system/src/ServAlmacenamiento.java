/*


*/

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.*;

public class ServAlmacenamiento {

    // DireccionIp Multicast por defecto "224.0.0.3"
    static InetAddress direccionIPMulticast;
    static MulticastSocket socketEscucha;

    public static boolean construirReplica(ArrayList<Documento> archivos_recibidos) {
        String nomDirectorio = "pruebasAlmacenamiento";
        byte[] buffer;

        if (archivos_recibidos != null) {
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
                for ( Documento doc : archivos_recibidos ) {
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
        byte[] tamanoPaqueteEntrante = new byte[256];

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
                System.out.println("Longitud de mensaje: " +temp.trim());
                //System.out.printf("Longitud de mensaje (entero): %d\n", Integer.parseInt(temp.trim() ) );
                byte[] paquete = new byte[Integer.parseInt(temp.trim())];
                paqueteEntrante = new DatagramPacket(paquete, paquete.length);
                socketEscucha.receive(paqueteEntrante);

                // Convertir
                ByteArrayInputStream bs= new ByteArrayInputStream(paquete); // bytes es el byte[]
                ObjectInputStream is = new ObjectInputStream(bs);
                ArrayList<Documento> archivos =
                    (ArrayList<Documento>) is.readObject();
                is.close();

                // Hacer la replica
                construirReplica(archivos);
            }
        } catch(Exception e) {
           System.out.println("ServAlmacenamiento :"+e.getMessage());
                e.printStackTrace();
        }
   }

}
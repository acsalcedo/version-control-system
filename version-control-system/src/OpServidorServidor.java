import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.*;


public class OpServidorServidor {

    // DireccionIp Multicast por defecto "224.0.0.3"
    private InetAddress direccionIpMulticast;
    private MulticastSocket socket;
    private int puerto;

    OpServidorServidor(int puerto, String direccionIpMulticast) {

        try {
            this.puerto = puerto;
            this.direccionIpMulticast = InetAddress.getByName(direccionIpMulticast);
            socket = new MulticastSocket(puerto);
            socket.joinGroup(this.direccionIpMulticast);
        } catch (Exception e) {
            System.out.println("OpServidorServidor :"+ e.getMessage());
            e.printStackTrace();
        }
    }

    /*
       Metodo replicar
       Envia una Coleccion para almacenarla provisionalmente en temp.
       Por otra parte, crea una directorio y escribe los archivos enviados
       en el.
    */
    public boolean replicarGrupal(Coleccion archivos_enviar) {

        byte[] archivosTransformados;
        DatagramPacket mensaje, longitud_a_enviar;

        try {
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream (bs);
            os.writeObject(archivos_enviar);  // this es de tipo DatoUdp
            os.close();
            archivosTransformados =  bs.toByteArray(); // devuelve byte[]

            // Receive the information and print it.
            int num =  archivosTransformados.length;
            byte[] longitud = Integer.toString(num).getBytes();
            longitud_a_enviar = new DatagramPacket(longitud, longitud.length,
                                        direccionIpMulticast, puerto);
            mensaje = new DatagramPacket(archivosTransformados,
                            archivosTransformados.length,
                                        direccionIpMulticast, puerto);

            System.out.println("Replicacion en proceso....");
            socket.send(longitud_a_enviar);
            socket.send(mensaje);
            return true;
        } catch (IOException ex) {
          ex.printStackTrace();
        }

        return false;
    }
    
    public InetAddress buscarHost(String nombreRepo) {
        
        String repo = "repositorios/" + nombreRepo;
        try {
            
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream (bs);
            os.writeObject(repo);  // this es de tipo DatoUdp
            os.close();
            byte[] str =  bs.toByteArray(); // devuelve byte[]

            // Receive the information and print it.
            int num =  str.length;
            byte[] longitud = Integer.toString(num).getBytes();
            DatagramPacket longitud_a_enviar = new DatagramPacket(longitud, longitud.length,
                                        direccionIpMulticast, puerto);
            DatagramPacket msj = new DatagramPacket(str,str.length,
                                                    direccionIpMulticast, puerto);
            socket.send(longitud_a_enviar);
            socket.send(msj);
            
            byte[] buf = new byte[1000];
            DatagramPacket recv = new DatagramPacket(buf, buf.length,direccionIpMulticast,puerto);
            System.out.println("Esperando...");
            //socket.receive(recv);
            socket.receive(recv);
            
            System.out.println("Recibi host: ");
            
            System.out.println(recv.getAddress());
            
            return recv.getAddress();
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
        
    }
    

}

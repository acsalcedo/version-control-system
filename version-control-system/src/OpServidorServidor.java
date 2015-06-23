import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.*;

/*
    Lista de aspectos por implementar
    - El socket multicast es UDP. Hacer acuse de recibo.
    -

*/
public class OpServidorServidor extends Thread {

    // DireccionIp Multicast por defecto "224.0.0.3"
    private InetAddress direccionIpMulticast;
    private MulticastSocket socket;
    private int puerto;
    private int nroReplicas;
    private HashMap<Integer,String> listaServAlmacen; 
    //Se puede adaptar este HashMap para mantener una pista de lo que 
    // de la carga de cada servidor

    OpServidorServidor(int puerto, String direccionIpMulticast, int nroReplicas) {
        try {
            this.nroReplicas = nroReplicas;
            this.puerto = puerto;
            this.direccionIpMulticast =
                InetAddress.getByName(direccionIpMulticast);
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

        ArrayList<Documento> archivos = new ArrayList<Documento>(
                                archivos_enviar.obtDocumentos());
        String nomDirectorio = archivos_enviar.obtNombreProyecto();
        byte[] archivosTransformados;
        DatagramPacket mensaje, longitud_a_enviar,
                nombreDirectorioAlmacenamiento, orden;

        try {
            System.out.println("Replicacion en proceso....");
            // Serializar el objeto ArrayList<Documento>
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream (bs);
            os.writeObject(archivos);
            os.close();
            archivosTransformados =  bs.toByteArray();
            mensaje = new DatagramPacket(archivosTransformados,
                            archivosTransformados.length,
                                        direccionIpMulticast, puerto);
            // Enviar orden REPLICA
            String ordenAEnviar = "REPLICA";
            orden = new DatagramPacket(
                    ordenAEnviar.getBytes(),
                    ordenAEnviar.length(),
                    direccionIpMulticast,
                    puerto);

            socket.send(orden);

            // Construir paquete con el nombre del proyecto
            nombreDirectorioAlmacenamiento = new DatagramPacket(
                    nomDirectorio.getBytes(),nomDirectorio.length(),
                    direccionIpMulticast,puerto);
            socket.send(nombreDirectorioAlmacenamiento);

            // Envia una paquete con el tamaño del objeto ArrayList<Documento>
            int num =  archivosTransformados.length;
            byte[] longitud = Integer.toString(num).getBytes();
            longitud_a_enviar = new DatagramPacket(longitud, longitud.length,
                                        direccionIpMulticast, puerto);

            socket.send(longitud_a_enviar);
            socket.send(mensaje);
            return true;
        } catch (IOException ex) {
          ex.printStackTrace();
        }
        return false;
    }



    public void agregarServAlmacen() {
        byte[] buzonEstandar = new byte[256];
        byte[] buzonEstandar2 = new byte[256];
        String mensaje, temporal;

        if (listaServAlmacen == null) {
            listaServAlmacen = new HashMap<Integer,String>();
        }

        while (true) {
            DatagramPacket paqueteEntrante = new
                DatagramPacket(buzonEstandar,buzonEstandar.length);
            try {
                socket.receive(paqueteEntrante);
                mensaje = new String(buzonEstandar, 0,
                                         buzonEstandar.length);
                mensaje = mensaje.trim();
                //System.out.println("Pruebassa Notifca: " + mensaje);
                if (mensaje.equals("AGREGAR")) {
                    DatagramPacket paqueteEntrante2 = new
                        DatagramPacket(buzonEstandar2,buzonEstandar2.length);
                    socket.receive(paqueteEntrante2);
                    mensaje = new String(
                        buzonEstandar2, 0, buzonEstandar2.length);
                    mensaje = mensaje.trim();
                    System.out.println("Nuevo servidor detectado: " + mensaje);
                    int tamanio = listaServAlmacen.size();
                    listaServAlmacen.put(tamanio++,mensaje);
                }
            } catch(Exception e) {
                System.out.println("ServPrincipal :" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void run() {
        System.out.println("Esperando servidores de almacenamiento");
        // Adaptar para que reciba mensajes en general
        agregarServAlmacen();
    }

}

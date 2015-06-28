import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private int nroServidores = 0;
    //Posible problemas de concurrencia
    private HashMap<Integer,String> listaServAlmacen;
    private HashMap<String,Integer> cargaServAlmacen;

    OpServidorServidor(int puerto, String direccionIpMulticast, int nroReplicas, int nroServidores) {
        try {
            this.nroReplicas = nroReplicas;
            this.nroServidores = nroServidores;
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
        String[] servidorElegido;
        byte[] archivosTransformados;
        DatagramPacket mensaje, longitud_a_enviar,
                nombreDirectorioAlmacenamiento, orden;
        Random genNumRnd = new Random();
        int numAleatorio, tamanio;

        try {
            System.out.println("Replicacion en proceso....");
            // Selección de servidores
            tamanio = listaServAlmacen.size();

            //(int) (rnd.nextDouble() * cantidad_números_rango + término_inicial_rango)
            servidorElegido = new String[nroReplicas];
            for (int i = 0; i < nroReplicas ; i++ ) {
                numAleatorio = (int)( genNumRnd.nextDouble() * (tamanio - 1));
                servidorElegido[i] = listaServAlmacen.get(numAleatorio);
                int carga = cargaServAlmacen.get(servidorElegido[i]);
                cargaServAlmacen.put(servidorElegido[i],carga);
            }


            // Serializar el objeto ArrayList<Documento>
            for (int i = 0; i < nroReplicas; i++) {
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                ObjectOutputStream os = new ObjectOutputStream (bs);
                os.writeObject(archivos);
                os.close();
                archivosTransformados =  bs.toByteArray();
                mensaje = new DatagramPacket(archivosTransformados,
                                archivosTransformados.length,
                                            direccionIpMulticast, puerto);
                // Enviar orden REPLICA
                String ordenAEnviar = "REPLICA " + servidorElegido[i];
                System.out.println("Enviar a " + servidorElegido[i]);
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
            }
            return true;
        } catch (IOException ex) {
          ex.printStackTrace();
        }
        return false;
    }

    /*public String[] elegirReplicacion(String[] listaExcepciones) {
        int minimo = INTEGER.MIN_VALUE;
        String nombreServMenorCarga;
        String[] salida;
        int temp, j = 0;
        Collection<String> servidores = listaServAlmacen.values();
        salida = new String[nroReplicas];
        if (listaExcepciones[0] != null){
            for (String elem : servidores) {
                temp = cargaServAlmacen ServAlmacen.get(elem);
                if ( minimo > temp ) {
                    minimo = temp;
                    nombreServMenorCarga = elem;
                    for (int i = 0; i < listaExcepciones.length ; i++) {
                        if (!listaExcepciones[i].equals(nombreServMenorCarga)) {
                            salida[j] = nombreServMenorCarga;
                            j++;
                        }
                    }
                }
            }
        }
        return salida;
    }

    public String elegirReplicacionUnitario() {
        int minimo = INTEGER.MIN_VALUE;
        String nombreServMenorCarga, salida;
        int temp, j = 0;
        Collection<String> servidores = listaServAlmacen.values();
        salida = new String[nroReplicas];
            for (String elem : servidores) {
                temp = cargaServAlmacen ServAlmacen.get(elem);
                if ( minimo > temp ) {
                    minimo = temp;
                    nombreServMenorCarga = elem;
                    for (int i = 0; i < listaExcepciones.length ; i++) {
                        if (!listaExcepciones[i].equals(nombreServMenorCarga)) {
                            salida[j] = nombreServMenorCarga;
                            j++;
                        }
                    }
                }
            }
        }
        return salida;
    }*/
    
    public Coleccion buscarRepo(String nombreRepo) {
        
        try {
            String ordenAEnviar = "BUSCAR ";
            DatagramPacket orden = new DatagramPacket(
                                       ordenAEnviar.getBytes(),
                                       ordenAEnviar.length(),
                                       direccionIpMulticast,
                                       puerto);
            
            socket.send(orden);
            
            DatagramPacket nombreDirectorio = new DatagramPacket(
                                                  nombreRepo.getBytes(),
                                                  nombreRepo.length(),
                                                  direccionIpMulticast,
                                                  puerto);
            
            socket.send(nombreDirectorio);
            
            byte[] buf = new byte[256];
            DatagramPacket recv = new DatagramPacket(buf,buf.length,
                                                     direccionIpMulticast,
                                                     puerto);
            
            System.out.println("Esperando respuesta...");

            while (true) {
                socket.receive(recv);

                String mensaje = new String(buf,0,buf.length);
                mensaje = mensaje.trim();

                System.out.println("Mensaje: " + mensaje);

                switch (mensaje) {
                    case "EXISTE":

                        buf = new byte[256];
                        byte[] buffer = new byte[256];
                        DatagramPacket paqueteEntrante = new DatagramPacket(
                                                             buffer,buffer.length);
                        socket.receive(paqueteEntrante);
                        String msj = new String(buffer, 0,buffer.length);
                        msj = msj.trim();
                        System.out.println("Longitud de paquete de documentos: "
                                            + msj + " bytes");
                        byte[] paquete = new byte[Integer.parseInt(msj)];
                        paqueteEntrante = new DatagramPacket(paquete, paquete.length);

                        socket.receive(paqueteEntrante);
                        ByteArrayInputStream bs = new ByteArrayInputStream(paquete);
                        ObjectInputStream is = new ObjectInputStream(bs);
                        Coleccion archivos = (Coleccion) is.readObject();
                        is.close();

                        return archivos;
                    case "NEXISTE":
                        return null;
                    default:
                        break;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(OpServidorServidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OpServidorServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

        
    }

    public void agregarServAlmacen() {
        byte[] buzonEstandar = new byte[256];
        byte[] buzonEstandar2 = new byte[256];
        String mensaje, temporal;
        String[] ordenDestinatario;
        int tamanioLista;
        int servAlm = 0;

        if ((listaServAlmacen == null) && (cargaServAlmacen == null)) {
            listaServAlmacen = new HashMap<Integer,String>();
            cargaServAlmacen = new HashMap<String,Integer>();
        }

        while (servAlm < nroServidores) {
            DatagramPacket paqueteEntrante = new
                DatagramPacket(buzonEstandar,buzonEstandar.length);
            try {
                socket.receive(paqueteEntrante);
                mensaje = new String(buzonEstandar, 0,
                                         buzonEstandar.length);
                mensaje = mensaje.trim();
                ordenDestinatario = mensaje.split(" ");
                if (ordenDestinatario[0].equals("AGREGAR")) {
                    System.out.println("Nuevo servidor detectado: "
                                        + ordenDestinatario[1]);
                    tamanioLista = listaServAlmacen.size();
                    // La lista comienza en cero
                    listaServAlmacen.put(tamanioLista,ordenDestinatario[1]);
                    cargaServAlmacen.put(ordenDestinatario[1],0);
                }
            } catch(Exception e) {
                System.out.println("ServPrincipal :" + e.getMessage());
                e.printStackTrace();
            }
            servAlm++;
        }
    }

    public void run() {
        System.out.println("Esperando servidores de almacenamiento");
        // Adaptar para que reciba mensajes en general
        agregarServAlmacen();
    }

}

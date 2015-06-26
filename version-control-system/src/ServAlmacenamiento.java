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
    static HashMap<Integer,String> listaServAlmacen;
    static String nombreServidor;
    static int puerto;

    public static boolean construirReplica(
        ArrayList<Documento> archivos_recibidos, String nomDirectorio) {
        //String nomDirectorio = "pruebasAlmacenamiento";
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

    static boolean agregarOtrosServEnServicio(String nombreServ) {
        if (listaServAlmacen == null) {
            listaServAlmacen = new HashMap<Integer,String>();
        }

        int tamanio = listaServAlmacen.size();
        listaServAlmacen.put(tamanio++,nombreServ);
        return true;
    }

    static boolean notificarServicio() {
        String orden = "AGREGAR " + nombreServidor;
        DatagramPacket reporte;
        try {
            reporte = new DatagramPacket(orden.getBytes(), orden.length(),
                                direccionIPMulticast, puerto);
            socketEscucha.send(reporte);
        } catch(Exception e) {
            System.out.println("ServAlmacenamiento :"+e.getMessage());
            e.printStackTrace();
        }
        return true;
    }

    public static void main(String args[])
        throws UnknownHostException, InterruptedException {

        if (args.length < 2) {
            System.err.println("Parametros incorrectos. java ServAlmacenamiento"
                    +"<nombreServidor> <puerto> <direccionIPMulticast>");
            System.exit(1);
        }

        System.out.println("Nro Puerto en escucha: " + args[1]);
        puerto = Integer.parseInt(args[1]);
        nombreServidor = args[0];
        byte[] buzonEstandar = new byte[256], paquete;
        byte[] copiaBuzon = new byte[256];
        String mensaje, nombreProyecto, temporal;
        String[] ordenDestinario;



        DatagramPacket paqueteEntrante = new
            DatagramPacket(buzonEstandar,buzonEstandar.length);

        try {
            if (args.length < 3)
                direccionIPMulticast = InetAddress.getByName("224.0.0.3");
            else
                direccionIPMulticast = InetAddress.getByName(args[2]);

            socketEscucha = new MulticastSocket(puerto);
            socketEscucha.joinGroup(direccionIPMulticast);
            notificarServicio();

            /*TimerTask timerTask = new TimerTask()
            {
                public void run() {
                    notificarServicio();
                }
            };

            Timer timer = new Timer();
            // Notificar servicio cada minuto
            timer.scheduleAtFixedRate(timerTask, 0, 3600000);*/

            while (true) {
              // Recibe un primer paquete del tamaño completo de todos los
              // documentos.
              socketEscucha.receive(paqueteEntrante);
              mensaje = new String(buzonEstandar, 0,
                                       buzonEstandar.length);
              mensaje = mensaje.trim();
              //System.out.println("Debug: " + mensaje );
              ordenDestinario = mensaje.split(" ");
                switch (ordenDestinario[0]) {
                  case "REPLICA":
                    if ( ordenDestinario[1].equals(nombreServidor)){
                        System.out.println("Nuevo mensaje");
                        System.out.println("Tipo de orden: " + ordenDestinario[0]);
                    // Recibe el nombre del proyecto
                        buzonEstandar = new byte[256]; // Resetear buffer entrada
                        paqueteEntrante =
                            new DatagramPacket(buzonEstandar, buzonEstandar.length);
                        socketEscucha.receive(paqueteEntrante);
                        mensaje = new String(buzonEstandar, 0,
                                         buzonEstandar.length);
                        mensaje = mensaje.trim();

                        System.out.println("Nombre proyecto: " + mensaje);
                        nombreProyecto = mensaje;


                        // Recibe un primer paquete del tamaño completo de
                        // todos los documentos.
                        buzonEstandar = new byte[256]; // Resetear buffer entrada
                        paqueteEntrante =
                            new DatagramPacket(buzonEstandar, buzonEstandar.length);
                        socketEscucha.receive(paqueteEntrante);
                        mensaje = new String(buzonEstandar, 0,
                                         buzonEstandar.length);
                        mensaje = mensaje.trim();
                        System.out.println("Longitud de paquete de documentos: "
                                            + mensaje + " bytes");
                        paquete = new byte[Integer.parseInt(mensaje)];
                        paqueteEntrante =
                            new DatagramPacket(paquete, paquete.length);

                        // Recibe el paquete de documentos y los deserializa
                        socketEscucha.receive(paqueteEntrante);
                        ByteArrayInputStream bs =
                            new ByteArrayInputStream(paquete);
                        ObjectInputStream is = new ObjectInputStream(bs);
                        ArrayList<Documento> archivos =
                            (ArrayList<Documento>) is.readObject();
                        is.close();

                        // Hacer la replica
                        construirReplica(archivos, nombreProyecto);
                        //Luego enviar acuse de recibo
                    }
                    break;
                  case "AGREGAR":
                    System.out.println("Nuevo mensaje");
                    System.out.println("Tipo de orden: " + ordenDestinario[0]);
                      System.out.println("Nuevo servidor detectado: "
                                          + ordenDestinario[1]);
                      agregarOtrosServEnServicio(ordenDestinario[1]);
                      //Luego enviar acuse de recibo
                      break;
                }
            }

        } catch(Exception e) {
           System.out.println("ServAlmacenamiento :"+e.getMessage());
                e.printStackTrace();
        }
   }

}
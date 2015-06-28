/*


*/

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ServAlmacenamiento extends Thread {

    // DireccionIp Multicast por defecto "224.0.0.3"
    static InetAddress direccionIPMulticast;
    static MulticastSocket socketEscucha;
    static HashMap<Integer,String> listaServAlmacen;
    static String nombreServidor;
    static int puerto;
    static int puertoCliente = 8600;

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
    
    public static Coleccion existeRepo(String nombreRepo) {
        
        Path path = Paths.get(nombreRepo);

        if (!Files.exists(path)) {
            System.out.println("El repositorio " + nombreRepo + " no existe.");
            return null;
        } else {
            System.out.println("Checkout al: " +nombreRepo);
            Coleccion docs = new Coleccion(nombreRepo);
            File carpeta = new File(nombreRepo);
            File[] archivos = carpeta.listFiles();

            for (int i = 0; i < archivos.length; i++) {
                if (archivos[i].isFile()) {
                    String nombreArchivo = archivos[i].getName();
                    System.out.println(nombreArchivo);
                    docs.agregarDocumento(path.toString()+'/'+nombreArchivo);
                }
            }
            return docs;
        }
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

//        Thread t1 = new Thread(){
//            
//            public void run() {
//        
//                ServerSocket socketServicio;
//                Socket socketCliente = null;
//                DataInputStream is;
//                PrintStream os;
//
//                try {
//                    socketServicio = new ServerSocket(puertoCliente);
//
//                    socketCliente = socketServicio.accept();
//
//                    is = new DataInputStream(socketCliente.getInputStream());
//                    os = new PrintStream(socketCliente.getOutputStream());
//                } catch (IOException e) {
//                    System.out.println(e);
//                }
//            }
//        };
        
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
                    case "BUSCAR":
                        System.out.println("Buscar Host");

                        buzonEstandar = new byte[256]; // Resetear buffer entrada
                        paqueteEntrante =
                            new DatagramPacket(buzonEstandar, buzonEstandar.length);
                        socketEscucha.receive(paqueteEntrante);
                        mensaje = new String(buzonEstandar, 0,
                                         buzonEstandar.length);
                        mensaje = mensaje.trim();

                        System.out.println("Nombre proyecto: " + mensaje);
                        nombreProyecto = mensaje;
                      
                        Coleccion existe = existeRepo(nombreProyecto);
                      
                        String orden;
                      
                        if (existe != null)
                            orden = "EXISTE";
                        else
                            orden = "NEXISTE";
                      
                        DatagramPacket existePaquete = 
                              new DatagramPacket(orden.getBytes(),
                                                 orden.length(),
                                                 direccionIPMulticast,puerto);
                      
                        socketEscucha.send(existePaquete);
                        
                        if (existe != null) {
                            
                            ByteArrayOutputStream bs = new ByteArrayOutputStream();
                            ObjectOutputStream os = new ObjectOutputStream (bs);
                            os.writeObject(existe);
                            os.close();
                            byte[] archivos =  bs.toByteArray();
                            DatagramPacket repo = new DatagramPacket(
                                                      archivos,archivos.length,
                                                      direccionIPMulticast, puerto);
                           
                            int num =  archivos.length;
                            byte[] longitud = Integer.toString(num).getBytes();
                            DatagramPacket longitudArchivos = new DatagramPacket(
                                                                  longitud, longitud.length,
                                                                  direccionIPMulticast, puerto);

                            socketEscucha.send(longitudArchivos);
                            socketEscucha.send(repo);
                        }
                      
                        break;
                      
                    case "EXISTE":
                        System.out.println("Ignorar mensaje EXISTE.");
                        break;
                    case "NEXISTE":
                        System.out.println("Ignorar mensaje NO EXISTE");
                        break;
                      
                }
            }

        } catch(Exception e) {
           System.out.println("ServAlmacenamiento :"+e.getMessage());
                e.printStackTrace();
        }
   }

}
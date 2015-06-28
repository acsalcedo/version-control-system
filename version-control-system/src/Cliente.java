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
            String nomDirectorio = documentos.obtNombreProyecto();

            File directorio = new File(nomDirectorio);

            if (directorio.exists()) {
                System.out.println("Ya existe el repositorio " + nomDirectorio);
                return;
            } else {
                if (directorio.mkdirs())
                    System.out.println("Se creo la carpeta del repositorio " + nomDirectorio);
            }
            try {
                for (Documento doc : archivos) {

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
    
        public static void actualizarRepositorio(Coleccion documentos) {

        if (!documentos.coleccionVacia()) {

            Collection<Documento> archivos = documentos.obtDocumentos();
            String nomDirectorio = documentos.obtNombreProyecto();

            File directorio = new File(nomDirectorio);

            if (!directorio.exists()) {
                System.out.println("No existe el repositorio " + nomDirectorio);
                System.out.println("No se puede actualizar.");
                return;
            }
            
            try {
                for (Documento doc : archivos) {

                    FileOutputStream salida = new FileOutputStream(nomDirectorio + '/'+doc.obtNombre());
                    salida.write(doc.obtContenidoByte());
                    salida.close();

                    System.out.println("Se actualizo el archivo: " + doc.obtNombre());
                }
            } catch (Exception e) {
                System.out.println("Cliente: " + e.getMessage());
            }
        } else
            System.out.println("El repositorio esta vacio.");

    }

    public static void main(String[] args) {

        String host = null;
        String file = null;
        String dir  = null;
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

        OpClienteServidor operaciones = (OpClienteServidor)
        Naming.lookup("rmi://" +host+ ":" +puerto+ "/ServicioSCVD");
        String workingDirectory = System.getProperty("user.dir");
        String repository = null;

        // Variable que determina la operacion remota a realizar
        int estado = 0;

        do {

            System.out.println("");
            System.out.print(" \t\t\t ");
            System.out.println("     MENU PRINCIPAL   ");
            System.out.println("");
            System.out.print(" \t\t ");
            System.out.println("  Sistemas de Control de Versiones USB   ");
            System.out.print(" \t\t ");
            System.out.println("  Sistema manejador de versiones (SMVD)  ");
            System.out.print(" \t\t ");
            System.out.println("     para Sistemas de Operación II       ");
            System.out.println("");
            System.out.print(" \t\t ");
            System.out.println(" Opción 1: Checkout de un repositorio. ");
            System.out.print(" \t\t ");
            System.out.println(" Opción 2: Crear un repositorio.   ");
            System.out.print(" \t\t ");
            System.out.println(" Opción 3: Agregar un archivo.   ");
            System.out.print(" \t\t ");
            System.out.println(" Opción 4: Realizar commit del sistema.");
            System.out.print(" \t\t ");
            System.out.println(" Opción 5: Actualizar un repositorio.        ");
            System.out.print(" \t\t ");
            System.out.println(" Opción 6: Salir.        ");
            System.out.println(" ");
            System.out.print(" \t\t ");
            System.out.println(" Indique la acción que desea realizar:  ");
            System.out.print(" \t\t ");
            System.out.println(" ");

            Scanner entrada = new Scanner(System.in);
            Scanner indata  = new Scanner(System.in);
            System.out.println("");
            System.out.print(" \t\tOpción: ");

            try {
                estado = entrada.nextInt();
            } catch(InputMismatchException exception){
                estado = 0;
            }
            switch(estado) {

                case 1:
                    System.out.println("");
                    System.out.println("Opción 1: Acción Checkout.");
                    // Acción del checkout
                    System.out.println("Indique el nombre del repositorio a crear: ");
                    repository = indata.next();
                    
                    Coleccion repo = operaciones.checkout(repository);
                    
                    if (repo != null) 
                        crearRepositorio(repo);
                    else 
                        System.out.println("El repositorio dado no existe en el servidor.");
                    break;

                case 2:
                    System.out.println("");
                    System.out.println("Opción 2: Acción Agregar Directorio.");
                    System.out.print("Indique el nombre del directorio:     ");
                    dir = indata.next();
                    // Acción de agregar directorio
                    cli.agregarDirectorioProtegido(dir);
                    System.out.println("Directorio creado exitosamente. ");
                    System.out.println("");
                    break;

                case 3:
                    System.out.println("");
                    System.out.println("Opción 3: Acción Agregar Archivo.");
                    System.out.print("Indique la ruta del archivo a agregar: ");
                    file = indata.next();
                    // Acción de agregar archivo
                    cli.agregarArchivo(workingDirectory + "/" + file);
                    System.out.println("Archivo creado exitosamente. ");
                    System.out.println("");
                    break;

                case 4:
                    System.out.println("");
                    System.out.println("Opción 4: Acción Commit.");
                    // Acción del commit
                    if (cli.archivosProtegidos != null) {
                        operaciones.commit(cli.obtArchivosProtegidos());
                        System.out.println("Acción commit realizada satisfactoriamente.");
                        System.out.println("");
                    } else {
                        System.out.println("No ha indicado el repositorio a crear.");
                        System.out.println("Por favor elija la opción 2 para crearlo.");
                        System.out.println("");
                    }
                    break;

                case 5:
                    System.out.println(" ");
                    System.out.println("Opción 5: Acción Update. ");
                    // Acción del update
                    System.out.println("Indique el nombre del repositorio a actualizar: ");
                    repository = indata.next();
                    
                    Coleccion repositorio = operaciones.update(repository);
                    if (repositorio != null)
                        actualizarRepositorio(repositorio);
                    else
                        System.out.println("El repositorio dado no existe en el servidor.");
                    break;

                case 6:
                    System.out.println(" ");
                    System.out.println("\t\tOpción 6: Acción Salir.  ");
                    System.out.println("\t\tUd. ha eligido salir.    ");
                    System.out.println("\t\tHasta luego.             ");
                    System.out.println("");
                    break;

                default:
                    System.out.println(" ");
                    System.out.print("\t\tOpción incorrecta. ");
                    System.out.println("Intente de nuevo.    ");
                    System.out.println("");
                    break;

            }

        } while( estado != 6);

        } catch (Exception e) {
          System.out.println("Cliente Exception: "+e.getMessage());
        }
    }
    

}
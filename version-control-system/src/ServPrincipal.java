/*
Clase de Servidor principal

*/
import java.io.*;
import java.rmi.*;


import java.util.*;
import java.rmi.registry.*;
import java.lang.*;

/*
    Clase que implementa el ServPrincipal. Si solo existe un servidor principal
    este funciona a su vez como ServAlmacenamiento.
    No debe definirse operaciones adicionales. Estas se implementan en la clase
    OpClienteServidorImple
    Se ejecuta de la siguiente manera: java ServPrincipal <NroPuerto>
*/

public class ServPrincipal extends ServAlmacenamiento {

    private int puerto;

    public ServPrincipal(int puertoServicio,
                         int puertoControl,
                         int nroReplicas,
                         String direccionMulticast,
                         OpServidorServidor operacionesInternas) {
        this.puerto = puertoServicio;
        try {
            OpClienteServidor operaciones =
                new OpClienteServidorImple(operacionesInternas);
            //Probar notificar servicio. Despues se borra
            Naming.rebind("rmi://localhost:"+puertoServicio+"/ServicioSCVD",
                         operaciones);
        } catch(Exception e) {
            System.out.println("ServPrincipal :"+e.getMessage());
            e.printStackTrace();
        }
    }

public static void main(String args[]) {

    if (args.length < 3) {
        System.err.println("Parametros incorrectos. java ServPrincipal"
            + "<puertoServicio> <puertoControl> <direccionMulticast> <NroReplicas>");
        System.exit(1);
    }

    int puertoServicio = Integer.parseInt(args[0]);
    int puertoControl = Integer.parseInt(args[1]);
    int nroReplicas = Integer.parseInt(args[3]);

    try {
        LocateRegistry.createRegistry(puertoServicio);
    } catch(Exception e) {
       System.out.println("ServPrincipal :"+e.getMessage());
            e.printStackTrace();
    }

    OpServidorServidor operacionesInternas =
        new OpServidorServidor(puertoControl, args[2], nroReplicas);

    operacionesInternas.start();
    new ServPrincipal(puertoServicio, puertoControl,
                      nroReplicas, args[2], operacionesInternas);
   }

}
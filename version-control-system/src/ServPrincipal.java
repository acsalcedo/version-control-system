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

    public ServPrincipal(int puerto){
        this.puerto = puerto;
        try {
            OpClienteServidor operaciones = new OpClienteServidorImple();
            Naming.rebind("rmi://localhost:"+puerto+"/ServicioSCVD",
                         operaciones);
        } catch(Exception e) {
           System.out.println("ServPrincipal :"+e.getMessage());
            e.printStackTrace();
        }
    }

public static void main(String args[]) {
      
    if (args.length < 1) {
        System.err.println("Parametros incorrectos. java ServPrincipal <puerto>");
        System.exit(1);
    }
       
    int puerto = Integer.parseInt(args[0]);
      try {
            LocateRegistry.createRegistry(puerto);

         } catch(Exception e) {
           System.out.println("ServPrincipal :"+e.getMessage());
                e.printStackTrace();
         }

        new ServPrincipal(puerto);
   }

}
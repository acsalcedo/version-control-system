/*
Interfase para las operaciones remotas entre el Cliente y el servPrincipal
Modo unicast
*/
import java.util.HashMap;
public interface OpClienteServidor extends java.rmi.Remote {

    /*
       Metodo commit
       Envia una Coleccion para almacenarla provisionalmente en temp.
       Por otra parte, crea una directorio y escribe los archivos enviados
       en el.
    */
    public boolean commit(Coleccion archivos_enviar)
        throws java.rmi.RemoteException;

    public void update() throws java.rmi.RemoteException;

    public Coleccion checkout(String nombreRepo) throws java.rmi.RemoteException;

    public void listarArchivos() throws java.rmi.RemoteException;

}
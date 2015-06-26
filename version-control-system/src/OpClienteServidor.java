/*
Interfase para las operaciones remotas entre el Cliente y el servPrincipal
Modo unicast
*/
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

    public String listarArchivos(String nombreRepo) throws java.rmi.RemoteException;

}
/*
Interfase para las operaciones remotas entre el cliente y el servidor
Modo unicast
*/

public interface OpClienteServidor extends java.rmi.Remote {

    public commit() throws java.rmi.RemoteException;

    public update() throws java.rmi.RemoteException;

    public checkout() throws java.rmi.RemoteException;

}
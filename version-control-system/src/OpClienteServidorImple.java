/*
Implementación de la interfase OpClienteServidor. Se implementan las operaciones
remotas
*/

public class OpClienteServidorImple
    extends
    java.rmi.server.UnicastRemoteObject
    implements OpClienteServidor {

    // Implementations must have an
    //explicit constructor
    // in order to declare the
    //RemoteException exception
    public OpClienteServidorImple() throws java.rmi.RemoteException {
        super();
    }

    public boolean commit() throws java.rmi.RemoteException {
    System.out.println("Sumando " + a + " " + b);
    return a + b;
    }

    public boolean update() throws java.rmi.RemoteException {

    }

    public checkout()  throws java.rmi.RemoteException {

    }

}
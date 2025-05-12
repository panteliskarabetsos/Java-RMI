import java.rmi.Remote;
import java.rmi.RemoteException;

public interface THClientCallback extends Remote {
    void notifyAvailable(String seatType) throws RemoteException;
}


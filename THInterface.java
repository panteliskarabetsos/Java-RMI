import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface THInterface extends Remote {

    // Επιστρέφει διαθέσιμες θέσεις ανά τύπο
    Map<String, Integer> getAvailableSeats() throws RemoteException;

    // Κάνει κράτηση θέσεων συγκεκριμένου τύπου για χρήστη
    String book(String type, int number, String name) throws RemoteException;

    // Επιστρέφει λίστα όλων των πελατών και των κρατήσεών τους
    List<String> getGuests() throws RemoteException;

    // Ακυρώνει κρατήσεις συγκεκριμένου χρήστη
    String cancel(String type, int number, String name) throws RemoteException;

    // Εγγραφή στη λίστα ειδοποίησης για συγκεκριμένο τύπο θέσεων
    void registerForNotification(String type, THClientCallback client) throws RemoteException;

    // Απεγγραφή από τη λίστα ειδοποίησης
    void unregisterForNotification(String type, THClientCallback client) throws RemoteException;
}

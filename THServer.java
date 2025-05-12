import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.Naming;

public class THServer {
    public static void main(String[] args) {
        try {
            // Δημιουργία ή εύρεση του RMI Registry
            try {
                LocateRegistry.createRegistry(1099);
                System.out.println("RMI registry created on port 1099.");
            } catch (Exception e) {
                System.out.println("RMI registry already running.");
            }

            // Δημιουργία αντικειμένου υλοποίησης
            THInterface impl = new THImpl();

            // Καταχώρηση στο RMI registry
            Naming.rebind("TheaterService", impl);
            System.out.println("TheaterService is ready.");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}

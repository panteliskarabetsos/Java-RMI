import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class THImpl extends UnicastRemoteObject implements THInterface {

    // Τύποι θέσεων και διαθεσιμότητα
    private final Map<String, Integer> seatAvailability;
    private final Map<String, Integer> seatPrices;

    // Κρατήσεις: <Όνομα Πελάτη, List<Κράτηση>>
    private final Map<String, List<Reservation>> reservations;

    // Λίστες ειδοποιήσεων: <Τύπος Θέσης, List<THClientCallback>>

      private final Map<String, List<THClientCallback>> notificationLists = new HashMap<>();


    // Constructor
    public THImpl() throws RemoteException {
        super();
        seatAvailability = new HashMap<>();
        seatPrices = new HashMap<>();
        reservations = new HashMap<>();
      

        // Αρχική διαθεσιμότητα
        seatAvailability.put("ΠΑ", 100);
        seatAvailability.put("ΠΒ", 200);
        seatAvailability.put("ΠΓ", 300);
        seatAvailability.put("ΚΕ", 250);
        seatAvailability.put("ΠΘ", 50);

        // Τιμές
        seatPrices.put("ΠΑ", 50);
        seatPrices.put("ΠΒ", 40);
        seatPrices.put("ΠΓ", 30);
        seatPrices.put("ΚΕ", 35);
        seatPrices.put("ΠΘ", 25);
    }

    @Override
    public synchronized Map<String, Integer> getAvailableSeats() {
        return new HashMap<>(seatAvailability);
    }

    @Override
    public synchronized String book(String type, int number, String name) throws RemoteException {
        if (!seatAvailability.containsKey(type)) return "Μη έγκυρος τύπος θέσης.";

        int available = seatAvailability.get(type);
        if (available >= number) {
            seatAvailability.put(type, available - number);
            reservations.putIfAbsent(name, new ArrayList<>());
            reservations.get(name).add(new Reservation(type, number, seatPrices.get(type)));

            return "Η κράτηση ήταν επιτυχής για " + number + " θέσεις τύπου " + type +
                    ". Συνολικό κόστος: " + (number * seatPrices.get(type)) + "€";
        } else if (available > 0) {
            return "Διαθέσιμες μόνο " + available + " θέσεις τύπου " + type + ". Θέλετε να τις κλείσετε;";
        } else {
            return "Δεν υπάρχουν διαθέσιμες θέσεις τύπου " + type + ".";
        }
    }

    @Override
    public synchronized String cancel(String type, int number, String name) throws RemoteException {
        List<Reservation> userReservations = reservations.get(name);
        if (userReservations == null) return "Δεν βρέθηκαν κρατήσεις για τον χρήστη " + name + ".";

        for (Reservation r : userReservations) {
            if (r.getType().equals(type)) {
                if (r.getNumber() >= number) {
                    r.setNumber(r.getNumber() - number);
                    seatAvailability.put(type, seatAvailability.get(type) + number);

                    // Καθαρισμός αν μηδενιστεί η κράτηση
                    if (r.getNumber() == 0) userReservations.remove(r);
                    if (userReservations.isEmpty()) reservations.remove(name);

                    // Ειδοποίηση ενδιαφερόμενων
                    notifyInterestedClients(type);

                    return "Ακυρώθηκαν " + number + " θέσεις τύπου " + type +
                            ". Υπόλοιπες κρατήσεις: " + getUserReservationsString(name);
                }
            }
        }

        return "Δεν βρέθηκαν " + number + " θέσεις τύπου " + type + " για ακύρωση για τον χρήστη " + name + ".";
    }


    @Override
    public synchronized List<String> getGuests() {
        List<String> output = new ArrayList<>();
        for (Map.Entry<String, List<Reservation>> entry : reservations.entrySet()) {
            StringBuilder sb = new StringBuilder(entry.getKey()).append(": ");
            int totalCost = 0;
            for (Reservation r : entry.getValue()) {
                sb.append(r.getNumber()).append(" θέσεις τύπου ").append(r.getType()).append(", ");
                totalCost += r.getNumber() * r.getPrice();
            }
            sb.append("Συνολικό κόστος: ").append(totalCost).append("€");
            output.add(sb.toString());
        }
        return output;
    }

    @Override
    public synchronized void registerForNotification(String type, THClientCallback client) throws RemoteException {
        notificationLists.putIfAbsent(type, new ArrayList<>());
        notificationLists.get(type).add(client);
    }

    @Override
    public synchronized void unregisterForNotification(String type, THClientCallback client) throws RemoteException {
        List<THClientCallback> clients = notificationLists.get(type);
        if (clients != null) {
            clients.remove(client);
        }
        
        
    }

    // Βοηθητικό: ενημέρωση πελατών σε λίστα ειδοποίησης
    private void notifyInterestedClients(String type) {
        List<THClientCallback> clients = notificationLists.get(type);
        if (clients != null) {
            for (THClientCallback client : clients) {
                try {
                    client.notifyAvailable(type);
                } catch (RemoteException e) {
                    System.out.println("Απέτυχε ειδοποίηση client.");
                }
            }
            clients.clear(); // Εκκαθάριση λίστας μετά την ειδοποίηση
        }
    }

    private String getUserReservationsString(String name) {
        List<Reservation> userRes = reservations.get(name);
        if (userRes == null || userRes.isEmpty()) return "Καμία κράτηση.";
        StringBuilder sb = new StringBuilder();
        for (Reservation r : userRes) {
            sb.append(r.getNumber()).append(" θέσεις ").append(r.getType()).append(", ");
        }
        return sb.toString();
    }

    // Εσωτερική κλάση για κράτηση
    private static class Reservation {
        private final String type;
        private int number;
        private final int price;

        public Reservation(String type, int number, int price) {
            this.type = type;
            this.number = number;
            this.price = price;
        }

        public String getType() { return type; }
        public int getNumber() { return number; }
        public int getPrice() { return price; }
        public void setNumber(int n) { number = n; }
    }
}

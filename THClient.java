import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Scanner;
import java.util.List;

public class THClient extends UnicastRemoteObject implements THClientCallback {

    private static THInterface theater;

    protected THClient() throws RemoteException {
        super();
    }

    @Override
    public void notifyAvailable(String type) throws RemoteException {
        System.out.println("ΕΙΔΟΠΟΙΗΣΗ: Θέσεις τύπου " + type + " είναι πλέον διαθέσιμες");
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        try {
            String command = args[0];

            if (command.equals("list") && args.length == 2) {
                connectToServer(args[1]);
                Map<String, Integer> availableSeats = theater.getAvailableSeats();
                for (Map.Entry<String, Integer> entry : availableSeats.entrySet()) {
                    String code = entry.getKey();
                    int count = entry.getValue();
                    int price = getPriceByCode(code);
                    String label = getSeatLabel(code);
                    System.out.println(count + " θέσεις " + label + " (κωδικός: " + code + ") - τιμή: " + price + " Ευρώ");
                }

            } else if (command.equals("book") && args.length == 5) {
                String host = args[1];
                String type = args[2];
                int number = Integer.parseInt(args[3]);
                String name = args[4];

                connectToServer(host);
                THClient clientObj = new THClient();

                String result = theater.book(type, number, name);
                System.out.println(result);

                if (result.startsWith("Διαθέσιμες μόνο")) {
                    Scanner scanner = new Scanner(System.in);
                    System.out.print("Θέλετε να τις κλείσετε; (ναι/όχι): ");
                    String answer = scanner.nextLine().trim().toLowerCase();
                    if (answer.equals("ναι")) {
                        int available = Integer.parseInt(result.replaceAll("[^0-9]", ""));
                        String confirm = theater.book(type, available, name);
                        System.out.println(confirm);
                    }
                } else if (result.contains("Δεν υπάρχουν διαθέσιμες θέσεις")) {
                    Scanner scanner = new Scanner(System.in);
                    System.out.print("Θέλετε να εγγραφείτε για ειδοποίηση αν υπάρξει διαθεσιμότητα; (ναι/όχι): ");
                    String answer = scanner.nextLine().trim().toLowerCase();
                    if (answer.equals("ναι")) {
                        theater.registerForNotification(type, clientObj);
                        System.out.println("Έγινε εγγραφή στη λίστα ειδοποιήσεων.");
                    }
                }

            } else if (command.equals("cancel") && args.length == 5) {
                connectToServer(args[1]);
                String result = theater.cancel(args[2], Integer.parseInt(args[3]), args[4]);
                System.out.println(result);

            } else if (command.equals("guests") && args.length == 2) {
                connectToServer(args[1]);
                List<String> guests = theater.getGuests();
                if (guests.isEmpty()) {
                    System.out.println("Δεν υπάρχουν καταχωρημένες κρατήσεις.");
                } else {
                    guests.forEach(System.out::println);
                }

            } else {
                printUsage();
            }

        } catch (Exception e) {
            System.err.println("Σφάλμα client: " + e);
            e.printStackTrace();
        }
    }

    private static void connectToServer(String host) throws Exception {
        theater = (THInterface) Naming.lookup("rmi://" + host + "/TheaterService");
    }

    private static void printUsage() {
        System.out.println("Χρήση:");
        System.out.println("java THClient list <hostname>");
        System.out.println("java THClient book <hostname> <type> <number> <name>");
        System.out.println("java THClient cancel <hostname> <type> <number> <name>");
        System.out.println("java THClient guests <hostname>");
    }

    private static String getSeatLabel(String code) {
        return switch (code) {
            case "ΠΑ" -> "Πλατεία - Ζώνη Α";
            case "ΠΒ" -> "Πλατεία - Ζώνη Β";
            case "ΠΓ" -> "Πλατεία - Ζώνη Γ";
            case "ΚΕ" -> "Κεντρικός Εξώστης";
            case "ΠΘ" -> "Πλαϊνά Θεωρεία";
            default -> "Άγνωστος Τύπος";
        };
    }

    private static int getPriceByCode(String code) {
        return switch (code) {
            case "ΠΑ" -> 50;
            case "ΠΒ" -> 40;
            case "ΠΓ" -> 30;
            case "ΚΕ" -> 35;
            case "ΠΘ" -> 25;
            default -> 0;
        };
    }
}


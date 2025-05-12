# Java RMI Theater Booking System

A distributed client-server application developed as part of a **Distributed Systems lab project**. It allows multiple clients to book, cancel, and view theater seat reservations via Java RMI. Includes a notification system that alerts users when previously unavailable seat types become available again.

---

## Features
- View available seats by category (ΠΑ, ΠΒ, ΠΓ, ΚΕ, ΠΘ)
- Book multiple seats per user and seat type
- Cancel reservations
- View guest reservations and total cost
- Register for automatic notifications when canceled seats become available

## Technologies
- Java 17+
- Java RMI
- CLI-based interaction

## How to Run

### 1. Compile all Java files
```bash
javac *.java
```

### 2. Start the RMI registry (in project directory)
```bash
rmiregistry &
```

### 3. Start the server
```bash
java THServer
```

### 4. Run clients in separate terminals
```bash
java THClient list localhost
java THClient book localhost ΠΘ 2 Maria
java THClient cancel localhost ΠΘ 1 Maria
java THClient guests localhost
```

## File Overview
- `THInterface.java` – RMI server interface
- `THImpl.java` – Server implementation
- `THServer.java` – Launches RMI server
- `THClient.java` – CLI-based client
- `THClientCallback.java` – Client-side callback interface for notifications

## Example
```
$ java THClient book localhost ΠΘ 50 Alice
Η κράτηση ήταν επιτυχής για 50 θέσεις τύπου ΠΘ. Συνολικό κόστος: 1250€

$ java THClient book localhost ΠΘ 1 Bob
Δεν υπάρχουν διαθέσιμες θέσεις τύπου ΠΘ.
Θέλετε να εγγραφείτε για ειδοποίηση αν υπάρξει διαθεσιμότητα; (ναι/όχι): ναι
Έγινε εγγραφή στη λίστα ειδοποιήσεων.

$ java THClient cancel localhost ΠΘ 5 Alice
Ακυρώθηκαν 5 θέσεις τύπου ΠΘ. Υπόλοιπες κρατήσεις: 45 θέσεις ΠΘ,

📢 ΕΙΔΟΠΟΙΗΣΗ: Θέσεις τύπου ΠΘ είναι πλέον διαθέσιμες!
```

## License
For educational use only.

---

This README summarizes the full functionality of the application described in the main documentation.
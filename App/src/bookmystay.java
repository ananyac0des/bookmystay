import java.util.*;

// ===== Domain Model =====
abstract class Room {
    private int beds;
    private String size;
    private double price;

    public Room(int beds, String size, double price) {
        this.beds = beds;
        this.size = size;
        this.price = price;
    }

    public void displayDetails() {
        System.out.println("Beds: " + beds);
        System.out.println("Size: " + size);
        System.out.println("Price: ₹" + price);
    }

    public abstract String getRoomType();
}

class SingleRoom extends Room {
    public SingleRoom() { super(1, "Small", 1500); }
    public String getRoomType() { return "Single Room"; }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super(2, "Medium", 2500); }
    public String getRoomType() { return "Double Room"; }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super(3, "Large", 5000); }
    public String getRoomType() { return "Suite Room"; }
}

// ===== Inventory (from UC3/UC4) =====
class RoomInventory {
    private HashMap<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 2);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }
}

// ===== Reservation (Booking Request) =====
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void displayRequest() {
        System.out.println("Guest: " + guestName + " | Requested: " + roomType);
    }
}

// ===== Booking Request Queue =====
class BookingQueue {
    private Queue<Reservation> queue;

    public BookingQueue() {
        queue = new LinkedList<>();
    }

    // Add request (enqueue)
    public void addRequest(Reservation reservation) {
        queue.offer(reservation);
        System.out.println("Request added for " + reservation.getGuestName());
    }

    // View all requests (FIFO order)
    public void displayQueue() {
        System.out.println("\nBooking Requests (FIFO Order):");
        for (Reservation r : queue) {
            r.displayRequest();
        }
    }
}

// ===== Main Class =====
public class bookmystay {

    public static void main(String[] args) {

        System.out.println("=====================================");
        System.out.println("   Welcome to Book My Stay App");
        System.out.println("   Hotel Booking System v5.0");
        System.out.println("=====================================\n");

        // Initialize booking queue
        BookingQueue bookingQueue = new BookingQueue();

        // Simulate booking requests (arrival order matters)
        Reservation r1 = new Reservation("Ananya", "Single Room");
        Reservation r2 = new Reservation("Rahul", "Double Room");
        Reservation r3 = new Reservation("Priya", "Suite Room");

        // Add to queue (FIFO)
        bookingQueue.addRequest(r1);
        bookingQueue.addRequest(r2);
        bookingQueue.addRequest(r3);

        // Display queue
        bookingQueue.displayQueue();

        System.out.println("\nNote: No rooms allocated yet (intake phase only).");
    }
}
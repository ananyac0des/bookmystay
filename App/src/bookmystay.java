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

// ===== Inventory Service =====
class RoomInventory {
    private HashMap<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 1);
        inventory.put("Suite Room", 1);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void decrementRoom(String roomType) {
        int current = inventory.getOrDefault(roomType, 0);
        if (current > 0) {
            inventory.put(roomType, current - 1);
        }
    }

    public void displayInventory() {
        System.out.println("\nUpdated Inventory:");
        for (Map.Entry<String, Integer> e : inventory.entrySet()) {
            System.out.println(e.getKey() + " → " + e.getValue());
        }
    }
}

// ===== Reservation =====
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

// ===== Booking Queue =====
class BookingQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.offer(r);
    }

    public Reservation getNextRequest() {
        return queue.poll(); // FIFO
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

// ===== Booking Service (Core UC6) =====
class BookingService {

    private RoomInventory inventory;

    // Track allocated IDs globally
    private Set<String> allocatedRoomIds = new HashSet<>();

    // Map roomType -> assigned IDs
    private Map<String, Set<String>> roomAllocations = new HashMap<>();

    private int idCounter = 1;

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void processBookings(BookingQueue queue) {

        while (!queue.isEmpty()) {

            Reservation r = queue.getNextRequest();
            String roomType = r.getRoomType();

            System.out.println("\nProcessing request for " + r.getGuestName());

            // Check availability
            if (inventory.getAvailability(roomType) > 0) {

                // Generate unique room ID
                String roomId;
                do {
                    roomId = roomType.substring(0, 2).toUpperCase() + idCounter++;
                } while (allocatedRoomIds.contains(roomId));

                // Ensure uniqueness
                allocatedRoomIds.add(roomId);

                // Map allocation
                roomAllocations
                        .computeIfAbsent(roomType, k -> new HashSet<>())
                        .add(roomId);

                // Update inventory immediately
                inventory.decrementRoom(roomType);

                // Confirm booking
                System.out.println("Booking CONFIRMED for " + r.getGuestName());
                System.out.println("Assigned Room ID: " + roomId);

            } else {
                System.out.println("Booking FAILED for " + r.getGuestName() + " (No availability)");
            }
        }
    }

    public void displayAllocations() {
        System.out.println("\nRoom Allocations:");
        for (Map.Entry<String, Set<String>> e : roomAllocations.entrySet()) {
            System.out.println(e.getKey() + " → " + e.getValue());
        }
    }
}

// ===== Main =====
public class bookmystay {

    public static void main(String[] args) {

        System.out.println("=====================================");
        System.out.println("   Welcome to Book My Stay App");
        System.out.println("   Hotel Booking System v6.0");
        System.out.println("=====================================");

        // Initialize services
        RoomInventory inventory = new RoomInventory();
        BookingQueue queue = new BookingQueue();
        BookingService bookingService = new BookingService(inventory);

        // Add booking requests (FIFO order)
        queue.addRequest(new Reservation("Ananya", "Single Room"));
        queue.addRequest(new Reservation("Rahul", "Single Room"));
        queue.addRequest(new Reservation("Priya", "Single Room")); // will fail (only 2 available)
        queue.addRequest(new Reservation("Karan", "Suite Room"));

        // Process bookings
        bookingService.processBookings(queue);

        // Display final state
        bookingService.displayAllocations();
        inventory.displayInventory();
    }
}
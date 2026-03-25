import java.util.*;

// ===== Domain Model =====
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getReservationId() { return reservationId; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }

    public void display() {
        System.out.println(reservationId + " | " + guestName + " | " + roomType);
    }
}

// ===== Inventory =====
class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 1);
        inventory.put("Suite Room", 1);
    }

    public int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    public void decrement(String type) {
        inventory.put(type, inventory.get(type) - 1);
    }
}

// ===== Booking Queue =====
class BookingQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void add(Reservation r) { queue.offer(r); }
    public Reservation next() { return queue.poll(); }
    public boolean isEmpty() { return queue.isEmpty(); }
}

// ===== Booking History =====
class BookingHistory {
    private List<Reservation> history = new ArrayList<>();

    public void addReservation(Reservation r) {
        history.add(r); // maintains insertion order
    }

    public List<Reservation> getAll() {
        return new ArrayList<>(history); // defensive copy
    }
}

// ===== Booking Service =====
class BookingService {
    private RoomInventory inventory;
    private Set<String> allocatedIds = new HashSet<>();
    private BookingHistory history;

    public BookingService(RoomInventory inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
    }

    public void process(BookingQueue queue) {

        while (!queue.isEmpty()) {
            Reservation r = queue.next();
            String type = r.getRoomType();

            if (inventory.getAvailability(type) > 0 &&
                    !allocatedIds.contains(r.getReservationId())) {

                allocatedIds.add(r.getReservationId());
                inventory.decrement(type);

                // store in history AFTER confirmation
                history.addReservation(r);

                System.out.println("Confirmed: " + r.getReservationId());
            } else {
                System.out.println("Failed: " + r.getReservationId());
            }
        }
    }
}

// ===== Report Service =====
class BookingReportService {

    // Display all bookings
    public void showAllBookings(BookingHistory history) {
        System.out.println("\n=== Booking History ===");
        for (Reservation r : history.getAll()) {
            r.display();
        }
    }

    // Summary report
    public void generateSummary(BookingHistory history) {
        Map<String, Integer> countByType = new HashMap<>();

        for (Reservation r : history.getAll()) {
            countByType.put(
                    r.getRoomType(),
                    countByType.getOrDefault(r.getRoomType(), 0) + 1
            );
        }

        System.out.println("\n=== Booking Summary ===");
        for (Map.Entry<String, Integer> e : countByType.entrySet()) {
            System.out.println(e.getKey() + " → " + e.getValue() + " bookings");
        }
    }
}

// ===== Main =====
public class bookmystay {

    public static void main(String[] args) {

        System.out.println("====== Book My Stay v8.0 ======\n");

        RoomInventory inventory = new RoomInventory();
        BookingQueue queue = new BookingQueue();
        BookingHistory history = new BookingHistory();

        BookingService bookingService = new BookingService(inventory, history);
        BookingReportService reportService = new BookingReportService();

        // Add booking requests
        queue.add(new Reservation("R101", "Ananya", "Single Room"));
        queue.add(new Reservation("R102", "Rahul", "Single Room"));
        queue.add(new Reservation("R103", "Priya", "Single Room")); // will fail

        // Process bookings
        bookingService.process(queue);

        // Admin views history
        reportService.showAllBookings(history);

        // Admin views summary
        reportService.generateSummary(history);

        System.out.println("\nReporting complete (read-only).");
    }
}
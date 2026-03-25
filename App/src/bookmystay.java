import java.util.*;

// ===== Reservation =====
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;
    private String roomId;      // assigned during allocation
    private boolean cancelled;  // status

    public Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.cancelled = false;
    }

    public String getReservationId() { return reservationId; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }

    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getRoomId() { return roomId; }

    public boolean isCancelled() { return cancelled; }
    public void markCancelled() { this.cancelled = true; }

    public void display() {
        System.out.println(reservationId + " | " + guestName + " | " + roomType +
                " | RoomID: " + roomId + " | Cancelled: " + cancelled);
    }
}

// ===== Inventory =====
class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single Room", 1);
        inventory.put("Double Room", 1);
    }

    public int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    public void decrement(String type) {
        inventory.put(type, inventory.get(type) - 1);
    }

    public void increment(String type) {
        inventory.put(type, inventory.get(type) + 1);
    }

    public void display() {
        System.out.println("\nInventory:");
        for (Map.Entry<String, Integer> e : inventory.entrySet()) {
            System.out.println(e.getKey() + " → " + e.getValue());
        }
    }
}

// ===== Booking Service =====
class BookingService {
    private RoomInventory inventory;
    private Map<String, Reservation> confirmed = new HashMap<>();
    private Set<String> allocatedIds = new HashSet<>();
    private int counter = 1;

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void confirm(Reservation r) {

        if (inventory.getAvailability(r.getRoomType()) <= 0) {
            System.out.println("Booking failed for " + r.getReservationId());
            return;
        }

        // Generate unique room ID
        String roomId = r.getRoomType().substring(0, 2).toUpperCase() + counter++;
        allocatedIds.add(roomId);

        r.setRoomId(roomId);
        confirmed.put(r.getReservationId(), r);

        inventory.decrement(r.getRoomType());

        System.out.println("Confirmed: " + r.getReservationId() + " → " + roomId);
    }

    public Map<String, Reservation> getConfirmedReservations() {
        return confirmed;
    }

    public Set<String> getAllocatedIds() {
        return allocatedIds;
    }
}

// ===== Cancellation Service =====
class CancellationService {

    private RoomInventory inventory;
    private Map<String, Reservation> confirmed;
    private Set<String> allocatedIds;

    // Stack for rollback tracking
    private Stack<String> rollbackStack = new Stack<>();

    public CancellationService(RoomInventory inventory,
                               Map<String, Reservation> confirmed,
                               Set<String> allocatedIds) {
        this.inventory = inventory;
        this.confirmed = confirmed;
        this.allocatedIds = allocatedIds;
    }

    public void cancel(String reservationId) {

        // Validate existence
        if (!confirmed.containsKey(reservationId)) {
            System.out.println("Cancellation FAILED: Invalid reservation ID");
            return;
        }

        Reservation r = confirmed.get(reservationId);

        // Prevent duplicate cancellation
        if (r.isCancelled()) {
            System.out.println("Cancellation FAILED: Already cancelled");
            return;
        }

        // Step 1: push to rollback stack (LIFO tracking)
        rollbackStack.push(r.getRoomId());

        // Step 2: release room ID
        allocatedIds.remove(r.getRoomId());

        // Step 3: restore inventory
        inventory.increment(r.getRoomType());

        // Step 4: mark cancelled
        r.markCancelled();

        System.out.println("Cancelled: " + reservationId + " | Rolled back RoomID: " + r.getRoomId());
    }

    public void showRollbackStack() {
        System.out.println("\nRollback Stack (LIFO): " + rollbackStack);
    }
}

// ===== Main =====
public class bookmystay {

    public static void main(String[] args) {

        System.out.println("====== Book My Stay v10.0 ======\n");

        RoomInventory inventory = new RoomInventory();
        BookingService bookingService = new BookingService(inventory);

        // Create bookings
        Reservation r1 = new Reservation("R101", "Ananya", "Single Room");
        Reservation r2 = new Reservation("R102", "Rahul", "Double Room");

        bookingService.confirm(r1);
        bookingService.confirm(r2);

        inventory.display();

        // Cancellation
        CancellationService cancelService = new CancellationService(
                inventory,
                bookingService.getConfirmedReservations(),
                bookingService.getAllocatedIds()
        );

        cancelService.cancel("R101"); // valid
        cancelService.cancel("R999"); // invalid
        cancelService.cancel("R101"); // duplicate cancel

        cancelService.showRollbackStack();
        inventory.display();

        System.out.println("\nFinal Booking States:");
        for (Reservation r : bookingService.getConfirmedReservations().values()) {
            r.display();
        }
    }
}
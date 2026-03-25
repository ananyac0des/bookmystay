import java.util.*;

// ===== Custom Exceptions =====
class InvalidRoomTypeException extends Exception {
    public InvalidRoomTypeException(String message) {
        super(message);
    }
}

class NoAvailabilityException extends Exception {
    public NoAvailabilityException(String message) {
        super(message);
    }
}

// ===== Reservation =====
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
}

// ===== Inventory =====
class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single Room", 1);
        inventory.put("Double Room", 1);
    }

    public boolean isValidRoomType(String type) {
        return inventory.containsKey(type);
    }

    public int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    public void decrement(String type) throws NoAvailabilityException {
        int current = getAvailability(type);
        if (current <= 0) {
            throw new NoAvailabilityException("No rooms available for: " + type);
        }
        inventory.put(type, current - 1);
    }
}

// ===== Validator =====
class BookingValidator {

    public static void validate(RoomInventory inventory, Reservation r)
            throws InvalidRoomTypeException, NoAvailabilityException {

        // Validate room type
        if (!inventory.isValidRoomType(r.getRoomType())) {
            throw new InvalidRoomTypeException(
                    "Invalid room type: " + r.getRoomType()
            );
        }

        // Validate availability
        if (inventory.getAvailability(r.getRoomType()) <= 0) {
            throw new NoAvailabilityException(
                    "No availability for: " + r.getRoomType()
            );
        }
    }
}

// ===== Booking Service =====
class BookingService {
    private RoomInventory inventory;
    private Set<String> allocated = new HashSet<>();

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void confirmBooking(Reservation r) {

        try {
            // Fail-fast validation
            BookingValidator.validate(inventory, r);

            // Prevent duplicate reservation IDs
            if (allocated.contains(r.getReservationId())) {
                throw new Exception("Duplicate reservation ID: " + r.getReservationId());
            }

            // Allocate
            inventory.decrement(r.getRoomType());
            allocated.add(r.getReservationId());

            System.out.println("Booking CONFIRMED: " + r.getReservationId());

        } catch (InvalidRoomTypeException | NoAvailabilityException e) {
            System.out.println("Booking FAILED: " + e.getMessage());

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
import java.util.HashMap;
import java.util.Map;

/**
 * Hotel Booking Management System
 *
 * Use Case 4: Room Search & Availability Check (Read-only)
 *
 * @author Ananya
 * @version 4.0
 */

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

// ===== Centralized Inventory (state holder) =====
class RoomInventory {
    private HashMap<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 0); // example: unavailable to test filtering
    }

    // Read-only accessors
    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public Map<String, Integer> getAllAvailability() {
        // return a defensive copy to avoid external mutation
        return new HashMap<>(inventory);
    }

    // (Mutation methods exist but are NOT used in search)
    public void updateAvailability(String roomType, int newCount) {
        inventory.put(roomType, newCount);
    }
}

// ===== Search Service (read-only) =====
class RoomSearchService {

    private final RoomInventory inventory;
    private final Map<String, Room> roomCatalog;

    public RoomSearchService(RoomInventory inventory, Map<String, Room> roomCatalog) {
        this.inventory = inventory;
        this.roomCatalog = roomCatalog;
    }

    // Performs read-only search
    public void searchAvailableRooms() {
        System.out.println("Available Rooms:\n");

        Map<String, Integer> snapshot = inventory.getAllAvailability();

        for (Map.Entry<String, Integer> entry : snapshot.entrySet()) {
            String roomType = entry.getKey();
            int available = entry.getValue();

            // Defensive check: only show available rooms
            if (available > 0) {
                Room room = roomCatalog.get(roomType);

                // Defensive check: ensure room exists in catalog
                if (room != null) {
                    System.out.println(room.getRoomType());
                    room.displayDetails();
                    System.out.println("Available: " + available + "\n");
                }
            }
        }
    }
}

// ===== Main =====
public class bookmystay {

    public static void main(String[] args) {

        System.out.println("====== Book My Stay v9.0 ======\n");

        RoomInventory inventory = new RoomInventory();
        BookingService bookingService = new BookingService(inventory);

        // Test cases
        Reservation r1 = new Reservation("R101", "Ananya", "Single Room"); // valid
        Reservation r2 = new Reservation("R102", "Rahul", "Suite Room");   // invalid type
        Reservation r3 = new Reservation("R103", "Priya", "Single Room");  // no availability

        bookingService.confirmBooking(r1);
        bookingService.confirmBooking(r2);
        bookingService.confirmBooking(r3);

        System.out.println("\nSystem continues running safely.");
        System.out.println("=====================================");
        System.out.println("   Welcome to Book My Stay App");
        System.out.println("   Hotel Booking System v4.0");
        System.out.println("=====================================\n");

        // Initialize domain objects (catalog)
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Inventory (centralized state)
        RoomInventory inventory = new RoomInventory();

        // Display room details + availability
        System.out.println(single.getRoomType());
        single.displayDetails();
        System.out.println("Available: " + inventory.getAvailability(single.getRoomType()) + "\n");

        System.out.println(doubleRoom.getRoomType());
        doubleRoom.displayDetails();
        System.out.println("Available: " + inventory.getAvailability(doubleRoom.getRoomType()) + "\n");

        System.out.println(suite.getRoomType());
        suite.displayDetails();
        System.out.println("Available: " + inventory.getAvailability(suite.getRoomType()) + "\n");

        // Display centralized inventory
        inventory.displayInventory();

        System.out.println("\nApplication finished.");
    }
}
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
    }
}
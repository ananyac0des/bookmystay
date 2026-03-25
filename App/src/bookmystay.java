import java.io.*;
import java.util.*;

// ===== Reservation (Serializable) =====
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String guest;
    private String roomType;

    public Reservation(String id, String guest, String roomType) {
        this.id = id;
        this.guest = guest;
        this.roomType = roomType;
    }

    public String getId() { return id; }
    public String getGuest() { return guest; }
    public String getRoomType() { return roomType; }

    public void display() {
        System.out.println(id + " | " + guest + " | " + roomType);
    }
}

// ===== Inventory (Serializable) =====
class RoomInventory implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 1);
    }

    public int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    public void decrement(String type) {
        inventory.put(type, inventory.get(type) - 1);
    }

    public Map<String, Integer> getAll() {
        return inventory;
    }

    public void display() {
        System.out.println("\nInventory:");
        for (Map.Entry<String, Integer> e : inventory.entrySet()) {
            System.out.println(e.getKey() + " → " + e.getValue());
        }
    }
}

// ===== Booking History (Serializable) =====
class BookingHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Reservation> history = new ArrayList<>();

    public void add(Reservation r) {
        history.add(r);
    }

    public List<Reservation> getAll() {
        return history;
    }

    public void display() {
        System.out.println("\nBooking History:");
        for (Reservation r : history) {
            r.display();
        }
    }
}

// ===== Persistence Service =====
class PersistenceService {

    private static final String FILE_NAME = "booking_data.ser";

    // Save state
    public static void save(RoomInventory inventory, BookingHistory history) {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(FILE_NAME))) {

            out.writeObject(inventory);
            out.writeObject(history);

            System.out.println("\nState saved successfully.");

        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    // Load state
    public static Object[] load() {
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(FILE_NAME))) {

            RoomInventory inventory = (RoomInventory) in.readObject();
            BookingHistory history = (BookingHistory) in.readObject();

            System.out.println("State restored successfully.");
            return new Object[]{inventory, history};

        } catch (FileNotFoundException e) {
            System.out.println("No previous data found. Starting fresh.");
        } catch (Exception e) {
            System.out.println("Corrupted data. Starting fresh.");
        }

        // fallback (safe state)
        return new Object[]{new RoomInventory(), new BookingHistory()};
    }
}

// ===== Main =====
public class bookmystay{

    public static void main(String[] args) {

        System.out.println("====== Book My Stay v12.0 ======\n");

        // Load persisted state
        Object[] data = PersistenceService.load();
        RoomInventory inventory = (RoomInventory) data[0];
        BookingHistory history = (BookingHistory) data[1];

        // Simulate booking
        Reservation r1 = new Reservation("R201", "Ananya", "Single Room");

        if (inventory.getAvailability(r1.getRoomType()) > 0) {
            inventory.decrement(r1.getRoomType());
            history.add(r1);
            System.out.println("Booking confirmed: " + r1.getId());
        } else {
            System.out.println("Booking failed: No availability");
        }

        // Display current state
        inventory.display();
        history.display();

        // Save before shutdown
        PersistenceService.save(inventory, history);

        System.out.println("\nSystem ready for restart.");
    }
}
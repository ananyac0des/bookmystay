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

        System.out.println("=====================================");
        System.out.println("   Welcome to Book My Stay App");
        System.out.println("   Hotel Booking System v4.0");
        System.out.println("=====================================\n");

        // Initialize domain objects (catalog)
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        Map<String, Room> roomCatalog = new HashMap<>();
        roomCatalog.put(single.getRoomType(), single);
        roomCatalog.put(doubleRoom.getRoomType(), doubleRoom);
        roomCatalog.put(suite.getRoomType(), suite);

        // Initialize inventory (state)
        RoomInventory inventory = new RoomInventory();

        // Initialize search service (read-only)
        RoomSearchService searchService = new RoomSearchService(inventory, roomCatalog);

        // Guest performs search
        searchService.searchAvailableRooms();

        System.out.println("Search completed. (No state modified)");
    }
}
import java.util.HashMap;
import java.util.Map;

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
    public SingleRoom() {
        super(1, "Small", 1500);
    }

    public String getRoomType() {
        return "Single Room";
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super(2, "Medium", 2500);
    }

    public String getRoomType() {
        return "Double Room";
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super(3, "Large", 5000);
    }

    public String getRoomType() {
        return "Suite Room";
    }
}

/**
 * Inventory class - Centralized management
 */
class RoomInventory {
    private HashMap<String, Integer> inventory;

    // Constructor initializes availability
    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 2);
    }

    // Get availability
    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    // Update availability (controlled)
    public void updateAvailability(String roomType, int newCount) {
        inventory.put(roomType, newCount);
    }

    // Display full inventory
    public void displayInventory() {
        System.out.println("Current Room Inventory:");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " → Available: " + entry.getValue());
        }
    }
}

/**
 * Main class
 */
public class bookmystay {

    public static void main(String[] args) {

        System.out.println("=====================================");
        System.out.println("   Welcome to Book My Stay App");
        System.out.println("   Hotel Booking System v3.0");
        System.out.println("=====================================\n");

        // Room objects (domain)
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
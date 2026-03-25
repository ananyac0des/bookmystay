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

// ===== Inventory =====
class RoomInventory {
    private HashMap<String, Integer> inventory = new HashMap<>();

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

// ===== Reservation =====
class Reservation {
    private String guestName;
    private String roomType;
    private String reservationId;

    public Reservation(String guestName, String roomType, String reservationId) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.reservationId = reservationId;
    }

    public String getReservationId() { return reservationId; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

// ===== Booking Queue =====
class BookingQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.offer(r);
    }

    public Reservation next() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

// ===== Booking Service =====
class BookingService {
    private RoomInventory inventory;
    private Set<String> allocatedIds = new HashSet<>();
    private Map<String, String> confirmedReservations = new HashMap<>();

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void process(BookingQueue queue) {
        while (!queue.isEmpty()) {
            Reservation r = queue.next();
            String type = r.getRoomType();

            if (inventory.getAvailability(type) > 0) {
                String id = r.getReservationId();

                if (!allocatedIds.contains(id)) {
                    allocatedIds.add(id);
                    confirmedReservations.put(id, type);
                    inventory.decrement(type);

                    System.out.println("Confirmed: " + id + " (" + r.getGuestName() + ")");
                }
            } else {
                System.out.println("Failed: " + r.getGuestName());
            }
        }
    }

    public boolean isValidReservation(String reservationId) {
        return confirmedReservations.containsKey(reservationId);
    }
}

// ===== Add-On Service =====
class AddOnService {
    private String name;
    private double price;

    public AddOnService(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
}

// ===== Add-On Manager =====
class AddOnServiceManager {

    // reservationId -> list of services
    private Map<String, List<AddOnService>> serviceMap = new HashMap<>();

    // Attach service
    public void addService(String reservationId, AddOnService service) {
        serviceMap
                .computeIfAbsent(reservationId, k -> new ArrayList<>())
                .add(service);

        System.out.println("Added service: " + service.getName() + " to " + reservationId);
    }

    // Calculate total cost
    public double calculateTotal(String reservationId) {
        List<AddOnService> services = serviceMap.getOrDefault(reservationId, new ArrayList<>());
        double total = 0;

        for (AddOnService s : services) {
            total += s.getPrice();
        }

        return total;
    }

    // Display services
    public void displayServices(String reservationId) {
        List<AddOnService> services = serviceMap.get(reservationId);

        if (services == null || services.isEmpty()) {
            System.out.println("No add-ons for " + reservationId);
            return;
        }

        System.out.println("\nServices for " + reservationId + ":");
        for (AddOnService s : services) {
            System.out.println("- " + s.getName() + " (₹" + s.getPrice() + ")");
        }

        System.out.println("Total Add-On Cost: ₹" + calculateTotal(reservationId));
    }
}

// ===== Main =====
public class bookmystay {

    public static void main(String[] args) {

        System.out.println("========== Book My Stay v7.0 ==========\n");

        RoomInventory inventory = new RoomInventory();
        BookingQueue queue = new BookingQueue();
        BookingService bookingService = new BookingService(inventory);

        // Create reservations
        Reservation r1 = new Reservation("Ananya", "Single Room", "R101");
        Reservation r2 = new Reservation("Rahul", "Double Room", "R102");

        queue.addRequest(r1);
        queue.addRequest(r2);

        // Process bookings
        bookingService.process(queue);

        // Add-on services
        AddOnService wifi = new AddOnService("WiFi", 200);
        AddOnService breakfast = new AddOnService("Breakfast", 300);
        AddOnService spa = new AddOnService("Spa", 800);

        AddOnServiceManager manager = new AddOnServiceManager();

        // Attach services (only if reservation valid)
        if (bookingService.isValidReservation("R101")) {
            manager.addService("R101", wifi);
            manager.addService("R101", breakfast);
        }

        if (bookingService.isValidReservation("R102")) {
            manager.addService("R102", spa);
        }

        // Display
        manager.displayServices("R101");
        manager.displayServices("R102");

        System.out.println("\nCore booking unchanged. Add-ons handled separately.");
    }
}
import java.util.*;

// ===== Reservation =====
class Reservation {
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
}

// ===== Thread-Safe Inventory =====
class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 1);
    }

    // Critical section
    public synchronized boolean allocate(String type) {
        int available = inventory.getOrDefault(type, 0);

        if (available > 0) {
            inventory.put(type, available - 1);
            return true;
        }
        return false;
    }

    public synchronized void display() {
        System.out.println("\nFinal Inventory:");
        for (Map.Entry<String, Integer> e : inventory.entrySet()) {
            System.out.println(e.getKey() + " → " + e.getValue());
        }
    }
}

// ===== Shared Booking Queue =====
class BookingQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public synchronized void add(Reservation r) {
        queue.offer(r);
    }

    public synchronized Reservation getNext() {
        return queue.poll();
    }
}

// ===== Concurrent Booking Processor =====
class BookingProcessor implements Runnable {

    private BookingQueue queue;
    private RoomInventory inventory;
    private Set<String> allocatedIds;

    public BookingProcessor(BookingQueue queue,
                            RoomInventory inventory,
                            Set<String> allocatedIds) {
        this.queue = queue;
        this.inventory = inventory;
        this.allocatedIds = allocatedIds;
    }

    @Override
    public void run() {
        while (true) {

            Reservation r;

            // Critical section for queue
            synchronized (queue) {
                r = queue.getNext();
            }

            if (r == null) break;

            // Allocate safely
            boolean success = inventory.allocate(r.getRoomType());

            synchronized (allocatedIds) {
                if (success) {
                    allocatedIds.add(r.getId());
                    System.out.println(Thread.currentThread().getName()
                            + " CONFIRMED " + r.getId());
                } else {
                    System.out.println(Thread.currentThread().getName()
                            + " FAILED " + r.getId());
                }
            }
        }
    }
}

// ===== Main =====
public class bookmystay {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("====== Book My Stay v11.0 ======\n");

        RoomInventory inventory = new RoomInventory();
        BookingQueue queue = new BookingQueue();

        // Shared set (must be synchronized)
        Set<String> allocatedIds = Collections.synchronizedSet(new HashSet<>());

        // Simulate concurrent requests
        queue.add(new Reservation("R1", "Ananya", "Single Room"));
        queue.add(new Reservation("R2", "Rahul", "Single Room"));
        queue.add(new Reservation("R3", "Priya", "Single Room"));
        queue.add(new Reservation("R4", "Karan", "Double Room"));

        // Create threads
        Thread t1 = new Thread(new BookingProcessor(queue, inventory, allocatedIds), "Thread-1");
        Thread t2 = new Thread(new BookingProcessor(queue, inventory, allocatedIds), "Thread-2");

        // Start threads
        t1.start();
        t2.start();

        // Wait for completion
        t1.join();
        t2.join();

        inventory.display();

        System.out.println("\nAllocated Reservations: " + allocatedIds);
    }
}
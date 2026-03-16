import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.LinkedList;

// 1. Immutable Domain Model for a Request
class BookingRequest {
    private String guestName;
    private String roomType;

    public BookingRequest(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

// 2. Shared Resource (Critical Section requiring Thread Safety)
class InventoryService {
    private Map<String, Integer> inventory;

    public InventoryService() {
        inventory = new HashMap<>();
        // Purposefully minimal inventory to strictly test race condition prevention
        inventory.put("Double Room", 2); 
    }

    // Keyword 'synchronized' ensures only one thread can execute this method at a time
    // on this specific InventoryService instance. This defines our Critical Section.
    public synchronized boolean processBookingUnderLock(BookingRequest request) {
        String roomType = request.getRoomType();
        int available = inventory.getOrDefault(roomType, 0);

        if (available > 0) {
            // Simulate some database/network processing delay to highly increase the chance
            // of a Race Condition if synchronization were missing.
            try {
                Thread.sleep(50); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Safe State Mutation
            inventory.put(roomType, available - 1);
            System.out.println("[SUCCESS] Thread " + Thread.currentThread().getName() + 
                               " booked for " + request.getGuestName() + ". Remaining: " + (available - 1));
            return true;
        } else {
            System.out.println("[FAILED]  Thread " + Thread.currentThread().getName() + 
                               " failed to book for " + request.getGuestName() + ". No inventory.");
            return false;
        }
    }
    
    public void printInventoryStatus() {
        System.out.println("Final Inventory Status: " + inventory);
    }
}

// 3. Worker Thread Task taking from a shared, but thread-safe queue.
class ConcurrentBookingProcessor implements Runnable {
    private Queue<BookingRequest> sharedQueue;
    private InventoryService sharedInventory;

    public ConcurrentBookingProcessor(Queue<BookingRequest> sharedQueue, InventoryService sharedInventory) {
        this.sharedQueue = sharedQueue;
        this.sharedInventory = sharedInventory;
    }

    @Override
    public void run() {
        while (true) {
            BookingRequest requestToProcess = null;
            
            // Protect the queue reading/polling operation
            synchronized (sharedQueue) {
                if (sharedQueue.isEmpty()) {
                    break; // Exhausted work, thread exits gracefully
                }
                requestToProcess = sharedQueue.poll();
            }

            if (requestToProcess != null) {
                // Let the safe service handle the complex internal lock
                sharedInventory.processBookingUnderLock(requestToProcess);
            }
        }
    }
}

public class UseCase11ConcurrentBookingSimulation {

    public static void main(String[] args) {
        System.out.println("======================================");
        System.out.println("        BOOK MY STAY APP              ");
        System.out.println(" Use Case 11: Concurrent Thread Safety");
        System.out.println("======================================\n");

        InventoryService sharedInventory = new InventoryService();
        Queue<BookingRequest> sharedQueue = new LinkedList<>();

        // 1. Populate Queue (5 people want the 2 available double rooms simultaneously)
        sharedQueue.offer(new BookingRequest("Alice", "Double Room"));
        sharedQueue.offer(new BookingRequest("Bob", "Double Room"));
        sharedQueue.offer(new BookingRequest("Charlie", "Double Room"));
        sharedQueue.offer(new BookingRequest("Diana", "Double Room"));
        sharedQueue.offer(new BookingRequest("Eve", "Double Room"));

        System.out.println("Starting heavy concurrent load simulation...");
        
        // 2. Create 3 worker threads mimicking multiple web server processes connecting to the same DB
        Thread worker1 = new Thread(new ConcurrentBookingProcessor(sharedQueue, sharedInventory), "Worker-1");
        Thread worker2 = new Thread(new ConcurrentBookingProcessor(sharedQueue, sharedInventory), "Worker-2");
        Thread worker3 = new Thread(new ConcurrentBookingProcessor(sharedQueue, sharedInventory), "Worker-3");

        // 3. Start threads almost simultaneously
        worker1.start();
        worker2.start();
        worker3.start();

        // 4. Wait for all threads to finish processing
        try {
            worker1.join();
            worker2.join();
            worker3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\nAll concurrent threads finished.");
        
        // 5. Verify the state held strong
        sharedInventory.printInventoryStatus();
    }
}

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 1. Serializable Domain Models
// Implementing Serializable tells Java these objects can be converted to byte streams
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L; // Ensures version compatibility during deserialization
    
    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    @Override
    public String toString() {
        return "Reservation[" + reservationId + " | " + guestName + " | " + roomType + "]";
    }
}

// Wrapper class to hold all system state we want to persist together
class SystemStateData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Map<String, Integer> inventory;
    private List<Reservation> activeBookings;

    public SystemStateData(Map<String, Integer> inventory, List<Reservation> activeBookings) {
        this.inventory = inventory;
        this.activeBookings = activeBookings;
    }

    public Map<String, Integer> getInventory() { return inventory; }
    public List<Reservation> getActiveBookings() { return activeBookings; }
}

// 2. Service dedicated purely to reading/writing the physical file
class PersistenceService {
    private static final String DATA_FILE = "hotel_system_state.dat";

    // Serialize object to file
    public void saveState(SystemStateData stateData) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(stateData);
            System.out.println(">>> System state successfully persisted to " + DATA_FILE);
        } catch (IOException e) {
            System.err.println(">>> Failed to save system state: " + e.getMessage());
        }
    }

    // Deserialize object from file
    public SystemStateData loadState() {
        File file = new File(DATA_FILE);
        // Graceful handling if file doesn't exist (e.g. first ever startup)
        if (!file.exists()) {
            System.out.println(">>> No previous state found. Starting fresh.");
            return null; 
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            SystemStateData loadedData = (SystemStateData) ois.readObject();
            System.out.println(">>> System state successfully recovered from " + DATA_FILE);
            return loadedData;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(">>> Failed to load system state. File may be corrupted: " + e.getMessage());
            return null;
        }
    }
}

// 3. Orchestrator managing memory and triggering persistence
class SystemManager {
    private PersistenceService persistenceService;
    private Map<String, Integer> inventory;
    private List<Reservation> activeBookings;

    public SystemManager() {
        this.persistenceService = new PersistenceService();
        startup(); // Automatically try to load state on creation
    }

    // Initialization / Recovery Logic
    private void startup() {
        System.out.println("\n--- Initiating System Startup ---");
        SystemStateData recoveredData = persistenceService.loadState();
        
        if (recoveredData != null) {
            // Restore from disk
            this.inventory = recoveredData.getInventory();
            this.activeBookings = recoveredData.getActiveBookings();
            System.out.println("State restored into memory.");
        } else {
            // Initialize fresh state
            this.inventory = new HashMap<>();
            this.inventory.put("Single Room", 5);
            this.inventory.put("Double Room", 5);
            this.activeBookings = new ArrayList<>();
            System.out.println("Initialized fresh system memory.");
        }
        printCurrentState();
    }

    // Shutdown / Persistence Logic
    public void shutdown() {
        System.out.println("\n--- Initiating System Shutdown ---");
        SystemStateData currentData = new SystemStateData(inventory, activeBookings);
        persistenceService.saveState(currentData);
        System.out.println("System safely halted.\n");
    }

    // Business Logic Examples
    public void makeBooking(String reqId, String guest, String roomType) {
        int available = inventory.getOrDefault(roomType, 0);
        if (available > 0) {
            inventory.put(roomType, available - 1);
            activeBookings.add(new Reservation(reqId, guest, roomType));
            System.out.println("[Booking Added] " + guest + " booked a " + roomType);
        }
    }

    public void printCurrentState() {
        System.out.println("Current Memory State:");
        System.out.println(" - Inventory: " + inventory);
        System.out.println(" - Bookings: " + activeBookings.size() + " active records.");
        for (Reservation r : activeBookings) {
            System.out.println("    " + r);
        }
    }
}

public class UseCase12DataPersistenceRecovery {

    public static void main(String[] args) {
        System.out.println("======================================");
        System.out.println("        BOOK MY STAY APP              ");
        System.out.println(" Use Case 12: Persistence & Recovery  ");
        System.out.println("======================================");

        // --- PHASE 1: Initial Run ---
        System.out.println("\n# PHASE 1: Running system and mutating state");
        SystemManager systemA = new SystemManager();
        
        // Mutate state
        systemA.makeBooking("RES-01", "Alice Smith", "Single Room");
        systemA.makeBooking("RES-02", "Bob Jones", "Double Room");
        systemA.makeBooking("RES-03", "Charlie Davis", "Single Room");
        
        systemA.printCurrentState();
        
        // Save state to disk
        systemA.shutdown();

        // --- SIMULATED HARD CRASH / RESTART ---
        System.out.println("======================================");
        System.out.println("       [ POWER CYCLE SIMULATION ]     ");
        System.out.println("======================================");

        // --- PHASE 2: Recovery Run ---
        System.out.println("\n# PHASE 2: Rebooting system. Verifying state survival");
        // Creating a completely new memory instance
        SystemManager systemB = new SystemManager(); 
        
        // Just demonstrating the system can continue operating
        System.out.println("\nContinuing operations on recovered state...");
        systemB.makeBooking("RES-04", "Diana Evans", "Double Room");
        systemB.printCurrentState();
    }
}

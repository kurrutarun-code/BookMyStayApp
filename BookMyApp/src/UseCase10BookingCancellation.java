import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

// 1. Domain Model Tracking Status
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;
    private String roomId;
    private String status;

    public Reservation(String reservationId, String guestName, String roomType, String roomId) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
        this.status = "CONFIRMED";
    }

    public String getReservationId() { return reservationId; }
    public String getRoomType() { return roomType; }
    public String getRoomId() { return roomId; }
    public String getStatus() { return status; }
    
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Reservation[" + reservationId + " | " + guestName + " | " + roomId + " | " + status + "]";
    }
}

// 2. Core Service managing availability AND the rollback stack for released rooms
class InventoryService {
    private Map<String, Integer> inventory;
    // LIFO Stack to track the most recently released room IDs to be reused first
    private Stack<String> releasedRoomIds;

    public InventoryService() {
        inventory = new HashMap<>();
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 2);
        
        releasedRoomIds = new Stack<>();
    }

    // Standard allocation (simplified for this use case to focus on cancellation)
    public boolean checkAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0) > 0;
    }

    public void decrementInventory(String roomType) {
        if (checkAvailability(roomType)) {
            inventory.put(roomType, inventory.get(roomType) - 1);
        }
    }
    
    // Recovery Logic: Restore inventory count
    public void restoreInventory(String roomType) {
        inventory.put(roomType, inventory.getOrDefault(roomType, 0) + 1);
        System.out.println("Inventory restored for: " + roomType);
    }
    
    // Recovery Logic: Push released room ID to LIFO stack for safe reuse later
    public void pushReleasedRoomId(String roomId) {
        releasedRoomIds.push(roomId);
        System.out.println("Room ID '" + roomId + "' pushed to availability stack (LIFO).");
    }
    
    public void printInventoryStatus() {
        System.out.println("\n--- Current Inventory Status ---");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("Released Rooms in Stack: " + releasedRoomIds);
    }
}

// 3. Orchestrates safe rollback of operations
class CancellationService {
    private InventoryService inventoryService;
    private Map<String, Reservation> activeBookings;

    public CancellationService(InventoryService inventoryService, Map<String, Reservation> activeBookings) {
        this.inventoryService = inventoryService;
        this.activeBookings = activeBookings;
    }

    public void processCancellation(String reservationId) {
        System.out.println("\nProcessing cancellation for: " + reservationId);
        
        // Guard 1: Verify Reservation Exists
        Reservation targetReservation = activeBookings.get(reservationId);
        if (targetReservation == null) {
            System.out.println("CRITICAL FAILURE: Reservation ID not found.");
            return;
        }
        
        // Guard 2: Verify Reservation is active (Idempotency check)
        if ("CANCELLED".equals(targetReservation.getStatus())) {
            System.out.println("WARNING: Reservation is already cancelled.");
            return;
        }

        // Controlled Rollback Sequence
        // 1. Mark as cancelled
        targetReservation.setStatus("CANCELLED");
        
        // 2. Restore total inventory count
        inventoryService.restoreInventory(targetReservation.getRoomType());
        
        // 3. Return allocated room ID to Stack
        inventoryService.pushReleasedRoomId(targetReservation.getRoomId());
        
        System.out.println("Cancellation Successful: State rolled back safely.");
    }
}

public class UseCase10BookingCancellation {

    public static void main(String[] args) {
        System.out.println("======================================");
        System.out.println("        BOOK MY STAY APP              ");
        System.out.println("  Use Case 10: Cancellation Rollback  ");
        System.out.println("======================================\n");

        InventoryService inventoryService = new InventoryService();
        Map<String, Reservation> activeBookings = new HashMap<>();

        // Simulate existing fully confirmed bookings with previously deducted inventory
        Reservation res1 = new Reservation("RES-001", "Alice Smith", "Single Room", "SIN-01");
        inventoryService.decrementInventory("Single Room");
        activeBookings.put(res1.getReservationId(), res1);

        Reservation res2 = new Reservation("RES-002", "Bob Jones", "Double Room", "DOU-01");
        inventoryService.decrementInventory("Double Room");
        activeBookings.put(res2.getReservationId(), res2);
        
        Reservation res3 = new Reservation("RES-003", "Charlie Davis", "Single Room", "SIN-02");
        inventoryService.decrementInventory("Single Room");
        activeBookings.put(res3.getReservationId(), res3);
        
        inventoryService.printInventoryStatus();
        for (Reservation r : activeBookings.values()) {
            System.out.println(r);
        }

        CancellationService cancellationService = new CancellationService(inventoryService, activeBookings);

        System.out.println("\n--- Initiating Cancellations ---");
        
        // Valid Cancellation
        cancellationService.processCancellation("RES-001");
        
        // Valid Cancellation (Second one pushes to LIFO Stack)
        cancellationService.processCancellation("RES-003");
        
        // Invalid Cancellation: Already Cancelled
        cancellationService.processCancellation("RES-001");
        
        // Invalid Cancellation: Doesn't Exist
        cancellationService.processCancellation("RES-999");
        
        inventoryService.printInventoryStatus();
        
        System.out.println("\n--- Final Bookings Database State ---");
        for (Reservation r : activeBookings.values()) {
            System.out.println(r);
        }
    }
}

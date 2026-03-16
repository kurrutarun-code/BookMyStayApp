import java.util.*;

// Domain Models
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

class InventoryService {
    private Map<String, Integer> inventory;

    public InventoryService() {
        inventory = new HashMap<>();
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 2);
        inventory.put("Suite Room", 1);
    }

    public boolean checkAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0) > 0;
    }

    public void decrementInventory(String roomType) {
        if (checkAvailability(roomType)) {
            inventory.put(roomType, inventory.get(roomType) - 1);
        }
    }
    
    public void printInventory() {
        System.out.println("Current Inventory:");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}

class BookingService {
    private Queue<BookingRequest> requestQueue;
    private InventoryService inventoryService;
    private Map<String, Set<String>> allocatedRooms;
    private int roomCounter = 1;

    public BookingService(InventoryService inventoryService) {
        this.requestQueue = new LinkedList<>();
        this.inventoryService = inventoryService;
        this.allocatedRooms = new HashMap<>();
    }

    public void addRequest(BookingRequest request) {
        requestQueue.offer(request);
        System.out.println("Added request to queue: " + request.getGuestName() + " for " + request.getRoomType());
    }

    public void processRequests() {
        System.out.println("\n--- Processing Booking Requests ---");
        while (!requestQueue.isEmpty()) {
            BookingRequest request = requestQueue.poll();
            System.out.println("\nProcessing request for: " + request.getGuestName() + " (" + request.getRoomType() + ")");
            
            if (inventoryService.checkAvailability(request.getRoomType())) {
                String roomId = generateUniqueRoomId(request.getRoomType());
                
                // Allocate room (Set prevents duplicates by design, though we generated a unique ID)
                allocatedRooms.putIfAbsent(request.getRoomType(), new HashSet<>());
                allocatedRooms.get(request.getRoomType()).add(roomId);
                
                // Update inventory immediately
                inventoryService.decrementInventory(request.getRoomType());
                
                System.out.println("Reservation Confirmed! Room Allocated: " + roomId);
            } else {
                System.out.println("Reservation Failed! No availability for " + request.getRoomType());
            }
        }
    }
    
    private String generateUniqueRoomId(String roomType) {
        String prefixCode = roomType.substring(0, 3).toUpperCase();
        String generatedId;
        do {
            generatedId = prefixCode + "-" + String.format("%03d", roomCounter++);
            // Check against all allocated rooms to ensure absolute uniqueness across all operations
        } while (isRoomIdReused(generatedId));
        return generatedId;
    }
    
    private boolean isRoomIdReused(String roomId) {
        for (Set<String> rooms : allocatedRooms.values()) {
            if (rooms.contains(roomId)) {
                return true;
            }
        }
        return false;
    }
    
    public void printAllocatedRooms() {
        System.out.println("\nAllocated Rooms:");
        for (Map.Entry<String, Set<String>> entry : allocatedRooms.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}

public class UseCase6RoomAllocationService {
    public static void main(String[] args) {
        System.out.println("======================================");
        System.out.println("        BOOK MY STAY APP              ");
        System.out.println("    Use Case 6: Room Allocation       ");
        System.out.println("======================================");
        
        InventoryService inventoryService = new InventoryService();
        inventoryService.printInventory();
        System.out.println();
        
        BookingService bookingService = new BookingService(inventoryService);
        
        // Add multiple requests, including those that will exceed inventory
        bookingService.addRequest(new BookingRequest("Alice", "Single Room"));
        bookingService.addRequest(new BookingRequest("Bob", "Double Room"));
        bookingService.addRequest(new BookingRequest("Charlie", "Single Room"));
        bookingService.addRequest(new BookingRequest("Diana", "Suite Room"));
        bookingService.addRequest(new BookingRequest("Eve", "Single Room")); // Should fail
        bookingService.addRequest(new BookingRequest("Frank", "Double Room"));
        bookingService.addRequest(new BookingRequest("Grace", "Double Room")); // Should fail
        
        bookingService.processRequests();
        
        System.out.println("\n--- Final System State ---");
        inventoryService.printInventory();
        bookingService.printAllocatedRooms();
    }
}

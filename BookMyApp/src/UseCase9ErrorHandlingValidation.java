import java.util.HashMap;
import java.util.Map;

// 1. Custom Exception for domain-specific errors
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// 2. Domain Model representing a request
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

// 3. Validator to check inputs before reaching core logic
class BookingValidator {
    public static void validateRequest(BookingRequest request) throws InvalidBookingException {
        if (request == null) {
            throw new InvalidBookingException("Booking request cannot be null.");
        }
        
        if (request.getGuestName() == null || request.getGuestName().trim().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }
        
        if (request.getRoomType() == null || request.getRoomType().trim().isEmpty()) {
            throw new InvalidBookingException("Room type cannot be empty.");
        }
    }
}

// 4. Inventory Service with strict validation against negative states
class InventoryService {
    private Map<String, Integer> inventory;

    public InventoryService() {
        inventory = new HashMap<>();
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 1);
        inventory.put("Suite Room", 1);
    }

    // Attempt to process a parsed, validated request against the current state
    public void processBooking(BookingRequest request) throws InvalidBookingException {
        // First validate the raw inputs
        BookingValidator.validateRequest(request);
        
        String roomType = request.getRoomType();
        
        // Guard Check: Does the room type exist?
        if (!inventory.containsKey(roomType)) {
            throw new InvalidBookingException("Invalid room type requested: '" + roomType + "'");
        }
        
        // Guard Check: Is there availability?
        int available = inventory.get(roomType);
        if (available <= 0) {
            throw new InvalidBookingException("No availability for room type: '" + roomType + "'");
        }
        
        // Safe State Change: We verified availability, so we decrement
        inventory.put(roomType, available - 1);
        System.out.println("Processing Success: Confirmed " + roomType + " for " + request.getGuestName());
    }
    
    public void printInventory() {
        System.out.println("Current Inventory:");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}

public class UseCase9ErrorHandlingValidation {

    public static void main(String[] args) {
        System.out.println("======================================");
        System.out.println("        BOOK MY STAY APP              ");
        System.out.println(" Use Case 9: Validation & Errors      ");
        System.out.println("======================================\n");

        InventoryService inventoryService = new InventoryService();
        inventoryService.printInventory();
        System.out.println("\n--- Processing Bookings ---");

        // Set up test scenarios: happy paths and explicit error boundaries
        BookingRequest[] requests = {
            new BookingRequest("Alice Smith", "Single Room"),    // Valid: Success
            new BookingRequest("Bob Jones", "Double Room"),    // Valid: Success
            new BookingRequest("", "Double Room"),             // Invalid: Empty Guest Name
            new BookingRequest("Charlie Davis", "Penthouse"),  // Invalid: Non-existent Room Type
            new BookingRequest("Diana Evans", "Double Room"),  // Invalid: Out of Stock (Bob took the only one)
            null,                                          // Invalid: Null request object
            new BookingRequest("Eve Foster", "Single Room")    // Valid: Success (ensuring the system kept running)
        };

        for (int i = 0; i < requests.length; i++) {
            BookingRequest req = requests[i];
            
            // Context information for the log
            String guestName = (req != null && req.getGuestName() != null) ? req.getGuestName() : "Unknown Guest";
            String roomType = (req != null && req.getRoomType() != null) ? req.getRoomType() : "Unknown Room";
            
            System.out.print("Request " + (i + 1) + " [" + guestName + " | " + roomType + "] -> ");
            
            // Try-Catch block demonstrates "Fail Fast, Graceful Recovery"
            try {
                // The service could throw our Custom Exception
                inventoryService.processBooking(req);
            } catch (InvalidBookingException e) {
                // Caught domains-specific error without crashing the main thread loop
                System.out.println("FAILED: " + e.getMessage());
            } catch (Exception e) {
                // Catch all other unexpected errors
                System.out.println("CRITICAL FAILURE: " + e.getMessage());
            }
        }

        System.out.println("\n--- Final System State ---");
        // Inventory should cleanly reflect ONLY the successfully processed bookings
        inventoryService.printInventory();
    }
}

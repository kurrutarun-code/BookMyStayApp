import java.util.HashMap;
import java.util.Map;

/**
 * Book My Stay App
 * Use Case 3: Centralized Room Inventory Management
 *
 * Demonstrates centralized inventory using HashMap.
 *
 * @author Student
 * @version 3.1
 */

public class BookMyAppMain {

    public static void main(String[] args) {

        System.out.println("====================================");
        System.out.println("        BOOK MY STAY APP            ");
        System.out.println("     Hotel Booking System v3.1      ");
        System.out.println("====================================");

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();

        // Display inventory
        inventory.displayInventory();

        // Check availability
        int available = inventory.getAvailability("Double Room");

        System.out.println("\nDouble Room Available: " + available);

        // Update availability
        System.out.println("\nUpdating Double Room availability to 2");

        inventory.updateAvailability("Double Room", 2);

        // Display updated inventory
        inventory.displayInventory();

        System.out.println("\nApplication finished.");
    }
}

/**
 * RoomInventory class
 * Manages centralized room availability using HashMap
 *
 * @version 3.0
 */
class RoomInventory {

    private HashMap<String, Integer> inventory;

    // Constructor
    public RoomInventory() {

        inventory = new HashMap<>();

        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 2);
    }

    // Get availability
    public int getAvailability(String roomType) {

        if (inventory.containsKey(roomType)) {
            return inventory.get(roomType);
        }

        return 0;
    }

    // Update availability
    public void updateAvailability(String roomType, int count) {

        if (inventory.containsKey(roomType)) {
            inventory.put(roomType, count);
        }
    }

    // Display inventory
    public void displayInventory() {

        System.out.println("\nCurrent Room Inventory");

        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {

            System.out.println(entry.getKey() + " : " + entry.getValue() + " rooms available");

        }
    }
}
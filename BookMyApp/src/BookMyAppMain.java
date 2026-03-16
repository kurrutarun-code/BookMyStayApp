import java.util.HashMap;
import java.util.Map;

/**
 * Book My Stay App
 * Use Case 4: Room Search & Availability Check
 *
 * Demonstrates safe read-only search on centralized inventory
 * without modifying system state.
 *
 * @author Student
 * @version 4.0
 */

public class BookMyAppMain {

    public static void main(String[] args) {

        System.out.println("======================================");
        System.out.println("        BOOK MY STAY APP              ");
        System.out.println("    Hotel Booking System v4.0         ");
        System.out.println("======================================");

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();

        // Initialize room objects
        Room[] rooms = {
                new Room("Single Room", 1, 200, 100.0),
                new Room("Double Room", 2, 350, 180.0),
                new Room("Suite Room", 3, 500, 350.0)
        };

        // Perform room search (read-only)
        System.out.println("\nSearching for available rooms...");

        boolean anyAvailable = false;

        for (Room room : rooms) {
            int available = inventory.getAvailability(room.getRoomType());

            if (available > 0) {
                anyAvailable = true;
                System.out.println("\nRoom Type : " + room.getRoomType());
                System.out.println("Beds      : " + room.getBeds());
                System.out.println("Size      : " + room.getSize() + " sq.ft");
                System.out.println("Price     : $" + room.getPrice());
                System.out.println("Available : " + available);
            }
        }

        if (!anyAvailable) {
            System.out.println("Sorry, no rooms are currently available.");
        }

        System.out.println("\nRoom search completed. Inventory not modified.");
    }
}

/**
 * Room class represents room details (domain model)
 */
class Room {

    private String roomType;
    private int beds;
    private int size;
    private double price;

    public Room(String roomType, int beds, int size, double price) {
        this.roomType = roomType;
        this.beds = beds;
        this.size = size;
        this.price = price;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getBeds() {
        return beds;
    }

    public int getSize() {
        return size;
    }

    public double getPrice() {
        return price;
    }
}

/**
 * RoomInventory class manages centralized availability
 */
class RoomInventory {

    private HashMap<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 2);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void updateAvailability(String roomType, int count) {
        if (inventory.containsKey(roomType)) {
            inventory.put(roomType, count);
        }
    }
}
import java.util.*;

// Domain Models

// 1. Represents an individual optional offering
class AddOnService {
    private String serviceName;
    private double cost;

    public AddOnService(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getCost() {
        return cost;
    }
    
    @Override
    public String toString() {
        return serviceName + " ($" + cost + ")";
    }
}

// 2. Represents the core booking
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }
    
    public String getRoomType() {
        return roomType;
    }
}

// 3. Manages association between reservations and selected services
class AddOnServiceManager {
    // Maps Reservation ID to a List of AddOnServices
    private Map<String, List<AddOnService>> reservationServicesMap;

    public AddOnServiceManager() {
        this.reservationServicesMap = new HashMap<>();
    }

    // Attach a service to a reservation
    public void addServiceToReservation(String reservationId, AddOnService service) {
        // If the reservationId isn't tracking services yet, initialize the List
        reservationServicesMap.putIfAbsent(reservationId, new ArrayList<>());
        reservationServicesMap.get(reservationId).add(service);
        System.out.println("Added " + service.getServiceName() + " to Reservation " + reservationId);
    }

    // Retrieve all services for a given reservation
    public List<AddOnService> getServicesForReservation(String reservationId) {
        return reservationServicesMap.getOrDefault(reservationId, new ArrayList<>());
    }

    // Calculate total additional cost for a given reservation
    public double calculateTotalAdditionalCost(String reservationId) {
        List<AddOnService> services = getServicesForReservation(reservationId);
        double totalCost = 0.0;
        for (AddOnService service : services) {
            totalCost += service.getCost();
        }
        return totalCost;
    }
    
    // Print the bill details for a reservation
    public void printReservationBill(Reservation reservation) {
        System.out.println("\n--- Bill Details for Reservation: " + reservation.getReservationId() + " ---");
        System.out.println("Guest Name: " + reservation.getGuestName());
        System.out.println("Room Type: " + reservation.getRoomType());
        
        List<AddOnService> services = getServicesForReservation(reservation.getReservationId());
        
        if (services.isEmpty()) {
            System.out.println("No Add-On Services selected.");
        } else {
            System.out.println("Add-On Services:");
            for (AddOnService service : services) {
                System.out.println(" - " + service.toString());
            }
            double totalAdditionalCost = calculateTotalAdditionalCost(reservation.getReservationId());
            System.out.println("Total Additional Cost: $" + totalAdditionalCost);
        }
    }
}

public class UseCase7AddOnServiceSelection {

    public static void main(String[] args) {
        System.out.println("======================================");
        System.out.println("        BOOK MY STAY APP              ");
        System.out.println("    Use Case 7: Add-On Services       ");
        System.out.println("======================================");

        // 1. Available Add-On Services Catalog
        AddOnService breakfast = new AddOnService("Breakfast Buffet", 25.0);
        AddOnService spa = new AddOnService("Spa Access", 50.0);
        AddOnService airportPickup = new AddOnService("Airport Pickup", 40.0);
        AddOnService lateCheckout = new AddOnService("Late Checkout", 30.0);

        // 2. Existing Reservations (Simulating core booking completion)
        Reservation res1 = new Reservation("RES-001", "Alice Smith", "Single Room");
        Reservation res2 = new Reservation("RES-002", "Bob Jones", "Double Room");
        Reservation res3 = new Reservation("RES-003", "Charlie Davis", "Suite Room");

        // 3. Initialize the Manager
        AddOnServiceManager serviceManager = new AddOnServiceManager();

        System.out.println("\n--- Selecting Add-On Services ---");
        
        // Alice selects Breakfast and Spa
        serviceManager.addServiceToReservation(res1.getReservationId(), breakfast);
        serviceManager.addServiceToReservation(res1.getReservationId(), spa);

        // Charlie selects Airport Pickup, Breakfast, and Late Checkout
        serviceManager.addServiceToReservation(res3.getReservationId(), airportPickup);
        serviceManager.addServiceToReservation(res3.getReservationId(), breakfast);
        serviceManager.addServiceToReservation(res3.getReservationId(), lateCheckout);
        
        // Bob selects no add-on services (handled gracefully)

        // 4. Print bills
        serviceManager.printReservationBill(res1);
        serviceManager.printReservationBill(res2);
        serviceManager.printReservationBill(res3);
        
        System.out.println("\nAdd-On service selection completed without modifying core booking logic.");
    }
}

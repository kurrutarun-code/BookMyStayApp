import java.util.LinkedList;
import java.util.Queue;

/**
 * Book My Stay App
 * Use Case 5: Booking Request (First-Come-First-Served)
 *
 * Demonstrates booking request intake using a FIFO queue.
 *
 * Author: Student
 * Version: 5.0
 */

public class BookMyAppMain {

    public static void main(String[] args) {

        System.out.println("======================================");
        System.out.println("        BOOK MY STAY APP              ");
        System.out.println("    Hotel Booking System v5.0         ");
        System.out.println("======================================");

        // Initialize booking request queue
        BookingRequestQueue requestQueue = new BookingRequestQueue();

        // Simulate guest booking requests
        requestQueue.addRequest(new Reservation("Alice", "Single Room"));
        requestQueue.addRequest(new Reservation("Bob", "Double Room"));
        requestQueue.addRequest(new Reservation("Charlie", "Suite Room"));
        requestQueue.addRequest(new Reservation("Diana", "Double Room"));

        // Display current queue
        System.out.println("\nCurrent Booking Requests in FIFO order:\n");
        requestQueue.displayQueue();

        System.out.println("\nBooking request intake completed. Inventory not modified.");
    }
}

/**
 * Reservation represents a guest's booking request
 */
class Reservation {

    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

/**
 * BookingRequestQueue manages booking requests in FIFO order
 */
class BookingRequestQueue {

    private Queue<Reservation> queue;

    public BookingRequestQueue() {
        queue = new LinkedList<>();
    }

    // Add booking request
    public void addRequest(Reservation reservation) {
        queue.add(reservation);
        System.out.println("Added booking request: " + reservation.getGuestName() +
                " for " + reservation.getRoomType());
    }

    // Display queued requests
    public void displayQueue() {
        int position = 1;
        for (Reservation r : queue) {
            System.out.println(position + ". Guest: " + r.getGuestName() +
                    " | Room Type: " + r.getRoomType());
            position++;
        }
    }

    // Poll next request (optional for allocation stage)
    public Reservation pollNext() {
        return queue.poll(); // Returns null if empty
    }

    // Check if queue is empty
    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
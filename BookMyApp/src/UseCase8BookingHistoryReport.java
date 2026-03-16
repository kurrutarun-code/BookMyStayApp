import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// 1. Represents a confirmed reservation with extended details
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;
    private Date bookingDate;
    private String status;

    public Reservation(String reservationId, String guestName, String roomType, Date bookingDate) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.bookingDate = bookingDate;
        this.status = "CONFIRMED";
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

    public Date getBookingDate() {
        return bookingDate;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.format("ID: %-10s | Guest: %-15s | Room: %-12s | Status: %-10s | Date: %s",
                reservationId, guestName, roomType, status, bookingDate.toString());
    }
}

// 2. Maintains a chronological record of confirmed bookings
class BookingHistory {
    // List preserves insertion order, serving as our chronological audit trail
    private List<Reservation> historyList;

    public BookingHistory() {
        this.historyList = new ArrayList<>();
    }

    // Add a confirmed reservation to the history
    public void addReservation(Reservation reservation) {
        historyList.add(reservation);
        System.out.println("Recorded reservation in history: " + reservation.getReservationId());
    }

    // Retrieve all historical reservations
    public List<Reservation> getAllReservations() {
        // Return a copy to prevent external modification (immutability for audits)
        return new ArrayList<>(historyList);
    }
}

// 3. Generates summaries and reports from stored historical data
class BookingReportService {
    private BookingHistory bookingHistory;

    public BookingReportService(BookingHistory bookingHistory) {
        this.bookingHistory = bookingHistory;
    }

    // Generate a simple chronological report
    public void generateChronologicalReport() {
        System.out.println("\n==========================================================================");
        System.out.println("                   ADMINISTRATIVE BOOKING REPORT                          ");
        System.out.println("                       (Chronological Order)                              ");
        System.out.println("==========================================================================");

        List<Reservation> allReservations = bookingHistory.getAllReservations();

        if (allReservations.isEmpty()) {
            System.out.println("No booking history available.");
        } else {
            for (int i = 0; i < allReservations.size(); i++) {
                System.out.println((i + 1) + ". " + allReservations.get(i).toString());
            }
        }
        System.out.println("==========================================================================");
        System.out.println("Total Bookings Processed: " + allReservations.size());
    }
    
    // Generate an analytical report grouped by room type
    public void generateRoomTypeSummaryReport() {
        System.out.println("\n--- Reservation Summary by Room Type ---");
        List<Reservation> allReservations = bookingHistory.getAllReservations();
        
        int singleCount = 0;
        int doubleCount = 0;
        int suiteCount = 0;
        
        for (Reservation res : allReservations) {
            switch (res.getRoomType()) {
                case "Single Room": singleCount++; break;
                case "Double Room": doubleCount++; break;
                case "Suite Room": suiteCount++; break;
            }
        }
        
        System.out.println("Single Rooms Booked : " + singleCount);
        System.out.println("Double Rooms Booked : " + doubleCount);
        System.out.println("Suite Rooms Booked  : " + suiteCount);
    }
}

public class UseCase8BookingHistoryReport {

    public static void main(String[] args) {
        System.out.println("======================================");
        System.out.println("        BOOK MY STAY APP              ");
        System.out.println(" Use Case 8: History & Reporting      ");
        System.out.println("======================================\n");

        BookingHistory history = new BookingHistory();
        BookingReportService reportService = new BookingReportService(history);

        // Simulate a booking flow over time
        System.out.println("--- Simulating Live Booking Flow ---");
        
        // Using current time minus some arbitrary milliseconds to simulate past bookings chronologically
        long currentTime = System.currentTimeMillis();
        
        Reservation res1 = new Reservation("RES-001", "Alice Smith", "Single Room", new Date(currentTime - 1000000));
        history.addReservation(res1);

        Reservation res2 = new Reservation("RES-002", "Bob Jones", "Double Room", new Date(currentTime - 800000));
        history.addReservation(res2);

        Reservation res3 = new Reservation("RES-003", "Charlie Davis", "Suite Room", new Date(currentTime - 500000));
        history.addReservation(res3);
        
        Reservation res4 = new Reservation("RES-004", "Diana Evans", "Single Room", new Date(currentTime - 200000));
        history.addReservation(res4);

        System.out.println("\nTime advanced... Admin requests reports.\n");

        // Generate reports from historical data without modifying it
        reportService.generateChronologicalReport();
        reportService.generateRoomTypeSummaryReport();
    }
}

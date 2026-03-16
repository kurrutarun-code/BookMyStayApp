
abstract class Room {

    protected String roomType;
    protected int beds;
    protected int size;
    protected double price;

    public Room(String roomType, int beds, int size, double price) {
        this.roomType = roomType;
        this.beds = beds;
        this.size = size;
        this.price = price;
    }

    public void displayRoomDetails() {
        System.out.println("Room Type : " + roomType);
        System.out.println("Beds      : " + beds);
        System.out.println("Size      : " + size + " sq.ft");
        System.out.println("Price     : $" + price);
    }
}

    /* Single Room Class */
    class SingleRoom extends Room {

        public SingleRoom() {
            super("Single Room", 1, 200, 100);
        }
    }

    /* Double Room Class */
    class DoubleRoom extends Room {

        public DoubleRoom() {
            super("Double Room", 2, 350, 180);
        }
    }

    /* Suite Room Class */
    class SuiteRoom extends Room {

        public SuiteRoom() {
            super("Suite Room", 3, 500, 350);
        }
    }

    /* Main Class */
    public class BookMyAppMain {

        public static void main(String[] args) {

            System.out.println("===================================");
            System.out.println("        BOOK MY STAY APP           ");
            System.out.println("     Hotel Booking System v2.1     ");
            System.out.println("===================================");

            // Creating room objects (Polymorphism)
            Room singleRoom = new SingleRoom();
            Room doubleRoom = new DoubleRoom();
            Room suiteRoom = new SuiteRoom();

            // Static availability
            int singleAvailable = 5;
            int doubleAvailable = 3;
            int suiteAvailable = 2;

            System.out.println("\nRoom Details & Availability\n");

            singleRoom.displayRoomDetails();
            System.out.println("Available Rooms : " + singleAvailable);
            System.out.println("-----------------------------------");

            doubleRoom.displayRoomDetails();
            System.out.println("Available Rooms : " + doubleAvailable);
            System.out.println("-----------------------------------");

            suiteRoom.displayRoomDetails();
            System.out.println("Available Rooms : " + suiteAvailable);
            System.out.println("-----------------------------------");

            System.out.println("Application finished.");
        }
    }


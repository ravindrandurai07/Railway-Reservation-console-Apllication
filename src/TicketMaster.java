import java.util.InputMismatchException;
import java.util.Scanner;

public class TicketMaster {

    static Train train = new Train();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws InputMismatchException {
        System.out.println("\n\t\t\tWelcome to " + train.getTrainName() + " Train ticket Booking Portal");
        System.out.print("\nPress 1 to continue....");

        while (sc.nextInt() == 1){
            System.out.println("\nWhat are you looking for ?");
            System.out.println(
                            "1. Book Tickets\n" +
                            "2. Cancel Booked Ticket\n" +
                            "3. Get Ticket Details\n" +
                            "4. Show available Tickets\n"
            );
            try{
                switch (sc.nextInt()){
                    case 1 -> book(sc);
                    case 2 -> cancel(sc);
                    case 3 -> getDetails(sc);
                    case 4 -> showAvailability();
                    default -> System.out.println("\nEnter Valid input ra Tharkuri..");
                }
            }
            catch (InputMismatchException e){
                System.out.println("\nEnter Valid input ra Tharkuri..");
                sc.next();
            }

            System.out.print("\nPress 1 to continue....");
        }
        train.printBookedTickets();
        System.out.println("Thank you!....");
    }

    public static void book (Scanner sc){
        System.out.print("\nEnter No of Passengers : ");
        int count = sc.nextByte();
        int i = 1;

        while (i <= count){
            System.out.print("Enter Passenger " + i + " Name : ");sc.nextLine();
            String name = sc.nextLine();
            System.out.print("Enter Passenger " + i + " Age : ");
            int age = sc.nextByte();
            System.out.print("Enter Berth Preference : " +
                    "\t1. Lower" +
                    "\t2. Middle" +
                    "\t3. Upper "
            );
            int preference = sc.nextByte();
            train.bookTicket(name, age, preference);
            i++;
        }
    }
    public static void cancel (Scanner sc){
        System.out.print("\nEnter a valid Passenger ID :");
        boolean result = train.cancelTicket(sc.nextInt());
        if (result)
            System.out.println("Cancellation successful..");
        else
            System.out.println("Cancellation failed...Passenger id not found.");
    }
    public static void getDetails (Scanner sc){
        System.out.print("\nEnter valid Passenger ID ");
        train.getDetails(sc.nextInt());
    }
    public static void showAvailability (){
        System.out.println("\nAvailable Tickets");
        train.getAvailableTickets();
    }
}

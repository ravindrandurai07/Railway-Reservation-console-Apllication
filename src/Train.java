import java.util.*;

public class Train {

    private class Ticket {
        String name;
        int passenger_id;
        String seatType;

        static HashMap<Passenger, Ticket> ticketPassengerMap = new HashMap<>();

        private Ticket(Passenger passenger, Seat seat){
            this.name = passenger.name;
            this.passenger_id = passenger.passenger_id;

            this.seatType = seat.type == 1? "Lower Berth : " : seat.type == 2? "Middle Berth : " : "Upper Berth : ";
            this.seatType += seat.seatNo + 1;
            ticketPassengerMap.put(passenger, this);
        }
        private Ticket (Passenger passenger, String seatType){
            this.name = passenger.name;
            this.passenger_id = passenger.passenger_id;
            this.seatType = seatType.equals(" RAC")? seatType + " " + racCount : seatType + " " + wlCount;
            ticketPassengerMap.put(passenger, this);
        }
        private static void printTicket (Ticket ticket){
            System.out.println("Passenger Name : " + ticket.name);
            System.out.println("Passenger Id : " + ticket.passenger_id);
            System.out.println("You have booked " + ticket.seatType);
        }
    }

    private static class Seat{
        int seatNo;
        int type;
        String berth;
        Passenger passenger;
        static HashMap<Passenger, Seat> passengerSeatMap = new HashMap<>();

        public Seat(Passenger passenger, int seatNo, int type) {
            this.passenger = passenger;
            this.seatNo = seatNo;
            this.type = type;
            this.berth = type == 1? "Lower" : type == 2? "Middle" : "Upper";
            passengerSeatMap.put(passenger, this);
        }
    }


    private static class Passenger {
        String name;
        int age;
        int passenger_id;
        boolean isRac;
        boolean isWl;
        static HashMap<Integer, Passenger> passengerIdMap = new HashMap<>();

        public Passenger(String name, int age) {
            this.age = age;
            this.name = name;
            this.passenger_id = ((int)(Math.random() * 90000) + 10000) + 500000;//5 digit  random number
            Passenger.passengerIdMap.put(passenger_id, this);
        }
    }

    private static final String TRAIN_NAME = "HOMAGE";
    public String getTrainName(){
        return TRAIN_NAME;
    }
    private static final int DEFAULT_SIZE = 12;
    private int racLimit;
    private int racCount;
    private int wlLimit;
    private int wlCount;

    private Seat[] lowerBerth;
    private Seat[] middleBerth;
    private Seat[] upperBerth;

    private Queue<Passenger> rac;
    private Queue<Passenger> wl;

    public Train(){
        this(DEFAULT_SIZE);
    }
    public Train (int size) {
        this.lowerBerth = new Seat[size/ 3];
        this.middleBerth = new Seat[size/ 3];
        this.upperBerth = new  Seat[size/ 3];
        this.wl = new LinkedList<>();
        this.rac = new LinkedList<>();
        this.racLimit = size / 3 + 1;
        this.wlLimit = size / 4 + 1;
    }

//----------------------------------------------------------------------------------------------------------------------
    //Finding the preference to book ticket , Returns false if tickets are not available
//----------------------------------------------------------------------------------------------------------------------
    public void bookTicket (String name, int age, int preference){

        Passenger passenger = new Passenger(name, age);
        Ticket ticket = null;
        boolean isPreferenceAvailable = false;
        Seat[] berth = null;
        switch (preference){
            case 1: {
                isPreferenceAvailable = isAvailable(lowerBerth);
                berth = lowerBerth;
                break;
            }
            case 2:{
                isPreferenceAvailable = isAvailable(middleBerth);
                berth = middleBerth;
                break;
            }
            case 3:{
                isPreferenceAvailable = isAvailable(upperBerth);
                berth = upperBerth;
                break;
            }
        }

        if (isPreferenceAvailable){
            ticket = bookBerth(berth, passenger, preference);
        }
        else {
            if (isAvailable(lowerBerth))
                ticket = bookBerth(lowerBerth, passenger, 1);

            else if (isAvailable(middleBerth))
                ticket = bookBerth(middleBerth, passenger, 2);

            else if (isAvailable(upperBerth))
                ticket = bookBerth(upperBerth, passenger, 3);

            else if (racCount < racLimit){
                passenger.isRac = true;
                ticket = bookRAC(passenger);
            }
            else if (wlCount < wlLimit) {
                passenger.isWl = true;
                ticket = bookWL(passenger);
            }
            else
                ticket = null;
        }
        if (ticket == null) {
            System.out.println("We are sorry to tell you that no tickets are available");
            return;
        }
        System.out.println("Ticket booked successfully..");
        System.out.println("------------------------------");
        Ticket.printTicket(ticket);
        System.out.println("------------------------------");

    }
    private boolean isAvailable (Seat[] berth){
        int i = 0;
        while (i < berth.length){
            if (berth[i] == null)
                return true;
            i++;
        }
        return false;
    }

//----------------------------------------------------------------------------------------------------------------------
    //Booking the ticket and returns the ticket
//----------------------------------------------------------------------------------------------------------------------
    private Ticket bookBerth(Seat[] berth, Passenger passenger, int type){

        int i = 0;
        while (berth[i] != null){
            i++;
        }
        berth[i] = new Seat(passenger, i, type);

        Ticket ticket = new Ticket(passenger, berth[i]);
        return ticket;
    }
    private Ticket  bookRAC (Passenger passenger) {
        racCount++;
        rac.offer(passenger);
        Ticket ticket = new Ticket(passenger, " RAC" );
        return ticket;
    }
    private Ticket bookWL (Passenger passenger) {
        wlCount++;
        wl.offer(passenger);
        Ticket ticket = new Ticket(passenger, " Waiting List");
        return ticket;
    }

//----------------------------------------------------------------------------------------------------------------------
    //Cancellation of tickets
//----------------------------------------------------------------------------------------------------------------------

    public boolean cancelTicket (int passenger_id){
        Passenger passenger = Passenger.passengerIdMap.getOrDefault(passenger_id,null);
        Seat seat = Seat.passengerSeatMap.getOrDefault(passenger, null);
        if (passenger == null){
            return false;
        }
        //If there is a berth allocated
        if (seat != null){
            clearSeat (seat);
            Seat.passengerSeatMap.remove(passenger);
            Ticket.ticketPassengerMap.remove(passenger);
            Seat.passengerSeatMap.remove(passenger);
            Passenger.passengerIdMap.remove(passenger_id);
            update (this.wl, 1);
            update (this.rac, 0);
            return true;
        }
        //If the cancellation is in rac
        if (rac.contains(passenger)){
            rac.remove(passenger);
            Ticket.ticketPassengerMap.remove(passenger);
            Passenger.passengerIdMap.remove(passenger_id);
            racCount--;
            update(rac, 0);
            Passenger nextRACPassenger = null;
            if (!wl.isEmpty()){
                nextRACPassenger = wl.poll();
                nextRACPassenger.isRac = true;
                nextRACPassenger.isWl = false;
                rac.offer(nextRACPassenger);
                racCount++;
                wlCount--;
                update (this.wl, 1);
                update(this.rac, 0);
            }
            System.out.println("Passenger " + passenger.name + " Your RAC ticket has been Cancelled");
            if (nextRACPassenger != null)
                System.out.println("Passenger " + nextRACPassenger.name + " has been moved to RAC " + racCount);
            return true;
        }
        //If the cancellation is for wl
        if (wl.contains(passenger)){
            wl.remove(passenger);
            Ticket.ticketPassengerMap.remove(passenger);
            Passenger.passengerIdMap.remove(passenger_id);
            wlCount--;
            System.out.println("Passenger " + passenger.name + " Your Waiting List Ticket has been cancelled");
            update (this.wl, 1);
            return true;
        }
        return false;
    }
    //Updating the ticket details after cancellation
    private void update (Queue<Passenger> queue, int flag) {
        String indent = flag == 0 ? " RAC " : " Waiting List ";
        int i = 1;
        for (Passenger passenger : queue){
            Ticket ticket = Ticket.ticketPassengerMap.get(passenger);
            ticket.seatType = indent + i;
            i++;
        }
    }

    //Berth Cancellation
    private void clearSeat(Seat seat) {
        int type = seat.type;
        Seat[] berth = type == 1? lowerBerth : type == 2? middleBerth : upperBerth;

        Passenger nextBerthPassenger = null;
        Passenger nextRACpassenger = null;
        if (!rac.isEmpty()) {

            nextBerthPassenger = rac.poll();
            nextBerthPassenger.isRac = false;
            this.racCount--;
            if (!wl.isEmpty()){
                this.wlCount--;
                nextRACpassenger = wl.poll();
                nextRACpassenger.isWl = false;
                rac.offer(nextRACpassenger);
                changeTicketDetails (nextRACpassenger, null);
                this.racCount++;
            }
        }


        int i = 0;
        while (i < berth.length){
            if (berth[i] == seat){
                if (nextBerthPassenger != null){
                    berth[i] = new Seat(nextBerthPassenger, i, seat.type);
                    changeTicketDetails (nextBerthPassenger, berth[i]);
                }
                else
                    berth[i] = null;
                break;
            }
            i++;
        }
        if (berth[i] == null){

        }
        //printing the result
        System.out.println(seat.passenger.passenger_id + "-> " + seat.passenger.name + " Ticket has been cancelled.");
        if (nextBerthPassenger != null){
            System.out.println(nextBerthPassenger.passenger_id + " -> "
                    + nextBerthPassenger.name + " has moved to " + seat.berth + " " + (seat.seatNo + 1));
        }
        if (nextRACpassenger != null){
            System.out.println(nextRACpassenger.passenger_id + " -> "
                    + nextRACpassenger.name + " has moved to RAC ");
        }
    }

    private void changeTicketDetails (Passenger passenger, Seat seat){

        Ticket ticket = Ticket.ticketPassengerMap.getOrDefault(passenger, null);
        if (seat == null)
            ticket.seatType = "RAC";
        else
            ticket.seatType = ( seat.type == 1? "Lower : " : seat.type == 2? "Middle " : "Upper : ") + (seat.seatNo + 1);
    }


//----------------------------------------------------------------------------------------------------------------------
    //Getting Ticket Details
//----------------------------------------------------------------------------------------------------------------------
    public void getDetails (int passenger_id){
        Passenger passenger = Passenger.passengerIdMap.getOrDefault(passenger_id, null);

        if (passenger == null){
            System.out.println("Invalid Passenger ID..");
            return;
        }
        Ticket ticket = Ticket.ticketPassengerMap.getOrDefault(passenger, null);
        if (ticket == null){

            return;
        }
        System.out.println("---------------------------------------------");
        System.out.println(
                        "Passenger Id : " + passenger.passenger_id + "\n" +
                        "Name : " + passenger.name + "\n" +
                        "Seat Details : " + ticket.seatType
        );
        System.out.println("---------------------------------------------");

    }

    public void printBookedTickets (){
        for (Map.Entry<Passenger, Ticket> map : Ticket.ticketPassengerMap.entrySet()){
            Ticket ticket = map.getValue();
            Passenger passenger = map.getKey();

            System.out.println("---------------------------------------------");
            System.out.println(
                    "Passenger Id : " + passenger.passenger_id + "\n" +
                    "Name : " + passenger.name + "\n" +
                    "Seat Details : " + ticket.seatType
            );
            System.out.println("---------------------------------------------");

        }
    }
//----------------------------------------------------------------------------------------------------------------------
    //Ticket Availability
//----------------------------------------------------------------------------------------------------------------------

    public void getAvailableTickets () {
        int lower = 0;
        int middle = 0;
        int upper = 0;
        int rac = racLimit - racCount;
        int wl = wlLimit - wlCount;

        int i = 0;
        while (i < this.lowerBerth.length){
            if (lowerBerth[i] == null)
                lower ++;
            i++;
        }
        i = 0;
        while (i < this.middleBerth.length){
            if (middleBerth[i] == null)
                middle ++;
            i++;
        }
        i = 0;
        while (i < this.upperBerth.length){
            if (upperBerth[i] == null)
                upper ++;
            i++;
        }

        System.out.println(
//                "Lower Berth  : " + lower + "\n" +
//                "Middle Berth : " + middle + "\n" +
//                "Upper Berth  : " + upper + "\n" +
                "Berth        : " + (lower + upper + middle) + "\n" +
                "RAC          : " + rac + "\n" +
                "Waiting List : " + wl
        );
    }
}

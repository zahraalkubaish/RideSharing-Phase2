public class PrivateRide extends Ride implements IPrivateRide {
    private final IRider rider;
    public PrivateRide(int rideId, String pickupLocation, String dropoffLocation, ITimeSlot timeSlot, IRider rider, IDriver driver) {
        super(rideId, pickupLocation, dropoffLocation, timeSlot, driver);
        this.rider = rider;
    }
    public IRider getRider() { return rider; }
    public boolean hasRider(int riderId) { return rider.getId() == riderId; }
    public String toString() { return "Private Ride: " + super.toString() + ", Rider ID: " + rider.getId(); }
}

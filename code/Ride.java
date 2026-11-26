public abstract class Ride implements IRide {
    private final int rideId;
    private final String pickupLocation, dropoffLocation;
    private final ITimeSlot timeSlot;
    private final IDriver driver;
    public Ride(int rideId, String pickupLocation, String dropoffLocation, ITimeSlot timeSlot, IDriver driver) {
        this.rideId = rideId; this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation; this.timeSlot = timeSlot; this.driver = driver;
    }
    public int getRideId() { return rideId; }
    public String getPickupLocation() { return pickupLocation; }
    public String getDropoffLocation() { return dropoffLocation; }
    public ITimeSlot getTimeSlot() { return timeSlot; }
    public IDriver getDriver() { return driver; }
    public abstract boolean hasRider(int riderId);
    public int compareTo(IRide other) { return Integer.compare(rideId, other.getRideId()); }
    public String toString() {
        return "Ride ID: " + rideId + ", Pickup: " + pickupLocation + ", Dropoff: " + dropoffLocation
            + ", Start: " + timeSlot.getStart().format() + ", End: " + timeSlot.getEnd().format() + ", Driver ID: " + driver.getId();
    }
}

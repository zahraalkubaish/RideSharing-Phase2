public class SharedRide extends Ride implements ISharedRide {
    private final IBST<Integer,IRider> participants;
    public SharedRide(int rideId, String pickupLocation, String dropoffLocation, ITimeSlot timeSlot, IDriver driver) {
        super(rideId, pickupLocation, dropoffLocation, timeSlot, driver);
        participants = new BST<Integer,IRider>();
    }
    public LinkedList<IRider> getParticipants() { return participants.inOrder(); }
    public boolean addParticipant(IRider rider) { return participants.insert(rider.getId(), rider); }
    public boolean removeParticipantById(int riderId) { return participants.removeKey(riderId); }
    public boolean hasRider(int riderId) { return participants.findKey(riderId); }
    public boolean isEmpty() { return participants.empty(); }
    public String toString() { return "Shared Ride: " + super.toString() + ", Participants: " + participants.size(); }
}

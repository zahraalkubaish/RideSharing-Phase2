public class Driver extends Person implements IDriver {
    private final String vehiclePlate;
    private final VehicleType vehicleType;
    public Driver(int id, String name, String phoneNumber, String vehiclePlate, VehicleType vehicleType) {
        super(id, name, phoneNumber);
        if (vehiclePlate == null || !vehiclePlate.matches("[A-Z]{3}[0-9]{4}"))
            throw new IllegalArgumentException("Invalid vehicle plate");
        this.vehiclePlate = vehiclePlate; this.vehicleType = vehicleType;
    }
    public String getVehiclePlate() { return vehiclePlate; }
    public VehicleType getVehicleType() { return vehicleType; }
    public int compareTo(IDriver other) { return Integer.compare(getId(), other.getId()); }
    public String toString() { return super.toString() + ", Vehicle Plate: " + vehiclePlate + ", Vehicle Type: " + vehicleType; }
}

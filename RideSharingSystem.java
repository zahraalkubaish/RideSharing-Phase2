import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RideSharingSystem implements IRideSharingSystem {
    private final IBST<Integer, IRider> ridersById = new BST<>();
    private final IBST<String, IBST<Integer, IRider>> ridersByName = new BST<>();
    private final IBST<String, IBST<Integer, IRider>> ridersByHomeCity = new BST<>();
    private final IBST<Integer, IDriver> driversById = new BST<>();
    private final IBST<String, IDriver> driversByPlate = new BST<>();
    private final IBST<VehicleType, IBST<Integer, IDriver>> driversByVehicleType = new BST<>();
    private final IBST<Integer, IRide> ridesById = new BST<>();
    private final IBST<String, IBST<Integer, IRide>> ridesByPickupLocation = new BST<>();
    private final IBST<IDateTime, IBST<Integer, IRide>> ridesByPickupTime = new BST<>();
    private int nextRideId = 1;

    public RideSharingSystem() { }

    // Create an inner tree only when the outer key is new.
    private <K extends Comparable<K>, T> void addNested(IBST<K, IBST<Integer,T>> index, K key, int id, T value) {
        IBST<Integer,T> inner;
        if (index.findKey(key)) inner = index.retrieve();
        else {
            inner = new BST<Integer,T>();
            index.insert(key, inner);
        }
        inner.insert(id, value);
    }

    private <K extends Comparable<K>, T> void removeNested(IBST<K, IBST<Integer,T>> index, K key, int id) {
        if (index.findKey(key)) {
            IBST<Integer,T> inner = index.retrieve();
            inner.removeKey(id);
            if (inner.empty()) index.removeKey(key);
        }
    }

    private <K extends Comparable<K>, T> LinkedList<T> searchNested(IBST<K, IBST<Integer,T>> index, K key) {
        if (key != null && index.findKey(key)) return index.retrieve().inOrder();
        return new LinkedList<T>();
    }

    public boolean addRider(IRider rider) {
        if (rider == null || rider.getName() == null || rider.getHomeCity() == null || ridersById.findKey(rider.getId())) return false;
        ridersById.insert(rider.getId(), rider);
        addNested(ridersByName, rider.getName(), rider.getId(), rider);
        addNested(ridersByHomeCity, rider.getHomeCity(), rider.getId(), rider);
        return true;
    }
    public IRider searchRiderById(int id) {
        if (ridersById.findKey(id)) return ridersById.retrieve();
        return null;
    }
    public LinkedList<IRider> searchRidersByName(String name) { return searchNested(ridersByName, name); }
    public LinkedList<IRider> searchRidersByHomeCity(String city) { return searchNested(ridersByHomeCity, city); }
    public LinkedList<IRider> getAllRiders() { return ridersById.inOrder(); }

    public boolean addDriver(IDriver driver) {
        if (driver == null || driver.getVehiclePlate() == null || driver.getVehicleType() == null || driversById.findKey(driver.getId()) || driversByPlate.findKey(driver.getVehiclePlate())) return false;
        driversById.insert(driver.getId(), driver);
        driversByPlate.insert(driver.getVehiclePlate(), driver);
        addNested(driversByVehicleType, driver.getVehicleType(), driver.getId(), driver);
        return true;
    }
    public IDriver searchDriverById(int id) {
        if (driversById.findKey(id)) return driversById.retrieve();
        return null;
    }
    public LinkedList<IDriver> searchDriversByVehicleType(VehicleType type) { return searchNested(driversByVehicleType, type); }
    public LinkedList<IDriver> getAllDrivers() { return driversById.inOrder(); }

    public boolean removeRider(int id) {
        IRider rider = searchRiderById(id);
        if (rider == null) return false;
        // A traversal result is a snapshot; cancellation changes the actual schedule.
        LinkedList<IRide> rides = rider.getSchedule().inOrder();
        if (!rides.empty()) {
            rides.findFirst();
            while (true) {
                IRide ride = rides.retrieve();
                if (ride instanceof ISharedRide) {
                    ISharedRide shared = (ISharedRide) ride;
                    shared.removeParticipantById(id);
                    rider.getSchedule().removeKey(ride.getTimeSlot());
                    if (shared.isEmpty()) cancelRide(ride.getRideId());
                } else cancelRide(ride.getRideId());
                if (rides.last()) break;
                rides.findNext();
            }
        }
        ridersById.removeKey(id);
        removeNested(ridersByName, rider.getName(), id);
        removeNested(ridersByHomeCity, rider.getHomeCity(), id);
        return true;
    }
    public boolean removeDriver(int id) {
        IDriver driver = searchDriverById(id);
        if (driver == null) return false;
        LinkedList<IRide> rides = driver.getSchedule().inOrder();
        if (!rides.empty()) {
            rides.findFirst();
            while (true) {
                cancelRide(rides.retrieve().getRideId());
                if (rides.last()) break;
                rides.findNext();
            }
        }
        driversById.removeKey(id);
        driversByPlate.removeKey(driver.getVehiclePlate());
        removeNested(driversByVehicleType, driver.getVehicleType(), id);
        return true;
    }
    public int nbKeyCompRiderById(int id) { return ridersById.nbKeyComp(id); }
    private IBST<?,?> getIndex(IndexName index) {
        switch (index) {
            case RIDERS_BY_ID: return ridersById;
            case RIDERS_BY_NAME: return ridersByName;
            case RIDERS_BY_HOME_CITY: return ridersByHomeCity;
            case DRIVERS_BY_ID: return driversById;
            case DRIVERS_BY_PLATE: return driversByPlate;
            case DRIVERS_BY_VEHICLE_TYPE: return driversByVehicleType;
            case RIDES_BY_ID: return ridesById;
            case RIDES_BY_PICKUP_LOCATION: return ridesByPickupLocation;
            case RIDES_BY_PICKUP_TIME: return ridesByPickupTime;
            default: throw new IllegalArgumentException("Unknown index");
        }
    }
    public int indexSize(IndexName index) { return getIndex(index).size(); }
    public int indexHeight(IndexName index) { return getIndex(index).height(); }

    public int loadRidersFromCSV(String path) {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length != 5) continue;
                try {
                    if (addRider(new Rider(Integer.parseInt(p[0].trim()), p[1].trim(), p[2].trim(), p[3].trim(), p[4].trim()))) count++;
                } catch (IllegalArgumentException e) { /* Skip invalid record. */ }
            }
        } catch (IOException e) { return -1; }
        return count;
    }
    public int loadDriversFromCSV(String path) {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length != 5) continue;
                try {
                    if (addDriver(new Driver(Integer.parseInt(p[0].trim()), p[1].trim(), p[2].trim(), p[3].trim(), VehicleType.valueOf(p[4].trim())))) count++;
                } catch (IllegalArgumentException e) { /* Skip invalid record. */ }
            }
        } catch (IOException e) { return -1; }
        return count;
    }

    // Student C: replace these placeholders with ride operations.
    public int loadRidesFromCSV(String ridesFilePath) { throw new UnsupportedOperationException("Student C pending"); }
    public int schedulePrivateRide(String pickupLocation, String dropoffLocation, ITimeSlot timeSlot, int riderId, int driverId) { throw new UnsupportedOperationException("Student C pending"); }
    public int scheduleSharedRide(String pickupLocation, String dropoffLocation, ITimeSlot timeSlot, int[] riderIds, int driverId) { throw new UnsupportedOperationException("Student C pending"); }
    public boolean cancelRide(int rideId) { throw new UnsupportedOperationException("Student C pending"); }
    public LinkedList<IRide> searchRidesByPickupLocation(String pickupLocation) { throw new UnsupportedOperationException("Student C pending"); }
    public LinkedList<IRide> searchRidesByRiderName(String riderName) { throw new UnsupportedOperationException("Student C pending"); }
    public LinkedList<IRider> getSharedRideParticipants(int rideId) { throw new UnsupportedOperationException("Student C pending"); }
    public LinkedList<IRide> getAllRidesAlphabetically() { throw new UnsupportedOperationException("Student C pending"); }
    public LinkedList<IRide> searchRidesByPickupTimeRange(IDateTime from, IDateTime to) { throw new UnsupportedOperationException("Student C pending"); }
    public LinkedList<IDriver> findAvailableDrivers(ITimeSlot timeSlot, VehicleType vehicleType) { throw new UnsupportedOperationException("Student C pending"); }
}

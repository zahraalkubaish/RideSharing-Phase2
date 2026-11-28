// Student B: tests for rider/driver indexes and CSV loading.
// Compile: javac -d out code/*.java RideSharingSystem.java student-b-tests/TestB.java
// Run from repository root: java -cp out TestB
public class TestB {
    public static void main(String[] args) {
        RideSharingSystem system = new RideSharingSystem();
        if (system.loadRidersFromCSV("riders_100.csv") != 100)
            throw new AssertionError("Riders CSV load failed");
        if (system.loadDriversFromCSV("drivers_30.csv") != 30)
            throw new AssertionError("Drivers CSV load failed");

        for (IndexName index : IndexName.values())
            System.out.println(index + " size=" + system.indexSize(index)
                    + " height=" + system.indexHeight(index));

        for (int id : new int[] {1, 50, 100, 101})
            System.out.println(id + " key comparisons=" + system.nbKeyCompRiderById(id));

        IRider a = new Rider(101, "Test", "a", "0500000000", "TestCity");
        IRider b = new Rider(102, "Test", "b", "0500000001", "TestCity");
        if (!system.addRider(b) || !system.addRider(a) || system.addRider(a))
            throw new AssertionError("Rider insertion/duplicate test failed");

        LinkedList<IRider> matches = system.searchRidersByName("Test");
        matches.findFirst();
        if (matches.size() != 2 || matches.retrieve() != a)
            throw new AssertionError("Nested name index ordering failed");

        if (!system.removeRider(101) || !system.removeRider(102)
                || !system.searchRidersByHomeCity("TestCity").empty())
            throw new AssertionError("Rider cascade/empty nested index test failed");

        IDriver first = system.searchDriverById(1);
        if (system.addDriver(new Driver(99, "Test", "0500000000",
                first.getVehiclePlate(), VehicleType.SUV)))
            throw new AssertionError("Duplicate vehicle plate accepted");

        if (!system.removeDriver(1) || system.searchDriverById(1) != null)
            throw new AssertionError("Driver removal failed");

        System.out.println("Basic Student B tests passed");
    }
}

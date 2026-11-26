/** Common immutable identity and per-person schedule. */
public abstract class Person implements IPerson {
    private final int id;
    private final String name;
    private String phoneNumber;
    private final IBST<ITimeSlot,IRide> schedule;
    public Person(int id, String name, String phoneNumber) {
        this.id = id; this.name = name;
        setPhoneNumber(phoneNumber);
        schedule = new BST<ITimeSlot,IRide>();
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public final void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || !phoneNumber.matches("[0-9]{10}"))
            throw new IllegalArgumentException("Invalid phone number");
        this.phoneNumber = phoneNumber;
    }
    public IBST<ITimeSlot,IRide> getSchedule() { return schedule; }
    public String toString() { return "ID: " + id + ", Name: " + name + ", Phone: " + phoneNumber; }
}

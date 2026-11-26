public class Rider extends Person implements IRider {
    private final String email, homeCity;
    public Rider(int id, String name, String email, String phoneNumber, String homeCity) {
        super(id, name, phoneNumber);
        this.email = email; this.homeCity = homeCity;
    }
    public String getEmail() { return email; }
    public String getHomeCity() { return homeCity; }
    public int compareTo(IRider other) { return Integer.compare(getId(), other.getId()); }
    public String toString() { return super.toString() + ", Email: " + email + ", Home City: " + homeCity; }
}

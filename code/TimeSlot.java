/** Half-open interval: touching intervals do not conflict. */
public class TimeSlot implements ITimeSlot {
    private final IDateTime start, end;
    public TimeSlot(IDateTime start, IDateTime end) {
        if (start == null || end == null || start.compareTo(end) >= 0)
            throw new IllegalArgumentException("Invalid time slot");
        this.start = start; this.end = end;
    }
    public IDateTime getStart() { return start; }
    public IDateTime getEnd() { return end; }
    public int compareTo(ITimeSlot other) {
        if (end.compareTo(other.getStart()) <= 0) return -1;
        else if (start.compareTo(other.getEnd()) >= 0) return 1;
        else return 0;
    }
}

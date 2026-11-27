/** INHERITANCE: a Seatwork is an Assessment. It only says its type. */
public class Seatwork extends Assessment {
    public Seatwork(String name, double score, double total) throws GradeException { super(name, score, total); }
    @Override public String getType() { return "Seatwork"; }
}

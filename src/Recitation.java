/** INHERITANCE: a Recitation is an Assessment. It only says its type. */
public class Recitation extends Assessment {
    public Recitation(String name, double score, double total) throws GradeException { super(name, score, total); }
    @Override public String getType() { return "Recitation"; }
}

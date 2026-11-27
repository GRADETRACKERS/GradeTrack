/** INHERITANCE: a Assignment is an Assessment. It only says its type. */
public class Assignment extends Assessment {
    public Assignment(String name, double score, double total) throws GradeException { super(name, score, total); }
    @Override public String getType() { return "Assignment"; }
}

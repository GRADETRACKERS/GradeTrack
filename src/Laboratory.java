/** INHERITANCE: a Laboratory is an Assessment. It only says its type. */
public class Laboratory extends Assessment {
    public Laboratory(String name, double score, double total) throws GradeException { super(name, score, total); }
    @Override public String getType() { return "Laboratory"; }
}

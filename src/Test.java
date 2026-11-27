/** INHERITANCE: a Test is an Assessment. It only says its type. */
public class Test extends Assessment {
    public Test(String name, double score, double total) throws GradeException { super(name, score, total); }
    @Override public String getType() { return "Test"; }
}

/** INHERITANCE: a Examination is an Assessment. It only says its type. */
public class Examination extends Assessment {
    public Examination(String name, double score, double total) throws GradeException { super(name, score, total); }
    @Override public String getType() { return "Examination"; }
}

/** INHERITANCE: a Performance Task is an Assessment. It only says its type. */
public class PerformanceTask extends Assessment {
    public PerformanceTask(String name, double score, double total) throws GradeException { super(name, score, total); }
    @Override public String getType() { return "Performance Task"; }
}

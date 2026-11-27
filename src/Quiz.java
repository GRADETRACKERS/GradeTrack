/** INHERITANCE: a Quiz is an Assessment. It only says its type. */
public class Quiz extends Assessment {
    public Quiz(String name, double score, double total) throws GradeException { super(name, score, total); }
    @Override public String getType() { return "Quiz"; }
}

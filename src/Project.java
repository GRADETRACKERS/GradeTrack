/** INHERITANCE: a Project is an Assessment. It only says its type. */
public class Project extends Assessment {
    public Project(String name, double score, double total) throws GradeException { super(name, score, total); }
    @Override public String getType() { return "Project"; }
}

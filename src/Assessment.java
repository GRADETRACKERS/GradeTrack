/**
 * ABSTRACTION: "one graded item". You cannot write new Assessment(...), only a child class.
 * ENCAPSULATION: private fields, public getters and a setter that checks the value.
 */
public abstract class Assessment {

    public static final double NOT_GRADED = -1;   // -1 means "no score yet"

    private String name;
    private double score;
    private double total;   // the "Out of" value

    public Assessment(String name, double score, double total) throws GradeException {
        if (name == null || name.trim().isEmpty()) throw new GradeException("Score name cannot be empty.");
        if (total <= 0) throw new GradeException("'Out of' must be greater than 0.");
        this.name = name.trim();
        this.total = total;
        setScore(score);
    }

    public abstract String getType();   // every child class says its own type

    public void setScore(double score) throws GradeException {
        if (score != NOT_GRADED && (score < 0 || score > total))
            throw new GradeException("Score must be from 0 to " + Course.show(total) + ".");
        this.score = score;
    }

    public String getName() { return name; }
    public double getScore() { return score; }
    public double getTotal() { return total; }
    public boolean isGraded() { return score != NOT_GRADED; }

    /** Score in percent, or -1 if not graded yet. */
    public double getPercent() { return isGraded() ? score / total * 100 : -1; }
}

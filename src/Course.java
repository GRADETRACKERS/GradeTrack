import java.util.ArrayList;
import java.util.HashMap;

/**
 * One course: name, credits, the weight of each score type (total 100%) and its scores.
 * It also calculates its own grade:
 *   average of a type = points earned / points graded   (only graded work counts)
 *   current grade     = weighted average of the types that have graded work
 */
public class Course {

    public static final String[] TYPES = {"Quiz", "Test", "Examination", "Laboratory",
            "Seatwork", "Assignment", "Recitation", "Project", "Performance Task"};
    public static final String[] SORT_OPTIONS = {
            "Order added", "Name (A-Z)", "Type", "Score (high to low)", "Score (low to high)"};

    private String name;
    private int credits;
    private HashMap<String, Double> weights = new HashMap<>();   // type -> percent
    private ArrayList<Assessment> scores = new ArrayList<>();

    /** weights[i] is the percent of TYPES[i]. Missing weights count as 0. */
    public Course(String name, int credits, double[] weights) throws GradeException {
        if (name == null || name.trim().isEmpty()) throw new GradeException("Course name cannot be empty.");
        if (credits < 1 || credits > 12) throw new GradeException("Credit hours must be between 1 and 12.");
        double total = 0;
        for (int i = 0; i < TYPES.length; i++) {
            double w = i < weights.length ? weights[i] : 0;
            if (w < 0) throw new GradeException("A weight cannot be negative.");
            total += w;
            this.weights.put(TYPES[i], w);
        }
        if (Math.abs(total - 100) > 0.001) throw new GradeException("Weights must add up to 100% (now " + show(total) + "%).");
        this.name = name.trim();
        this.credits = credits;
    }

    public String getName() { return name; }
    public int getCredits() { return credits; }
    public double getWeight(String type) { return weights.get(type); }
    public ArrayList<Assessment> getScores() { return scores; }

    @Override
    public String toString() { return name; }   // so a combo box shows the course name

    // ---------------- scores ----------------

    /** Creates the right kind of Assessment for the type (POLYMORPHISM) and adds it. */
    public void addScore(String type, String name, double score, double total) throws GradeException {
        switch (type) {
            case "Quiz":             scores.add(new Quiz(name, score, total)); break;
            case "Test":             scores.add(new Test(name, score, total)); break;
            case "Examination":      scores.add(new Examination(name, score, total)); break;
            case "Laboratory":       scores.add(new Laboratory(name, score, total)); break;
            case "Seatwork":         scores.add(new Seatwork(name, score, total)); break;
            case "Assignment":       scores.add(new Assignment(name, score, total)); break;
            case "Recitation":       scores.add(new Recitation(name, score, total)); break;
            case "Project":          scores.add(new Project(name, score, total)); break;
            case "Performance Task": scores.add(new PerformanceTask(name, score, total)); break;
            default: throw new GradeException("Unknown score type: " + type);
        }
    }

    /** METHOD OVERLOADING: same name, no score = "not graded yet". */
    public void addScore(String type, String name, double total) throws GradeException {
        addScore(type, name, Assessment.NOT_GRADED, total);
    }

    public void removeScore(Assessment a) { scores.remove(a); }

    /** SEARCH: scores whose name or type contains the text (empty text = all scores). */
    public ArrayList<Assessment> search(String text) {
        String q = text.trim().toLowerCase();
        ArrayList<Assessment> found = new ArrayList<>();
        for (Assessment a : scores) {
            if (a.getName().toLowerCase().contains(q) || a.getType().toLowerCase().contains(q)) found.add(a);
        }
        return found;
    }

    /** What the table shows: search first, then SORT (bubble sort). sortIndex is a position in SORT_OPTIONS. */
    public ArrayList<Assessment> view(String searchText, int sortIndex) {
        ArrayList<Assessment> list = search(searchText);
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.size() - 1 - i; j++) {
                if (outOfOrder(list.get(j), list.get(j + 1), sortIndex)) {   // swap neighbours that are in the wrong order
                    Assessment temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
        return list;
    }

    /** True if a should come after b for this sort option. "Not graded" always goes last. */
    private static boolean outOfOrder(Assessment a, Assessment b, int sortIndex) {
        switch (sortIndex) {
            case 1:  return a.getName().compareToIgnoreCase(b.getName()) > 0;
            case 2:  return a.getType().compareToIgnoreCase(b.getType()) > 0;
            case 3:  return b.getPercent() > a.getPercent();   // not graded is -1, so it ends last
            case 4:  return lowFirst(a) > lowFirst(b);
            default: return false;   // 0 = order added
        }
    }

    private static double lowFirst(Assessment a) { return a.isGraded() ? a.getPercent() : 1000; }

    // ---------------- grades ----------------

    /** Returns {all points, graded points, earned points} of one type. */
    private double[] points(String type) {
        double all = 0, graded = 0, earned = 0;
        for (Assessment a : scores) {
            if (a.getType().equals(type)) {
                all += a.getTotal();
                if (a.isGraded()) {
                    graded += a.getTotal();
                    earned += a.getScore();
                }
            }
        }
        return new double[]{all, graded, earned};
    }

    /** Returns {percent of the course already graded, course points already earned} (both 0 to 100). */
    private double[] progress() {
        double graded = 0, earned = 0;
        for (String type : TYPES) {
            double[] p = points(type);
            if (p[0] > 0) {
                graded += getWeight(type) * p[1] / p[0];
                earned += getWeight(type) * p[2] / p[0];
            }
        }
        return new double[]{graded, earned};
    }

    /** Percent of one type from its graded work, or -1 if nothing is graded in it. */
    public double typeAverage(String type) {
        double[] p = points(type);
        return p[1] == 0 ? -1 : p[2] / p[1] * 100;
    }

    /** Current grade in percent, or -1 if nothing is graded yet. */
    public double currentGrade() {
        double sum = 0, weightUsed = 0;
        for (String type : TYPES) {
            if (typeAverage(type) >= 0) {
                sum += getWeight(type) * typeAverage(type);
                weightUsed += getWeight(type);
            }
        }
        return weightUsed == 0 ? -1 : sum / weightUsed;
    }

    /** How much of the whole course is already graded (0 to 100). */
    public double percentGraded() { return progress()[0]; }

    /** The sentence on the Target tab: the average still needed to reach the target. */
    public String adviceFor(double target) throws GradeException {
        if (target < 0 || target > 100) throw new GradeException("Target grade must be between 0 and 100.");
        double[] p = progress();
        double remaining = 100 - p[0];
        if (remaining < 0.0001) return "Everything is graded. Your final grade is " + percent(p[1]) + ".";
        double needed = (target - p[1]) / remaining * 100;
        String left = percent(remaining);
        if (needed <= 0) {
            return "You already secured " + show(target) + "%. Even 0% on the remaining " + left
                    + " keeps you at or above your target.";
        }
        if (needed > 100) {
            return "A target of " + show(target) + "% is no longer reachable. You would need "
                    + percent(needed) + " on the remaining " + left + ", which is above 100%.";
        }
        return "You need an average of " + percent(needed) + " on the remaining " + left
                + " of the course to reach " + show(target) + "%.";
    }

    // ---------------- text for the screen ----------------

    /** 77.7 -> "77.7%". A negative value (nothing graded) -> "-". */
    public static String percent(double value) { return value < 0 ? "-" : String.format("%.1f%%", value); }

    /** Shows 85.0 as "85" and 85.756 as "85.76". */
    public static String show(double value) {
        return value == Math.rint(value) ? String.valueOf((long) value) : String.format("%.2f", value);
    }
}

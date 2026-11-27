import java.util.ArrayList;

/** GPA calculation. 1.00 is the best grade and 5.00 is failed. All methods are static. */
public class Gpa {

    // a percent of CUTOFFS[i] or more gives POINTS[i]. Edit these two arrays for another school scale.
    private static final double[] CUTOFFS = {97, 94, 91, 88, 85, 82, 79, 76, 75};
    private static final double[] POINTS = {1.00, 1.25, 1.50, 1.75, 2.00, 2.25, 2.50, 2.75, 3.00};

    public static double gradePoint(double percent) {
        for (int i = 0; i < CUTOFFS.length; i++) {
            if (percent >= CUTOFFS[i]) return POINTS[i];
        }
        return 5.00;
    }

    /** GPA weighted by credit hours. Courses with no graded work are skipped. -1 if there are none. */
    public static double compute(ArrayList<Course> courses) {
        double points = 0;
        int credits = 0;
        for (Course c : courses) {
            if (c.currentGrade() >= 0) {
                points += gradePoint(c.currentGrade()) * c.getCredits();
                credits += c.getCredits();
            }
        }
        if (credits == 0) return -1;
        return Math.round((points / credits + 0.000001) * 100) / 100.0;   // +0.000001 so 2.225 rounds to 2.23
    }
}

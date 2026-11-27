import java.io.File;
import java.util.ArrayList;

/** Plain-Java checks (no libraries). Run:  java -cp out:testout Tests */
public class Tests {

    static int passed = 0, failed = 0;

    static void check(String label, boolean ok) {
        if (ok) passed++; else failed++;
        System.out.println((ok ? "PASS  " : "FAIL  ") + label);
    }

    interface Action { void run() throws Exception; }

    /** True if the action throws a GradeException. */
    static boolean rejected(Action a) {
        try { a.run(); } catch (GradeException e) { return true; } catch (Exception e) { return false; }
        return false;
    }

    public static void main(String[] args) throws Exception {
        // ---- example: Physics 77.7 (4 cr), Calculus 85.8 (3), Intro 88.0 (3) -> GPA 2.23 ----
        Course physics = new Course("Physics 101", 4, new double[]{20, 20, 40, 20});
        physics.addScore("Quiz", "Quiz 1", 78, 100);
        physics.addScore("Test", "Test 1", 76.5, 100);
        physics.addScore("Examination", "Midterm", 78, 100);
        physics.addScore("Laboratory", "Lab 1", 78, 100);
        Course calculus = new Course("Calculus II", 3, new double[]{20, 30, 30, 20});
        calculus.addScore("Quiz", "Quiz 1", 88, 100);
        calculus.addScore("Test", "Test 1", 84.3, 100);
        Course intro = new Course("Intro", 3, new double[]{20, 20, 40, 20});
        for (String t : Course.TYPES) intro.addScore(t, t + " 1", 88, 100);
        Course biology = new Course("Biology", 3, new double[]{25, 25, 25, 25});

        ArrayList<Course> all = new ArrayList<>();
        all.add(physics); all.add(calculus); all.add(intro); all.add(biology);

        check("Physics 77.7%", Course.percent(physics.currentGrade()).equals("77.7%"));
        check("Calculus 85.8%", Course.percent(calculus.currentGrade()).equals("85.8%"));
        check("Calculus is 50% graded", Math.abs(calculus.percentGraded() - 50) < 1e-9);
        check("Intro 88.0%", Course.percent(intro.currentGrade()).equals("88.0%"));
        check("Biology (nothing graded) shows -", Course.percent(biology.currentGrade()).equals("-"));
        check("GPA = 2.23", Gpa.compute(all) == 2.23);
        check("Grade points 88->1.75, 75->3.00, 74.9->5.00",
                Gpa.gradePoint(88) == 1.75 && Gpa.gradePoint(75) == 3.0 && Gpa.gradePoint(74.9) == 5.0);
        check("GPA with no graded course is -1", Gpa.compute(new ArrayList<>()) == -1);

        // ---- not graded work does not change the grade ----
        calculus.addScore("Examination", "Midterm", 100);   // overloaded method: no score
        check("Ungraded exam keeps Calculus at 85.8%", Course.percent(calculus.currentGrade()).equals("85.8%"));
        check("Ungraded exam does not count as graded", Math.abs(calculus.percentGraded() - 50) < 1e-9);

        // ---- target grade ----
        check("Fully graded message", physics.adviceFor(85).equals("Everything is graded. Your final grade is 77.7%."));
        check("Calculus target 85 needs 84.2%", calculus.adviceFor(85).contains("84.2%"));
        check("Target already secured", calculus.adviceFor(40).contains("already secured"));
        check("Target unreachable", calculus.adviceFor(99.9).contains("no longer reachable"));
        check("Target 101 rejected", rejected(() -> calculus.adviceFor(101)));

        // ---- input errors ----
        check("Empty course name rejected", rejected(() -> new Course(" ", 3, new double[]{25, 25, 25, 25})));
        check("Credits 0 rejected", rejected(() -> new Course("X", 0, new double[]{25, 25, 25, 25})));
        check("Weights not 100 rejected", rejected(() -> new Course("X", 3, new double[]{20, 20, 20, 20})));
        check("Negative weight rejected", rejected(() -> new Course("X", 3, new double[]{-10, 60, 30, 20})));
        check("Score above 'Out of' rejected", rejected(() -> physics.addScore("Quiz", "Q", 101, 100)));
        check("'Out of' 0 rejected", rejected(() -> physics.addScore("Quiz", "Q", 5, 0)));
        check("Empty score name rejected", rejected(() -> physics.addScore("Quiz", " ", 5, 10)));
        check("Unknown type rejected", rejected(() -> physics.addScore("Attendance", "P", 5, 10)));
        check("Letters rejected", rejected(() -> Input.number("abc", "Score")));
        check("NaN rejected", rejected(() -> Input.number("NaN", "Score")));
        check("Negative score rejected", rejected(() -> Input.score("-5")));
        check("Decimal credits rejected", rejected(() -> Input.wholeNumber("3.5", "Credits")));
        check("Empty score means not graded", Input.score("  ") == Assessment.NOT_GRADED);

        // ---- more Philippine score types ----
        Course shs = new Course("Filipino", 3, new double[]{20, 0, 30, 0, 10, 10, 10, 10, 10});
        for (String t : Course.TYPES) shs.addScore(t, t + " 1", 90, 100);
        check("Nine types, each says its own type", shs.getScores().size() == 9 && shs.getScores().get(8).getType().equals("Performance Task"));
        check("Weights add up with new types", Course.percent(shs.currentGrade()).equals("90.0%"));
        check("Old 4-weight course still valid", Course.percent(physics.currentGrade()).equals("77.7%") && physics.getWeight("Project") == 0);

        // ---- polymorphism ----
        check("Each child says its own type", physics.getScores().get(0).getType().equals("Quiz")
                && physics.getScores().get(1).getType().equals("Test")
                && physics.getScores().get(2).getType().equals("Examination")
                && physics.getScores().get(3).getType().equals("Laboratory"));

        // ---- search and sort ----
        Course c = new Course("Sort me", 3, new double[]{25, 25, 25, 25});
        c.addScore("Test", "Zed", 50, 100);
        c.addScore("Quiz", "Alpha", 90, 100);
        c.addScore("Laboratory", "Mid", 70, 100);
        c.addScore("Quiz", "Pending", 100);
        check("Search by name", c.search("alp").size() == 1);
        check("Search by type", c.search("quiz").size() == 2);
        check("Empty search = all", c.search("").size() == 4);
        check("Sort A-Z", c.view("", 1).get(0).getName().equals("Alpha"));
        check("Sort by type", c.view("", 2).get(0).getType().equals("Laboratory"));
        check("Sort high to low, ungraded last",
                c.view("", 3).get(0).getName().equals("Alpha") && c.view("", 3).get(3).getName().equals("Pending"));
        check("Sort low to high, ungraded last",
                c.view("", 4).get(0).getName().equals("Zed") && c.view("", 4).get(3).getName().equals("Pending"));
        c.removeScore(c.getScores().get(0));
        check("Remove a score", c.getScores().size() == 3);

        // ---- update a score ----
        Assessment pending = c.search("pending").get(0);
        pending.setScore(80);
        check("Update a score", pending.isGraded() && pending.getPercent() == 80);
        check("Update above 'Out of' rejected", rejected(() -> pending.setScore(500)));

        // ---- file saving and loading ----
        File file = File.createTempFile("gradetrack", ".txt");
        DataFile.save(all, file);
        ArrayList<Course> loaded = DataFile.load(file);
        check("Loaded the same number of courses", loaded.size() == 4);
        check("Loaded grades are the same", Course.percent(loaded.get(0).currentGrade()).equals("77.7%")
                && Course.percent(loaded.get(1).currentGrade()).equals("85.8%"));
        check("Loaded GPA is the same", Gpa.compute(loaded) == 2.23);
        check("Ungraded score stays ungraded after loading", !loaded.get(1).search("midterm").get(0).isGraded());
        check("Missing file gives an empty list", DataFile.load(new File("does-not-exist.txt")).isEmpty());
        file.delete();

        System.out.println("\n" + passed + " passed, " + failed + " failed");
        System.exit(failed == 0 ? 0 : 1);
    }
}

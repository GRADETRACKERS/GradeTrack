import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * FILE HANDLING: saves and loads everything in a plain text file, one line per record (TAB between fields):
 *   COURSE  name  credits  one weight per type in Course.TYPES
 *   SCORE   type  name     score  total       (belongs to the COURSE line above it)
 */
public class DataFile {

    public static void save(ArrayList<Course> courses, File file) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        for (Course c : courses) {
            String line = "COURSE\t" + clean(c.getName()) + "\t" + c.getCredits();
            for (String type : Course.TYPES) line += "\t" + c.getWeight(type);
            lines.add(line);
            for (Assessment a : c.getScores()) {
                lines.add("SCORE\t" + a.getType() + "\t" + clean(a.getName()) + "\t" + a.getScore() + "\t" + a.getTotal());
            }
        }
        if (file.getParentFile() != null) file.getParentFile().mkdirs();   // create the folder the first time
        Files.write(file.toPath(), lines);
    }

    public static ArrayList<Course> load(File file) throws IOException {
        ArrayList<Course> courses = new ArrayList<>();
        if (!file.exists()) return courses;   // first time the program runs
        Course current = null;
        for (String line : Files.readAllLines(file.toPath())) {
            String[] f = line.split("\t");
            try {
                if (f[0].equals("COURSE")) {
                    current = null;
                    double[] weights = new double[Course.TYPES.length];
                    for (int i = 0; i < weights.length; i++) {
                        weights[i] = 3 + i < f.length ? Double.parseDouble(f[3 + i]) : 0;   // old files have 4 weights
                    }
                    current = new Course(f[1], Integer.parseInt(f[2]), weights);
                    courses.add(current);
                } else if (f[0].equals("SCORE") && current != null) {
                    current.addScore(f[1], f[2], Double.parseDouble(f[3]), Double.parseDouble(f[4]));
                }
            } catch (GradeException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
                // a damaged line is skipped, the rest of the file still loads
            }
        }
        return courses;
    }

    /** A TAB inside a name would break the file format. */
    private static String clean(String text) { return text.replace('\t', ' '); }
}

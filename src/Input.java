/** Turns the text the user typed into numbers. A bad value throws a GradeException. */
public class Input {

    public static double number(String text, String label) throws GradeException {
        try {
            double value = Double.parseDouble(text.trim());
            if (Double.isNaN(value) || Double.isInfinite(value)) throw new NumberFormatException();
            return value;
        } catch (NumberFormatException e) {
            throw new GradeException(label + " must be a number.");
        }
    }

    public static int wholeNumber(String text, String label) throws GradeException {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new GradeException(label + " must be a whole number (for example 3).");
        }
    }

    /** A score. Empty text means "not graded yet". */
    public static double score(String text) throws GradeException {
        if (text.trim().isEmpty()) return Assessment.NOT_GRADED;
        double value = number(text, "Score");
        if (value < 0) throw new GradeException("Score cannot be negative.");
        return value;
    }
}

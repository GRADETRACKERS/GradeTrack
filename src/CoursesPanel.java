import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Courses tab. Pick a course, see its grade, search and sort its scores,
 * add / update / delete scores, and add or delete courses.
 * Every button and box calls actionPerformed(), which checks which one was used.
 */
public class CoursesPanel extends JPanel implements ActionListener {

    private Main app;
    private ArrayList<Assessment> shown = new ArrayList<>();   // the scores in the table, in the same order

    private JComboBox<Course> courseBox = new JComboBox<>();
    private JLabel infoLabel = new JLabel(" ");
    private JLabel typeLabel = new JLabel(" ");
    private JTable scoreTable = Main.newTable(new String[]{"Name", "Type", "Score", "Out of", "Percent"});
    private JTextField searchField = new JTextField(12);
    private JComboBox<String> sortBox = new JComboBox<>(Course.SORT_OPTIONS);
    private JTextField nameField = new JTextField(10);
    private JComboBox<String> typeBox = new JComboBox<>(Course.TYPES);
    private JTextField scoreField = new JTextField(5);
    private JTextField totalField = new JTextField(5);

    private JButton newCourseBtn = Main.button("+ New course", this);
    private JButton deleteCourseBtn = Main.button("Delete course", this);
    private JButton addScoreBtn = Main.button("Add score", this);
    private JButton updateScoreBtn = Main.button("Update selected score", this);
    private JButton deleteScoreBtn = Main.button("Delete selected score", this);

    public CoursesPanel(Main app) {
        this.app = app;

        JPanel top = new JPanel(new GridLayout(3, 1));   // course chooser + the grade information
        top.add(Main.row(new JLabel("Course:"), courseBox, newCourseBtn, deleteCourseBtn));
        top.add(infoLabel);
        top.add(typeLabel);

        JPanel middle = new JPanel(new BorderLayout());   // search, sort and the scores table
        middle.add(Main.row(new JLabel("Search (name or type):"), searchField, Main.button("Search", this),
                new JLabel("Sort by:"), sortBox), BorderLayout.NORTH);
        middle.add(new JScrollPane(scoreTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(2, 1));   // add a score, update / delete buttons
        bottom.add(Main.row(new JLabel("Add a score:  Name"), nameField, new JLabel("Type"), typeBox,
                new JLabel("Score"), scoreField, new JLabel("Out of"), totalField, addScoreBtn));
        bottom.add(Main.row(updateScoreBtn, deleteScoreBtn, new JLabel("(leave Score empty if it is not graded yet)")));

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(middle, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        courseBox.addActionListener(this);
        sortBox.addActionListener(this);
        searchField.addActionListener(this);   // pressing Enter also searches
    }

    /** Runs when any button or box is used. */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == newCourseBtn) newCourse();
        else if (source == deleteCourseBtn) deleteCourse();
        else if (source == addScoreBtn) addScore();
        else if (source == updateScoreBtn) updateScore();
        else if (source == deleteScoreBtn) deleteScore();
        else if (source == courseBox) showCourse();
        else showScores();   // the Search button, Enter in the search box, or the sort box
    }

    // ---------------- showing data ----------------

    private Course selectedCourse() { return (Course) courseBox.getSelectedItem(); }

    public void refresh() {
        app.fill(courseBox);
        showCourse();
    }

    private void showCourse() {
        Course c = selectedCourse();
        if (c == null) {
            infoLabel.setText(" No course yet. Press \"+ New course\".");
            typeLabel.setText(" ");
        } else {
            infoLabel.setText(" Credits: " + c.getCredits() + "     Current grade: " + Course.percent(c.currentGrade())
                    + "     Graded: " + Course.percent(c.percentGraded()));
            Object picked = typeBox.getSelectedItem();   // the "Type" box only offers types that have a weight
            typeBox.removeAllItems();
            String text = " ";
            for (String type : Course.TYPES) {
                if (c.getWeight(type) > 0) {
                    typeBox.addItem(type);
                    text += type + " (" + Course.show(c.getWeight(type)) + "%): " + Course.percent(c.typeAverage(type)) + "     ";
                }
            }
            typeBox.setSelectedItem(picked);
            typeLabel.setText(text);
        }
        showScores();
    }

    private void showScores() {
        Course c = selectedCourse();
        shown = c == null ? new ArrayList<>() : c.view(searchField.getText(), sortBox.getSelectedIndex());
        DefaultTableModel model = (DefaultTableModel) scoreTable.getModel();
        model.setRowCount(0);
        for (Assessment a : shown) {
            model.addRow(new Object[]{a.getName(), a.getType(), a.isGraded() ? Course.show(a.getScore()) : "not graded",
                    Course.show(a.getTotal()), Course.percent(a.getPercent())});
        }
    }

    // ---------------- scores ----------------

    private void addScore() {
        try {
            Course c = selectedCourse();
            if (c == null) throw new GradeException("Create or select a course first.");
            String type = (String) typeBox.getSelectedItem();
            double total = Input.number(totalField.getText(), "'Out of'");
            if (scoreField.getText().trim().isEmpty()) {
                c.addScore(type, nameField.getText(), total);   // not graded yet
            } else {
                c.addScore(type, nameField.getText(), Input.score(scoreField.getText()), total);
            }
            nameField.setText("");
            scoreField.setText("");
            totalField.setText("");
            app.dataChanged();
        } catch (GradeException e) {
            app.showError(e.getMessage());
        }
    }

    /** The score picked in the table, or null (with a message) if none is picked. */
    private Assessment selectedScore() {
        int row = scoreTable.getSelectedRow();
        if (row < 0 || row >= shown.size()) {
            app.showError("Select a score in the table first.");
            return null;
        }
        return shown.get(row);
    }

    private void updateScore() {
        Assessment a = selectedScore();
        if (a == null) return;
        String text = JOptionPane.showInputDialog(this, "New score for \"" + a.getName() + "\" (out of "
                + Course.show(a.getTotal()) + ").\nLeave empty to mark it as not graded.",
                a.isGraded() ? Course.show(a.getScore()) : "");
        if (text == null) return;   // the user pressed Cancel
        try {
            a.setScore(Input.score(text));
            app.dataChanged();
        } catch (GradeException e) {
            app.showError(e.getMessage());
        }
    }

    private void deleteScore() {
        Assessment a = selectedScore();
        if (a != null && JOptionPane.showConfirmDialog(this, "Delete \"" + a.getName() + "\"?",
                "Delete score", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            selectedCourse().removeScore(a);
            app.dataChanged();
        }
    }

    // ---------------- courses ----------------

    private void newCourse() {
        JTextField name = new JTextField();
        JTextField credits = new JTextField("3");
        String[] defaults = {"20", "20", "40", "20", "0", "0", "0", "0", "0"};   // Quiz, Test, Exam, Lab, the rest 0
        JTextField[] weightFields = new JTextField[Course.TYPES.length];

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Course name"));
        form.add(name);
        form.add(new JLabel("Credit hours"));
        form.add(credits);
        for (int i = 0; i < Course.TYPES.length; i++) {
            weightFields[i] = new JTextField(defaults[i]);
            form.add(new JLabel(Course.TYPES[i] + " weight (%)"));
            form.add(weightFields[i]);
        }

        while (true) {   // after a mistake the form opens again, so nothing typed is lost
            if (JOptionPane.showConfirmDialog(this, form, "New course (weights must add up to 100%)",
                    JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
            try {
                double[] weights = new double[Course.TYPES.length];
                for (int i = 0; i < weights.length; i++) {
                    weights[i] = Input.number(weightFields[i].getText(), Course.TYPES[i] + " weight");
                }
                Course c = new Course(name.getText(), Input.wholeNumber(credits.getText(), "Credit hours"), weights);
                for (Course other : app.courses) {
                    if (other.getName().equalsIgnoreCase(c.getName()))
                        throw new GradeException("A course named \"" + c.getName() + "\" already exists.");
                }
                app.courses.add(c);
                app.dataChanged();
                courseBox.setSelectedItem(c);
                return;
            } catch (GradeException e) {
                app.showError(e.getMessage());
            }
        }
    }

    private void deleteCourse() {
        Course c = selectedCourse();
        if (c == null) {
            app.showError("There is no course to delete.");
        } else if (JOptionPane.showConfirmDialog(this, "Delete \"" + c.getName() + "\" and all of its scores?",
                "Delete course", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            app.courses.remove(c);
            app.dataChanged();
        }
    }
}

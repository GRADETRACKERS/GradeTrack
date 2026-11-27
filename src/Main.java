import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The window. It keeps the list of courses that all three tabs share.
 * After any change, dataChanged() saves the file and redraws every tab.
 */
public class Main extends JFrame {

    ArrayList<Course> courses = new ArrayList<>();
    private File file;

    private CoursesPanel coursesTab = new CoursesPanel(this);
    private TargetPanel targetTab = new TargetPanel(this);
    private SummaryPanel summaryTab = new SummaryPanel(this);

    public Main(File file) {
        super("GradeTrack - Student Grade and GPA Tracking System");
        this.file = file;
        try {
            courses = DataFile.load(file);
        } catch (IOException e) {
            showError("Could not read the saved data: " + e.getMessage());
        }

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Courses", coursesTab);
        tabs.addTab("Target grade", targetTab);
        tabs.addTab("Summary and GPA", summaryTab);
        add(tabs);

        refreshAll();
        setSize(900, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /** Call this after any change: it saves the file, then redraws every tab. */
    void dataChanged() {
        try {
            DataFile.save(courses, file);
        } catch (IOException e) {
            showError("Could not save your data: " + e.getMessage());
        } finally {
            refreshAll();   // runs even if saving failed
        }
    }

    void refreshAll() {
        coursesTab.refresh();
        targetTab.refresh();
        summaryTab.refresh();
    }

    void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Invalid input", JOptionPane.ERROR_MESSAGE);
    }

    /** Refills a course combo box and keeps the same course selected if it still exists. */
    void fill(JComboBox<Course> box) {
        Course selected = (Course) box.getSelectedItem();
        box.removeAllItems();
        for (Course c : courses) box.addItem(c);
        if (courses.contains(selected)) box.setSelectedItem(selected);
    }

    // ---------------- small helpers the panels share ----------------

    /** INHERITANCE again: a table model the user cannot type into. */
    static class ReadOnlyModel extends DefaultTableModel {
        ReadOnlyModel(String[] columns) { super(columns, 0); }

        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    }

    static JTable newTable(String[] columns) { return new JTable(new ReadOnlyModel(columns)); }

    /** A left-aligned row of components. */
    static JPanel row(Component... items) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (Component item : items) panel.add(item);
        return panel;
    }

    /** A button that tells the given listener (a panel) when it is clicked. */
    static JButton button(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        return button;
    }

    public static void main(String[] args) {
        // the data file: given as an argument (the website does this), or in the user's home folder
        File file = args.length > 0
                ? new File(args[0])
                : new File(System.getProperty("user.home"), ".gradetrack/gradetrack_data.txt");
        new Main(file).setVisible(true);
    }
}

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Font;

/** Summary tab: every course with its grade, and the GPA weighted by credit hours. */
public class SummaryPanel extends JPanel {

    private Main app;
    private JLabel gpaLabel = new JLabel();
    private JTable table = Main.newTable(new String[]{"Course", "Credits", "Current grade", "Graded", "Grade point"});

    public SummaryPanel(Main app) {
        this.app = app;
        gpaLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        setLayout(new BorderLayout());
        add(gpaLabel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(new JLabel("1.00 is the highest grade. Courses with no graded work are not counted."), BorderLayout.SOUTH);
    }

    public void refresh() {
        double gpa = Gpa.compute(app.courses);
        gpaLabel.setText(gpa < 0 ? " GPA: -" : String.format(" GPA: %.2f", gpa));

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for (Course c : app.courses) {
            String point = c.currentGrade() < 0 ? "-" : String.format("%.2f", Gpa.gradePoint(c.currentGrade()));
            model.addRow(new Object[]{c.getName(), c.getCredits(), Course.percent(c.currentGrade()),
                    Course.percent(c.percentGraded()), point});
        }
    }
}

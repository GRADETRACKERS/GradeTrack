import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Target tab: pick a course and a target, see the average you still need. */
public class TargetPanel extends JPanel implements ActionListener {

    private Main app;
    private JComboBox<Course> courseBox = new JComboBox<>();
    private JTextField targetField = new JTextField("85", 5);
    private JTextArea result = new JTextArea("Choose a course and a target, then press Compute.", 4, 40);

    public TargetPanel(Main app) {
        this.app = app;
        result.setEditable(false);
        result.setLineWrap(true);
        result.setWrapStyleWord(true);
        result.setFont(new Font("SansSerif", Font.BOLD, 18));

        setLayout(new BorderLayout());
        add(Main.row(new JLabel("Course:"), courseBox, new JLabel("Target final grade (%):"), targetField,
                Main.button("Compute", this)), BorderLayout.NORTH);
        add(result, BorderLayout.CENTER);
    }

    public void refresh() { app.fill(courseBox); }

    /** Runs when the Compute button is clicked. */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            Course c = (Course) courseBox.getSelectedItem();
            if (c == null) throw new GradeException("Add a course first in the Courses tab.");
            double target = Input.number(targetField.getText().replace("%", ""), "Target grade");
            result.setText(c.adviceFor(target));
        } catch (GradeException ex) {
            app.showError(ex.getMessage());
        }
    }
}

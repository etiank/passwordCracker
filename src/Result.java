import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Result {

    public static void showResult(String time, String password) {

        JFrame frame = new JFrame("Result");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(240, 120);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        panel.add(new JLabel("Password: " + password));
        panel.add(new JLabel("Time taken: " + time));

        frame.add(panel);
        frame.setVisible(true);

    }

}

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GUI {

    public static String selectedMode = "";

    public static void GUI() {
        JFrame frame = new JFrame("Brute Force Password Cracker (Sequential)");
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null); // na sredi ekrana ce se prav spomnim
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        //panel.setLayout(new FlowLayout());
        //panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        //panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // DEFINING ELEMENTS
        JTextField hash_field = new JTextField("", 40);
        JRadioButton radio_md5 = new JRadioButton("MD5");
        JRadioButton radio_sha256 = new JRadioButton("SHA256");
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(radio_md5); buttonGroup.add(radio_sha256);
        JTextField char_set = new JTextField("[0-9A-Za-z!@#$%^&*()_\\-+=\\[\\]{};:'\",.<>/?\\\\|`~]", 25);
        JSlider length_slider = new JSlider(0,20,10);
        length_slider.setMajorTickSpacing(5);
        length_slider.setMinorTickSpacing(1);
        length_slider.setPaintTicks(true);
        length_slider.setPaintLabels(true);
        length_slider.setPaintTrack(true);
        length_slider.setSnapToTicks(true);
        JProgressBar progress = new JProgressBar(0,100);
        JButton button = new JButton("Crack ▶");

        // ADDING TO PANEL
        panel.add(new JLabel("Enter password hash:"));
        panel.add(hash_field);
        panel.add(new JLabel("Hashing type:"));
        panel.add(radio_md5); panel.add(radio_sha256);
        panel.add(new JLabel("Character set:"));
        panel.add(char_set);
        panel.add(new JLabel("Password length:"));
        panel.add(length_slider);
        panel.add(new JLabel("Progress:"));
        panel.add(progress);


        button.addActionListener(e -> {
            if (radio_md5.isSelected()){
                selectedMode = "md5";
            }
            if (radio_sha256.isSelected()){
                selectedMode = "sha256";
            }
        });

        panel.add(button);


        frame.add(panel);
        frame.setVisible(true);
    }
}
/*
Jtextfield  https://docs.oracle.com/javase/8/docs/api/javax/swing/JTextField.html
Jslider     https://docs.oracle.com/javase/tutorial/uiswing/components/slider.html
JProgress   https://docs.oracle.com/javase/8/docs/api/javax/swing/JProgressBar.html

 */
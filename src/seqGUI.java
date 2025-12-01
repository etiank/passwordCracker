import javax.swing.*;
import java.awt.*;

public class seqGUI {

    public static void GUI() {
        JFrame frame = new JFrame("Brute Force Password Cracker (Sequential)");
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null); // na sredi ekrana ce se prav spomnim
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // DEFINING ELEMENTS
        JTextField hash_field = new JTextField("72b302bf297a228a75730123efef7c41", 40);
        JRadioButton radio_md5 = new JRadioButton("MD5");
        JRadioButton radio_sha256 = new JRadioButton("SHA-256");
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
        panel.add(new JLabel("Enter password hash:")); panel.add(hash_field);
        panel.add(new JLabel("Hashing type:"));
        panel.add(radio_md5); panel.add(radio_sha256);
        panel.add(new JLabel("Character set:")); panel.add(char_set);
        panel.add(new JLabel("Password length:")); panel.add(length_slider);
        panel.add(new JLabel("Progress:")); panel.add(progress);


        button.addActionListener(e -> {
            String hash = hash_field.getText();
            String char_set2 = char_set.getText();
            int pwd_length = length_slider.getValue();
            if (pwd_length != 0){
                if (radio_md5.isSelected()){
                    Seq.runSeq(hash,"MD5", char_set2, pwd_length);
                }
                if (radio_sha256.isSelected()){
                    Seq.runSeq(hash,"SHA-256", char_set2, pwd_length);
                }
            } else {
                System.out.println("Password length cannot be 0!\n");
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

read jtextfield https://stackoverflow.com/questions/36936186/how-to-get-string-from-jtextfield-and-save-it-in-variable
read jslider    https://stackoverflow.com/questions/16586867/read-the-value-of-a-jslider

 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class parGUI {

    static String PATH = "/home/ket/IdeaProjects/passwordCracker/rockyou.txt";
    private static JButton button; private static JButton dictionaryButton;

    public static void GUI() {
        JFrame frame = new JFrame("Brute Force Password Cracker (Parallel)");
        frame.setSize(600, 200);
        frame.setLocationRelativeTo(null); // na sredi ekrana ce se prav spomnim
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // DEFINING ELEMENTS
        JTextField hash_field = new JTextField("84e3bc8f2edc71abb4e22e1163e921c9", 40); // "kljen" MD5
        JRadioButton radio_md5 = new JRadioButton("MD5");
        JRadioButton radio_sha256 = new JRadioButton("SHA-256");
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(radio_md5); buttonGroup.add(radio_sha256);
        JTextField char_set = new JTextField("[a-z]", 25); // [0-9A-Za-z!@#$%^&*()_\-+=\[\]{};:'",.<>/?\\|`~]  [0-9A-Za-z]
        JSlider length_slider = new JSlider(0,20,5);
        length_slider.setMajorTickSpacing(2);
        length_slider.setMinorTickSpacing(1);
        length_slider.setPaintTicks(true);
        length_slider.setPaintLabels(true);
        length_slider.setPaintTrack(true);
        length_slider.setSnapToTicks(true);
        JProgressBar progress = new JProgressBar(0,100);
        progress.setStringPainted(true);
        button = new JButton("Crack ▶");
        dictionaryButton = new JButton("rockyou.txt");

        // ADDING TO PANEL
        panel.add(new JLabel("Enter password hash:")); panel.add(hash_field);
        panel.add(new JLabel("Hashing type:"));
        panel.add(radio_md5); panel.add(radio_sha256);
        panel.add(new JLabel("Character set:")); panel.add(char_set);
        panel.add(new JLabel("Password length:")); panel.add(length_slider);
        panel.add(new JLabel("Progress:")); panel.add(progress);
        panel.add(new JLabel("Select dictionary:")); panel.add(dictionaryButton);

        // run button
        button.addActionListener(e -> {
            String hash = hash_field.getText();
            String char_set2 = char_set.getText();
            System.out.println("[char set]:"+char_set2);
            int pwd_length = length_slider.getValue();
            progress.setValue(0); // restart

            if (pwd_length != 0 && !char_set2.isEmpty()){
                if (radio_md5.isSelected()){
                    button.setEnabled(false); dictionaryButton.setEnabled(false);
                    new Thread(() -> { // rabi thread kr ce ne samo zamrzne
                        try {
                            Par.runPar(hash,"MD5", char_set2, pwd_length, progress, PATH);
                        } catch (NoSuchAlgorithmException ex) {
                            throw new RuntimeException(ex);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        //

                    }).start();
                }
                if (radio_sha256.isSelected()) {
                    button.setEnabled(false); dictionaryButton.setEnabled(false);
                    new Thread(() -> {
                        try {
                            Par.runPar(hash, "SHA-256", char_set2, pwd_length, progress, PATH);
                        } catch (NoSuchAlgorithmException ex) {
                            throw new RuntimeException(ex);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        //

                    }).start();
                }

            } else {
                System.out.println("[ERROR] Password length and character set cannot be null.\n");
            }
        });


        dictionaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                FileDialog fileDialog = new FileDialog((Frame) null, "Select the dictionary to be used.");
                fileDialog.setVisible(true);

                String selectedFile = fileDialog.getFile();

                if (selectedFile != null){
                    PATH = fileDialog.getDirectory() + selectedFile;
                    dictionaryButton.setText(selectedFile);
                    System.out.println("Selected dictionary: " + PATH);
                }


            }
        });



        panel.add(button);
        frame.add(panel);
        frame.setVisible(true);



    }

    public static void enableButtons(){
        button.setEnabled(true); dictionaryButton.setEnabled(true);
    }
}
/*
Jtextfield  https://docs.oracle.com/javase/8/docs/api/javax/swing/JTextField.html
Jslider     https://docs.oracle.com/javase/tutorial/uiswing/components/slider.html
JProgress   https://docs.oracle.com/javase/8/docs/api/javax/swing/JProgressBar.html

read jtextfield https://stackoverflow.com/questions/36936186/how-to-get-string-from-jtextfield-and-save-it-in-variable
read jslider    https://stackoverflow.com/questions/16586867/read-the-value-of-a-jslider

 */
import mpi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

// Maybe add `-Djava.awt.headless=false` in the VM options instead of in the code
/*"kljen"
        MD5:        84e3bc8f2edc71abb4e22e1163e921c9
        SHA-256:    84272a03a83349e39109d3fdb12cfbc746ffdc6968a180145a6dc7653f15cc63 */
public class Main {


    public static void main(String[] args) {
        ///  SELECT RUN MODE BY CHANGING THIS VALUE
        int input = 2;
        ///  1 : Sequential
        ///  2 : Parallel
        ///  3 : Distributed


        //System.setProperty("java.awt.headless", "false");

        MPI.Init(args);
        int me = MPI.COMM_WORLD.Rank(); // current process
        int nodes = MPI.COMM_WORLD.Size(); // 5 i think
        System.out.println("[" + me + "] NODE STARTED");


        // maybe a hash generator class -> add a "Generate random hash" button in GUI

        if(me == 0) {
            switch (input) {
                case 1:
                    seqGUI.GUI();
                    MPI.Finalize();
                    break;
                case 2:
                    parGUI.GUI();
                    System.out.println("Available cores: " + Runtime.getRuntime().availableProcessors() + " - 1"); //
                    MPI.Finalize();
                    break;
                case 3:
                    GUI_MPJ(nodes, me);
                    break;
                case 0:
                    Result.showResult("69.7s", "Banana");
                    break;
                default:
            }
        }
        // MPJ

        if(me == 0){ // ROOT -> GUI, send the ranges to workers, collect

        } else { // WORKER -> receive range, compute -> can i use functions class? I need new function, send back

            // RECEIVE
            int[] pwd_lengthBuffer = new int[1];
//            System.out.println("["+me+"] WORKERS");
            MPI.COMM_WORLD.Bcast(pwd_lengthBuffer, 0, 1, MPI.INT, 0); //
//            System.out.println("["+me+"] RECV pwd_length: " + pwd_lengthBuffer[0]);
            int pwd_length = pwd_lengthBuffer[0];

            int[] hash_length = new int[1];
            MPI.COMM_WORLD.Bcast(hash_length, 0, 1, MPI.INT, 0); //
//            System.out.println("["+me+"] RECV hash_length: " + hash_length[0]);

            char[] hash_buffer = new char[hash_length[0]];
            MPI.COMM_WORLD.Bcast(hash_buffer, 0, hash_length[0], MPI.CHAR, 0); // [✔]
//          String hash = Arrays.toString(hash_buffer);
            String hash = new String(hash_buffer);
//            System.out.println("["+ me+ "] RECV hash: " + hash);

            int[] char_set_length = new int[1];
            MPI.COMM_WORLD.Bcast(char_set_length, 0, 1, MPI.INT, 0); // [✔]
//            System.out.println("["+ me+ "] RECV char_set_length: " + char_set_length[0]);

            char[] char_set = new char[char_set_length[0]];
            MPI.COMM_WORLD.Bcast(char_set, 0, char_set_length[0], MPI.CHAR, 0); // [✔]
//            System.out.println("["+ me+ "] RECV char_set: " + Arrays.toString(char_set));

            int[] hash_type_length = new int[1];
            MPI.COMM_WORLD.Bcast(hash_type_length, 0, 1, MPI.INT, 0); // [✔]
//            System.out.println("["+ me+ "] RECV hash_type_length: " + hash_type_length[0]);

            char[] hash_type_buffer = new char[hash_type_length[0]];
            MPI.COMM_WORLD.Bcast(hash_type_buffer, 0, hash_type_length[0], MPI.CHAR, 0); // [✔]
//            System.out.println("["+ me+ "] RECV hash_type: " + Arrays.toString(hash_type_buffer));
            String hash_type = new String(hash_type_buffer);

            char[] range = new char[2];
            char[] sendBuff = new char[2*nodes];// bc for some reason
            MPI.COMM_WORLD.Scatter(
                    sendBuff, 0, 2, MPI.CHAR,
                    range, 0, 2, MPI.CHAR, 0);
//            System.out.println("["+ me+ "] RECV range from " + range[0] + " to " + range[1]);
            char starChar = range[0]; char endChar = range[1];

            // COMPUTE
            System.out.println("["+me+"] BRUTE FORCE START");
            String[] result;
            try {
                result = Functions.distributedBruteForceGenerator(pwd_length, char_set, starChar, endChar, hash, hash_type, me, nodes);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            System.out.println("["+me+"] RESULT: " + result[0]);
            System.out.println("["+me+"] LOCAL ATTEMPTS: " + result[1]);
            long[] attempts = new long[1]; attempts[0] = Long.parseLong(result[1]);

            // SEND BACK
            // Attempts & found password
            long[] total_attempts = new long[nodes];

            MPI.COMM_WORLD.Reduce(
                    attempts, 0, total_attempts, 0, 1, MPI.LONG, MPI.SUM, 0
            );

            // if found passowrd, MPI.Send
            if( !result[0].isEmpty()){
                // send source?
                Object[] buffer = new Object[]{result[0]};
//                System.out.println("["+me+"] SENDING PASSWORD: " + Arrays.toString(buffer));
                MPI.COMM_WORLD.Send(buffer, 0, 1, MPI.OBJECT, 0, 69);
//                System.out.println("["+me+"] PASSWORD SENT");
            }

            // sending password size
//            int[] result_pwd_length = new int[result[0].length()];
//            System.out.println("["+me+"] SENDING PASSWORD LENGTH: " + result[0].length());
//            MPI.COMM_WORLD.Gather(
//                    result_pwd_length, 0, 1, MPI.INT,
//                    null, 0, 1, MPI.INT, 0);
//            System.out.println("["+me+"] PASSWORD LENGTH SENT");
//            // sending passwords
//            char[] results = result[0].toCharArray();
//            System.out.println("["+me+"] SENDING PASSWORD: " + Arrays.toString(results));
//            MPI.COMM_WORLD.Gather(
//                    results, 0, 1, MPI.CHAR,
//                    null, 0, 1, MPI.CHAR, 0);
//            System.out.println("["+me+"] PASSWORD SENT");
        }

        MPI.Finalize();
    }

    static String PATH = "/home/ket/IdeaProjects/passwordCracker/rockyou.txt";
    private static JButton button; private static JButton dictionaryButton;

    public static void GUI_MPJ(int nodes, int me){ // ROOT
        JFrame frame = new JFrame("Brute Force Password Cracker (Distributed)");
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
        JTextField char_set = new JTextField("[a-z]", 25); // [0-9A-Za-z!@#$%^&*()_\-+=\[\]{};:'",.<>/?\\|`~]
        //char_set.setToolTipText(". - all [a-z] - lowercase [A-Z] - uppercase [0-9] - numerical");
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
            int char_set3 = Functions.createCharSet(char_set2).length;
            int pwd_length = length_slider.getValue();
            progress.setValue(0); // restart prog. bar
            if (pwd_length != 0 && !char_set2.isEmpty() && !(nodes > char_set3)){
                if (radio_md5.isSelected()){
                    button.setEnabled(false); dictionaryButton.setEnabled(false);
                    new Thread(() -> { // rabi thread kr ce ne samo zamrzne

                        try {
                            Distr.runDistr(hash,"MD5", char_set2, pwd_length, progress, PATH, me, nodes);
                        } catch (NoSuchAlgorithmException ex) {
                            throw new RuntimeException(ex);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }

                    }).start();
                }
                if (radio_sha256.isSelected()) {
                    button.setEnabled(false); dictionaryButton.setEnabled(false);
                    new Thread(() -> {

                        try {
                            Distr.runDistr(hash,"SHA-256", char_set2, pwd_length, progress, PATH, me, nodes);
                        } catch (NoSuchAlgorithmException ex) {
                            throw new RuntimeException(ex);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }

                    }).start();
                }

            } else {
                System.out.println("[ERROR] Password length and character set cannot be null. Character set cannot be smaller than the amount of nodes: " + nodes + "\n");
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
import javax.swing.*;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Seq {

    public static void runSeq(String hash, String hash_type, String char_set, int pwd_length, JProgressBar progress) throws NoSuchAlgorithmException, IOException {

        //String PATH = "C:\\Users\\Etian\\passwordCracker\\rockyou.txt";
        String PATH = "/home/ket/IdeaProjects/passwordCracker/rockyou.txt";
        System.out.println("[input hash]: " + hash + " \n[hash_type]: " + hash_type + " \n[char_set]: " + char_set + " \n[length]: " + pwd_length +"\n");
        //long lines = 14344392;
        Pattern pattern = Pattern.compile("^" +char_set + "+$");
        //System.out.println("Pattern: " + pattern);
        progress.setString("Reading dictionary..");


        //List<String> fileStream = Files.readAllLines(Paths.get(PATH)); CRASHES BECAUSE IT LOADS THE WHOLE FILE INTO MEMORY FIRST
        //long lines = fileStream.size();

        // Have a string and I need a char[] (bolj efficient);
        char[] char_set_arr = Functions.createCharSet(char_set); // ✓
        long possible_combs = (long) Math.pow(char_set_arr.length, pwd_length);
        //System.out.print("Does this work? ");
        //for (int i = 0; i < char_set_arr.length; i++) {System.out.print( i + " " + char_set_arr[i] + " ");}

//      rockyou.txt = 14344392 lines
        long lines = 0; // kr rabim total lines of n length, ne vse
        try (BufferedReader reader = new BufferedReader(new FileReader(PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.length() == pwd_length && pattern.matcher(line).matches()) {
                    lines++;
                }
            }
        }
        //System.out.println("[Total lines]: "+ lines);


        // BUFFERED READER SET-UP
        BufferedReader rdr;
        try {rdr = new BufferedReader(new FileReader(PATH));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);}

        progress.setString("Dictionary attack..");

        int attempts = 1;
        long t0 = System.currentTimeMillis(); long t;

        // THE Loop
        String currentLine;
        long currentprogress = 0;
        while (true) {
            if (!((currentLine = rdr.readLine()) != null)) break;
            if (currentLine.length() != pwd_length) continue;
            if (!pattern.matcher(currentLine).matches()) continue;
            //System.out.println("[" + attempts + "] " + "Dictionary entry: " + currentLine);
            String dict_hash = Functions.hash_it(currentLine, hash_type);
            //System.out.println("input hash: "+hash);
            //System.out.println("Hashed dictionary entry: " + dict_hash);
            if (dict_hash.equalsIgnoreCase(hash)) break; // gregor
            attempts++; currentprogress++;
            int percent = Functions.computeProgress(currentprogress, lines);
            SwingUtilities.invokeLater(() -> {
                progress.setValue(percent);
            });
        }

        t = System.currentTimeMillis() - t0;

/*
        // TURN THIS INTO AN IF() PLEASE
        switch (currentLine) {
            case null:  // Dictionary attack failed, BRUTE FORCE
                System.out.println("[Dictionary attack] failed. [time]: " + Functions.time(t));
                progress.setValue(0);
                progress.setString("Brute force attack..");
                Functions.bruteForceGenerator(pwd_length, char_set_arr, hash, hash_type, possible_combs, attempts, t0, progress);
                break;

            default: // Dictionary attack success, END
                t = System.currentTimeMillis() - t0;
                progress.setValue(100);
                progress.setString("Success");
                System.out.println("[Dictionary attack] success.\n[pwd]: " + currentLine + " \n[time]: " + Functions.time(t) + " \n[attempts]: " + attempts);
                //Result.showResult(Functions.time(t), currentLine);

                break;
        }
*/
        if (!((currentLine) == null)){ // how to check if it's null, dio ken
            t = System.currentTimeMillis() - t0;
            progress.setValue(100);
            progress.setString("Success");
            System.out.println("[Dictionary attack] success.\n[pwd]: " + currentLine + " \n[time]: " + Functions.time(t) + " \n[attempts]: " + attempts);
        } else {
            System.out.println("[Dictionary attack] failed. [time]: " + Functions.time(t));
            progress.setValue(0);
            progress.setString("Brute force attack..");
            currentprogress = 0;
            //System.out.println("[possible combinations]: " + possible_combs);
            String pws_and_attempt = Functions.bruteForceGenerator(pwd_length, char_set_arr, hash, hash_type, possible_combs, attempts, currentprogress, progress);
            t = System.currentTimeMillis() - t0;
            // get password, attempts
            progress.setValue(100); progress.setString("Success");
            String[] output = pws_and_attempt.split("\n");
            System.out.println("[Brute force attack] success.\n[pwd]: " + output[0] + " \n[time]: " + Functions.time(t) + " \n[attempts]: " + output[1]);

        }


        // BRUTE FORCING
        // take char set
        // generate password of length n
/*
public static String BruteForce(int pwd_length, char[] char_set,){


        //System.out.println("List char set: " + list_char_set);

        System.out.println("Possible brute force combinations: " + possible_combs);

        //Iterable<String> word = Functions.generateString(pwd_length, Arrays.toString(list_char_set));
        currentprogress = 0;

        for (String password : word) {

            String hash_pwd = Functions.hash_it(hash, hash_type);
            System.out.println("[" + currentprogress + "]" + hash_pwd);
            if (hash_pwd.equalsIgnoreCase(hash)) {
                t = System.currentTimeMillis() - t0;
                progress.setValue(100);
                progress.setString("Success");
                System.out.println("[Brute force attack] success.\n[pwd]: " + password + " \n[time]: " + Functions.time(t) + " \n[attempts]: " + attempts);
                break;
            }
            attempts++;
            currentprogress++;
            int percent2 = Functions.computeProgress(currentprogress, possible_combs);
            SwingUtilities.invokeLater(() -> {
                progress.setValue(percent2);
            });

            //attempt++;

        }
    }


*/


        //System.out.println("Jusni praprot");
        //System.out.println("Hash of input is " + hashed_result);
    }
}

// Burrefered reader    https://docs.oracle.com/javase/8/docs/api/java/io/BufferedReader.html
//  rockyou.txt     https://github.com/dw0rsec/rockyou.txt
// read buf rdr     https://www.w3schools.com/java/java_bufferedreader.asp
// iterable         https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Iterable.html

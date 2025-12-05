import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLOutput;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Seq {

    public static void runSeq(String hash, String hash_type, String char_set, int pwd_length, JProgressBar progress) throws NoSuchAlgorithmException, IOException {

        String PATH = "C:\\Users\\Etian\\passwordCracker\\rockyou.txt";
        System.out.println("[input hash]: " + hash + " \n[hash_type]: " + hash_type + " \n[char_set]: " + char_set + " \n[length]: " + pwd_length +"\n");
        //long lines = 14344392;
        Pattern pattern = Pattern.compile("^" +char_set + "+$");
        //System.out.println("Pattern: " + pattern);
        progress.setString("Reading dictionary..");

        //List<String> fileStream = Files.readAllLines(Paths.get(PATH)); CRASHES BECAUSE IT LOADS THE WHOLE FILE INTO MEMORY FIRST
        //long lines = fileStream.size();

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
            System.out.println("[" + attempts + "] " + "Dictionary entry: " + currentLine);
            String dict_hash = Hash_it.hash_it(currentLine, hash_type);
            //System.out.println("input hash: "+hash);
            //System.out.println("Hashed dictionary entry: " + dict_hash);
            if (dict_hash.equalsIgnoreCase(hash)) break; // gregor
            attempts++; currentprogress++;
            int percent = Hash_it.computeProgress(currentprogress, lines);
            SwingUtilities.invokeLater(() -> {
                progress.setValue(percent);
            });
        }

        t = System.currentTimeMillis() - t0;

        // outputs
        switch (currentLine) {
            case null:
                System.out.println("[Dictionary attack] failed. [time]: " + Hash_it.time(t));
                progress.setValue(100);
                break;
            default:
                t = System.currentTimeMillis() - t0;
                progress.setValue(100);
                progress.setString("Success");
                System.out.println("[Dictionary attack] success.\n[pwd]: " + currentLine + " \n[time]: " + Hash_it.time(t) + " \n[attempts]: " + attempts);
                //System.exit(0);
                break;
        }

        //System.out.println("Jusni praprot");
        //System.out.println("Hash of input is " + hashed_result);
    }
}

// Burrefered reader    https://docs.oracle.com/javase/8/docs/api/java/io/BufferedReader.html
//  rockyou.txt     https://github.com/dw0rsec/rockyou.txt
// read buf rdr     https://www.w3schools.com/java/java_bufferedreader.asp

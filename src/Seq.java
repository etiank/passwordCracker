import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLOutput;

public class Seq {

    public static void runSeq(String hash, String hash_type, String char_set, int pwd_length) throws NoSuchAlgorithmException, IOException {

        String mystring = "banana";
        String PATH = "C:\\Users\\Etian\\passwordCracker\\dictionary.txt";

        System.out.println(hash + " hash_type " + hash_type + " char_set: " + char_set + " length: " + pwd_length);

        BufferedReader rdr;
        try {rdr = new BufferedReader(new FileReader(PATH));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);}

        long t0 = System.currentTimeMillis(); long t;
        int attempts = 1;

        // THE Loop
        String line;
        while (true) {
            if (!((line = rdr.readLine()) != null)) break;
            if (line.length() != pwd_length) continue;
            //System.out.println("Dictionary entry: " + line);
            String dict_hash = Hash_it.hash_it(line, hash_type);
            //System.out.println("input hash: "+hash);
            //System.out.println("Hashed dictionary entry: " + dict_hash);
            if (dict_hash.equalsIgnoreCase(hash)) break; // gregor
            attempts++;
        }

        t = System.currentTimeMillis() - t0;

        switch (line) {
            case null:
                System.out.println("[Dictionary attack] failed.");
                break;
            default:
                System.out.println("[Dictionary attack] success. [pwd]: " + line + " [time] " + t + " ms" + " [attempts]: " + attempts);
                break;
        }








        //System.out.println("Hash of input is " + hashed_result);



    }

    // FUNCTION to HASH IT


}

// Burrefered reader    https://docs.oracle.com/javase/8/docs/api/java/io/BufferedReader.html
//  rockyou.txt     https://github.com/dw0rsec/rockyou.txt
// read buf rdr     https://www.w3schools.com/java/java_bufferedreader.asp

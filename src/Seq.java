import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLOutput;

public class Seq {

    public static void runSeq(String hash, String hash_type, String char_set, int pwd_length) {


        String mystring = "banana";
        String PATH = "C:\\Users\\Etian\\passwordCracker\\rockyou.txt";

        System.out.println(hash + " " + hash_type + " " + char_set + " " + pwd_length);

        // now we have the hash to compare to the computed hashes ane
        // need a list of words ane
        // hash them

        // GET DICTIONARY (ROCKYOU.TXT)
        // GO THROUGH DICTIONARY
        // read file, buffered reader?
        BufferedReader rdr;
        try {rdr = new BufferedReader(new FileReader(PATH));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);}

        String line;
        while (true) {
            try {
                if (!((line = rdr.readLine()) != null)) break;
            } catch (IOException e) {throw new RuntimeException(e);}
            try {
                hash_it(rdr.readLine(), hash_type);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //System.out.println(line);
        }








        //System.out.println("Hash of input is " + hashed_result);



    }

    // FUNCTION to HASH IT


}

// Burrefered reader    https://docs.oracle.com/javase/8/docs/api/java/io/BufferedReader.html
//  rockyou.txt     https://github.com/dw0rsec/rockyou.txt
// read buf rdr     https://www.w3schools.com/java/java_bufferedreader.asp

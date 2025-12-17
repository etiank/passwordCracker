import javax.swing.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Functions {

    /// Gets string to hash and the hashing alg to use, and returns the digest. ✓
    public static String hash_it(String hash, String hashType) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance(hashType); // initialize it
        //md.update(hash.getBytes()); // to update the msg digest
        byte[] digest = md.digest(hash.getBytes());
        //String output = new String(digest);
        Formatter formatter = new Formatter();  // ┐
        for (byte d : digest){                  // │
            formatter.format("%02x", d);        // │    gregor
        }                                       // ┘
        String output = formatter.toString();
        formatter.close();
        return output;
    }

    /// Just returns the progress in a percentage to be able to update the JProgressBar. ✓
    public static int computeProgress(long currentProgress, long lines){
        int output = (int) (currentProgress * 100 / lines);
        return output;
    }

    /// Just converts the time from simply ms to also seconds and minutes if applicable ✓
    public static String time(long ms) {
        if (ms < 1000) return ms + "ms.";

        long seconds = ms / 1000;
        if (seconds<60) return seconds + "s " + (ms % 1000) + "ms.";

        long minutes = seconds / 60;
        if (minutes < 60) return minutes + " min " + (seconds % 60) + "s " + (ms % 1000) + "ms";
        return "";
    }

    /// Gets the regex-style char set definition, expands it and returns a char array ✓
    public static char[] createCharSet(String input){ // ✓
        char[] char_set = new char[input.length()];
        List<Character> char_list = new ArrayList<>();
        String stripped_input = input.replaceAll("[\\[\\]]", "");
        //System.out.println("stripped input: " + stripped_input);

        if (input.equals(".")) {    // every character
            for (char c = 32; c < 127; c++) { // ASCII
                char_list.add(c);
                //System.out.print("test" + char_list.get(c-1));
            }
            return toCharArr(char_list);
        }

        for (int i = 0; i < stripped_input.length(); i++) {
            char c = stripped_input.charAt(i);

            if (i + 2 < stripped_input.length() && stripped_input.charAt(i + 1) == '-') {   // ┐
                char end = stripped_input.charAt(i + 2);                                    // │    gpt
                for (char c1 = c; c1 <= end; c1++) {                                        // │
                    char_list.add(c1);                                                      // │
                }                                                                           // │
                i += 2;                                                                     // │
            } else  char_list.add(c);                                                       // ┘
        }
        return toCharArr(char_list);
    }

    /// The function that converts a character list into a char array ✓
    public static char[] toCharArr(List<Character> input){ // ✓
        char[] char_set = new char[input.size()];
            for (int i = 0; i < input.size(); i++) {
                char_set[i] = input.get(i);
            }
            return char_set;
    }

    /// Needs to take in the pwd_length, char_set, original hash, hasy_type. pa kaj se
    /// Iteratively generate string, hash it and compare it to the orignal hash: andrej's predlog btw
    /// Needa return the actual broken Password, n. of Attempts, time?,
    public static String bruteForceGenerator(int pwd_length, char[] char_set, String hash, String hash_type, long possible_combs, int attempts, long currentProgress, JProgressBar progress) throws NoSuchAlgorithmException {

        int[] indices = new int[pwd_length]; // initializing
        char[] currentGuess = new char[pwd_length];
        Arrays.fill(currentGuess, char_set[0]); // fill the char arr with the first char in char_set
        String password = "";

        while (true){ // compare the current guess
            String strGuess = new String(currentGuess);
            //System.out.println("["+attempts + "] guess: " + strGuess + " total combs: " + possible_combs);
            if (hash_it(strGuess, hash_type).equalsIgnoreCase(hash)){
                password = strGuess; break;}

            // Iterate string
            int len = pwd_length - 1;
            while (len >= 0){
                indices[len]++;
                if (indices[len] < char_set.length){
                    currentGuess[len] = char_set[indices[len]];
                    break;
                } else {
                    indices[len] = 0;
                    currentGuess[len] = char_set[0];
                    len--;
                }
            }
            attempts++; currentProgress++;
            // update progress bar
            int percent = Functions.computeProgress(currentProgress, possible_combs);
            SwingUtilities.invokeLater(() -> {
                progress.setValue(percent);
            });

            if(len < 0) break;
            // TODO, how to keep track of attempts / return the discovered
            // password & number of attempts, and compute time (?) ce nisem ze
        }

        return password + "\n" + attempts;

        //

    }



}
    // MessageDigest         https://www.baeldung.com/java-md5

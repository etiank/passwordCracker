import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class Functions {

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

    public static int computeProgress(long currentProgress, long lines){
        int output = (int) (currentProgress * 100 / lines);
        return output;
    }

    public static String time(long ms) {
        if (ms < 1000) return ms + "ms.";

        long seconds = ms / 1000;
        if (seconds<60) return seconds + "s " + (ms % 1000) + "ms.";

        long minutes = seconds / 60;
        if (minutes < 60) return minutes + " min " + (seconds % 60) + "s " + (ms % 1000) + "ms";
        return "";
    }

    public static List<Character> toList(String char_set){

        List<Character> char_list = new ArrayList<>();
        //strip brackets
        String stripped = char_set.replaceAll("[\\[\\]]", "");

        if (char_set.equals(".")) {
            for (char c = 32; c < 127; c++) { // ASCII chacarters
                char_list.add(c);
            }
            return char_list;
        }

        for (int i = 0; i < stripped.length(); i++) {
            char c = stripped.charAt(i);

            if (i + 2 < stripped.length() && stripped.charAt(i+1) == '-'){      // ┐
                char end = stripped.charAt(i+2);                                // │    gpt
                for (char c1 = c; c1 <= end ; c1++) {                           // │
                    char_list.add(c1);                                          // │
                }                                                               // │
                i += 2;                                                         // │
            } else {                                                            // │
                char_list.add(c);                                               // ┘
            }
        }
        return char_list;
    }

    // MessageDigest         https://www.baeldung.com/java-md5

}

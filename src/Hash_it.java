import java.awt.event.FocusAdapter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class Hash_it {

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
        if (minutes < 60) return minutes + "min " + (seconds % 60) + "s.";
        return "";
    }

    // MessageDigest         https://www.baeldung.com/java-md5

}

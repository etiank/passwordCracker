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

    // MessageDigest         https://www.baeldung.com/java-md5

}

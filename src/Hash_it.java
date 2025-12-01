import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash_it {
    private static String hash_it(String hash, String hashType) {

        MessageDigest md;
        try {
            md = MessageDigest.getInstance(hashType);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        byte[] dgst = md.digest();

        return hash;
    }

}

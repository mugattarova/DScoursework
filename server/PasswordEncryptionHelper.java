import java.security.NoSuchAlgorithmException;  
import java.security.SecureRandom;  
import java.security.spec.InvalidKeySpecException;  
import java.util.Arrays;
import java.util.Random;  
import javax.crypto.SecretKeyFactory;  
import javax.crypto.spec.PBEKeySpec;  

public class PasswordEncryptionHelper {
    private static final Random random = new SecureRandom();  
    private static final String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";  
    private static final int keylength = 256;  
    
    public static String genPasswordSalt(int length){
        String out = "";
        for (int i = 0; i < length; i++) {  
            out += characters.charAt(random.nextInt(characters.length()));  
        }
        return out;
    }

    public static byte[] encrypt(String password, String salt){
        char[] passChar = password.toCharArray();
        byte[] saltByte = salt.getBytes();
        PBEKeySpec spec = new PBEKeySpec(passChar, saltByte, 1000, keylength);  
        Arrays.fill(passChar, Character.MIN_VALUE);  
        try {  
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");  
            return skf.generateSecret(spec).getEncoded();  
        }   
        catch (NoSuchAlgorithmException  | InvalidKeySpecException e){  
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);  
        }
        finally {
            spec.clearPassword();  
        }
    }
}

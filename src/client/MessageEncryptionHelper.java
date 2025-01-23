import java.security.*;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.nio.file.*;
import java.security.spec.*;
import java.io.FileInputStream;

public class MessageEncryptionHelper {
        
    public static byte[] objectToByteArray(Serializable o){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);   
            out.writeObject(o);
            out.flush();
            byte[] yourBytes = bos.toByteArray();
            return yourBytes;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Serializable byteArrayToObject(byte[] b){
        ByteArrayInputStream bis = new ByteArrayInputStream(b);
        ObjectInputStream in = null;
        try {
          in = new ObjectInputStream(bis);
          Serializable o = (Serializable) in.readObject(); 
          return o;
        } catch (Exception e ) {
            e.printStackTrace();
        }

        try {
            if (in != null) {
                in.close();
            }
        } catch (Exception e) {}

        return null;
    }
    public static boolean verify(PublicKey pub, SignedMessage<?> msg){
        try {
            byte[] signature = msg.getSignature();
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(pub);
            sig.update(objectToByteArray(msg.getMessage()));
            return sig.verify(signature);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static byte[] hashMessage(Serializable mes){
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(objectToByteArray(mes));
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static PrivateKey getPrivKey(String file){
        try{
            byte[] keyBytes = Files.readAllBytes(Paths.get(file));
        
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(spec);
        } catch(Exception e){ e.printStackTrace(); }
        return null;
    }
    public static PublicKey getPubKey(String file) {
        try {
            FileInputStream fos = new FileInputStream(file);
            byte[] keyBytes = fos.readAllBytes();
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            fos.close();
            return kf.generatePublic(spec);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

}

import java.security.*;
import java.security.interfaces.*;

public class KeyGeneration {
    public static KeyGeneration Instance;
    private static RSAPublicKey pubKey;
    private static RSAPrivateKey privKey;

    private KeyGeneration(){
        try{
        System.out.println("Creating new key generation instance");
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        pubKey = (RSAPublicKey) keyPair.getPublic();
        privKey = (RSAPrivateKey) keyPair.getPrivate();

        // File pubKeyFile = new File("publickeyServer.txt");
        // pubKeyFile.createNewFile();
        // File privKeyFile = new File("privatekeyServer.txt");
        // privKeyFile.createNewFile();

        // FileOutputStream pubSt = new FileOutputStream(pubKeyFile);
        // FileOutputStream privSt = new FileOutputStream(privKeyFile);
        // pubSt.write( Base64.getEncoder().encodeToString(pubKey.getEncoded()).getBytes() );
        // privSt.write( Base64.getEncoder().encodeToString(privKey.getEncoded()).getBytes() );
        // pubSt.close();
        // privSt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static KeyGeneration getInstance() {
        if(Instance == null) {
            Instance = new KeyGeneration();
        }
        return Instance;
    }

    // public static void main(String[] args) {
    //     try {
            
    //         KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    //         keyPairGenerator.initialize(2048);
    //         KeyPair keyPair = keyPairGenerator.genKeyPair();
    //         pubKey = (RSAPublicKey) keyPair.getPublic();
    //         privKey = (RSAPrivateKey) keyPair.getPrivate();

    //         File pubKeyFile = new File("publickeyServer.txt");
    //         pubKeyFile.createNewFile();
    //         File privKeyFile = new File("privatekeyServer.txt");
    //         privKeyFile.createNewFile();

    //         FileOutputStream pubSt = new FileOutputStream(pubKeyFile);
    //         FileOutputStream privSt = new FileOutputStream(privKeyFile);
    //         pubSt.write( Base64.getEncoder().encodeToString(pubKey.getEncoded()).getBytes() );
    //         privSt.write( Base64.getEncoder().encodeToString(privKey.getEncoded()).getBytes() );
    //         pubSt.close();
    //         privSt.close();

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }
    public RSAPrivateKey getPrivKey() {
        return privKey;
    }
    public RSAPublicKey getPubKey() {
        return pubKey;
    }
}

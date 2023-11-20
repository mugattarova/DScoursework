import java.security.*;
import java.io.Serializable;

public class SignedMessage implements Serializable{
    Serializable message;
    byte[] hashedMessage;
    byte[] signature;

    public SignedMessage(Serializable _message, PrivateKey privKey){
        message = _message;

        try {
            hashedMessage = MessageEncryptionHelper.hashMessage(message);

            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign(privKey);
            sig.update(MessageEncryptionHelper.objectToByteArray(message));
            signature = sig.sign();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Serializable getMessage() {
        return message;
    }
    public byte[] getHashedMessage() {
        return hashedMessage;
    }
    public byte[] getSignature() {
        return signature;
    }

}

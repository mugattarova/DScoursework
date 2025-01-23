import java.security.*;
import java.io.Serializable;

public class SignedMessage<T extends Serializable> implements Serializable{
    private T message;
    private byte[] signature;

    public SignedMessage(T _message, PrivateKey privKey){
        message = _message;

        try {
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign(privKey);
            sig.update(MessageEncryptionHelper.objectToByteArray(message));
            signature = sig.sign();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public T getMessage() {
        return message;
    }
    public byte[] getSignature() {
        return signature;
    }

}

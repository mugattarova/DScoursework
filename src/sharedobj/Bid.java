import java.io.Serializable;

public class Bid implements Serializable{
    
    private float sumProposed;
    private String bidderName;
    private String email;

    public Bid(float _sumProposed, String _bidderName, String _email){
        sumProposed = _sumProposed;
        bidderName = _bidderName;
        email = _email;
    }

    public float getSum(){
        return sumProposed;
    }

    public String infoToString(){
        String out = "";

        out += "Sum of bid: " + sumProposed + "\n";
        out += "Name: " + bidderName + "\n";
        out += "Email: " + email + "\n";

        return out;
    }
}

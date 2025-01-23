import java.security.PrivateKey;
import java.util.Hashtable;
import java.io.Serializable;

public class BackendState implements Serializable{
    private PrivateKey privKey = MessageEncryptionHelper.getPrivKey("./private_key.der");
    private Hashtable<Integer, AuctionItem> auctionItems;
    private Hashtable<Integer, Auction> openAuctions;
    // email - salt
    private Hashtable<String, String> passwordSalt;
    // hashed salted password - UserAccount
    private Hashtable<String, UserAccount> accounts;

    public BackendState(Hashtable<Integer, AuctionItem> _auctionItems, Hashtable<Integer, Auction> _openAuctions, Hashtable<String, String> _passwordSalt, Hashtable<String, UserAccount> _accounts){
        auctionItems = _auctionItems;
        openAuctions = _openAuctions;
        passwordSalt = _passwordSalt;
        accounts = _accounts;
    }

    public Hashtable<Integer, AuctionItem> getAuctionItems() {
        return auctionItems;
    }

    public Hashtable<Integer, Auction> getOpenAuctions() {
        return openAuctions;
    }

    public Hashtable<String, String> getPasswordSalt() {
        return passwordSalt;
    }

    public Hashtable<String, UserAccount> getAccounts() {
        return accounts;
    }
}

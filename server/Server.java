import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Base64;
import java.security.*;

public class Server implements IAuctionApp{
    private PrivateKey privKey;
    private Hashtable<Integer, AuctionItem> auctionItems;
    private Hashtable<Integer, Auction> openAuctions;
    // email - salt
    private Hashtable<String, String> passwordSalt;
    // hashed salted password - UserAccount
    private Hashtable<String, UserAccount> accounts;

    public Server(){
        auctionItems = new Hashtable<Integer, AuctionItem>();
        openAuctions = new Hashtable<Integer, Auction>();
        passwordSalt = new Hashtable<String, String>();
        accounts = new Hashtable<String, UserAccount>();
        privKey = MessageEncryptionHelper.getPrivKey("D:/Personal/LU_Leipzig_University/3Y/311DS/coursework1/server/private_key.der");
        try{
            register("Bingus", "p", "p");
            register("Girlie", "l", "l");
            register("b1", "b1", "b1");
            register("b2", "b2", "b2");
            register("b3", "b3", "b3");
            register("s1", "s1", "s1");
            register("s2", "s2", "s2");
            register("s3", "s3", "s3");

            AuctionItem ai1 = new AuctionItem("Fancy vase", "A vase from the 18th century. Looks intricate.");
            AuctionItem ai2 = new AuctionItem("Spider-man Funko Pop", "An unopened funko pop. I guess someone will want it.");
            AuctionItem ai3 = new AuctionItem("Old horseshoe", "It is said to bring a lot of luck.");
            storeAuctionItem(ai1); storeAuctionItem(ai2); storeAuctionItem(ai3);
        
        } catch (Exception e) {e.printStackTrace();}
    }

    public static void main(String[] args) {
        try {
            Server s = new Server();
            String name = "myserver";
            IAuctionApp stub = (IAuctionApp) UnicastRemoteObject.exportObject(s, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);

            System.out.println("Server ready");

        } catch (Exception e) {
            System.err.println("Exception:");
            e.printStackTrace();
        }
    }

    public SignedMessage getItemInfo(int itemID) throws RemoteException{
        return new SignedMessage(auctionItems.get(itemID), privKey);
    }
    public SignedMessage getAuctionInfo(int auctionID) throws RemoteException{
        return new SignedMessage(openAuctions.get(auctionID).infoToString(), privKey);
    }
    public SignedMessage getAuction(int auctionID) throws RemoteException{
        return new SignedMessage(openAuctions.get(auctionID), privKey);
    }
    public void storeAuctionItem(AuctionItem aui) throws RemoteException{
        auctionItems.put(aui.getItemId(), aui);
    }
    public void storeAuctionItem(String _itemTitle, String _itemDescription) throws RemoteException{
        AuctionItem newItem = new AuctionItem(_itemTitle, _itemDescription);
        auctionItems.put(newItem.getItemId(), newItem);
    }
    public SignedMessage createForwardAuction(UserAccount owner, AuctionItem aui, float startingPrice, float reservePrice) throws RemoteException {
        ForwardAuction newAu = new ForwardAuction(owner, aui, startingPrice, reservePrice);
        openAuctions.put(newAu.getAuctionID(), newAu);
        System.out.println("New forward auction created, id " + newAu.getAuctionID());
        return new SignedMessage(newAu.getAuctionID(), privKey);
    }
    public SignedMessage createReverseAuction(UserAccount owner, AuctionItem aui) throws RemoteException{
        ReverseAuction newAu = new ReverseAuction(owner, aui);
        openAuctions.put(newAu.getAuctionID(), newAu);
        System.out.println("New reverse auction created, id " + newAu.getAuctionID());
        return new SignedMessage(newAu.getAuctionID(), privKey);
    }
    public SignedMessage createDoubleAuction(UserAccount owner, AuctionItem aui) throws RemoteException{
        DoubleAuction newAu = new DoubleAuction(owner, aui);
        openAuctions.put(newAu.getAuctionID(), newAu);
        System.out.println("New double auction created, id " + newAu.getAuctionID());
        return new SignedMessage(newAu.getAuctionID(), privKey);
    }
    public SignedMessage closeAuction(UserAccount acc, int auctionID) throws RemoteException {
        try {
            // check if has permission
            Auction au = openAuctions.get(auctionID);
            if(au.verifyUser(acc)){
                openAuctions.remove(auctionID);
                System.out.println("Auction closed, id " + au.getAuctionID());
            }
            String msg = au.closeAuction(acc);
            return new SignedMessage(msg, privKey);
        } catch (Exception e) {
            System.out.println("Cannot close auction, id " + auctionID);
            return new SignedMessage("Cannot close auction, id " + auctionID, privKey);
        } 
    }
    public SignedMessage submitForwAucBid(UserAccount acc, int auctionID, Bid bid) throws RemoteException{
        try{
            ForwardAuction forwAuction = (ForwardAuction) openAuctions.get(auctionID);
            if(forwAuction == null){
                return new SignedMessage("Auction not found. Bid was not successful.", privKey);
            } else {
                if(forwAuction.placeBid(acc, bid)){
                    System.out.println("Successful bid " + bid.getSum() + " at auction " + forwAuction.getAuctionID());
                    return new SignedMessage("Bid " + bid.getSum() + " was placed successfully.", privKey);
                } else {
                    System.out.println("Failed bid " + bid.getSum() + " at auction " + forwAuction.getAuctionID());
                    return new SignedMessage("Bid " + bid.getSum() + " was unsuccessful. Bid a higher amount.", privKey);
                }
            }
        } catch (Exception e) {
            return new SignedMessage("Bid was unsuccessful.", privKey);
        }
    }
    public SignedMessage submitRevAucBid(UserAccount acc, int auctionID, Bid bid) throws RemoteException{
        try{
            ReverseAuction revAuction = (ReverseAuction) openAuctions.get(auctionID);
            if(revAuction == null){
                return new SignedMessage("Auction not found. Bid was not successful.", privKey);
            } else {
                if(revAuction.placeBid(acc, bid)){
                    System.out.println("Successful bid " + bid.getSum() + " at auction " + revAuction.getAuctionID());
                    return new SignedMessage("Bid " + bid.getSum() + " was placed successfully.", privKey);
                } else {
                    System.out.println("Failed bid " + bid.getSum() + " at auction " + revAuction.getAuctionID());
                    return new SignedMessage("Bid " + bid.getSum() + " was unsuccessful", privKey);
                }
            }
        } catch (Exception e) {
            return new SignedMessage("Bid was unsuccessful.", privKey);
        }
    }
    public SignedMessage submitDoubleAucSell(UserAccount acc, int auctionID, Bid bid) throws RemoteException{
        try {
            DoubleAuction doubAuction = (DoubleAuction) openAuctions.get(auctionID);
            if(doubAuction == null){
                return new SignedMessage("Auction not found. Bid was not successful.", privKey);
            } else {
                if(doubAuction.placeSell(bid)){
                    System.out.println("Successful sell request " + bid.getSum() + " at auction " + doubAuction.getAuctionID());
                    return new SignedMessage("Sell request " + bid.getSum() + " was placed successfully.", privKey);
                } else {
                    System.out.println("Failed sell request " + bid.getSum() + " at auction " + doubAuction.getAuctionID());
                    return new SignedMessage("Sell request " + bid.getSum() + " was unsuccessful", privKey);
                }
            }
            
        } catch (Exception e) {
            return new SignedMessage("Sell request was uncsuccessful", privKey);
        }
    }
    public SignedMessage submitDoubleAucBuy(UserAccount acc, int auctionID, Bid bid) throws RemoteException{
        try {
            DoubleAuction doubAuction = (DoubleAuction) openAuctions.get(auctionID);
            if(doubAuction == null){
                return new SignedMessage("Auction not found. Bid was not successful.", privKey);
            } else {
                if(doubAuction.placeBuy(bid)){
                    System.out.println("Successful buy request " + bid.getSum() + " at auction " + doubAuction.getAuctionID());
                    return new SignedMessage("Buy request " + bid.getSum() + " was placed successfully.", privKey);
                } else {
                    System.out.println("Failed buy request " + bid.getSum() + " at auction " + doubAuction.getAuctionID());
                    return new SignedMessage("Buy request " + bid.getSum() + " was unsuccessful", privKey);
                }
            }
            
        } catch (Exception e) {
            return new SignedMessage("Buy request was uncsuccessful", privKey);
        }
    }
    public SignedMessage openAuctionsToString(String auctionType) throws RemoteException{
        String out = "";
        Enumeration<Auction> list = openAuctions.elements();
        Auction curElem;

        while(list.hasMoreElements()){
            curElem = list.nextElement();
            switch (auctionType) {
                case "f":
                    if(curElem instanceof ForwardAuction){
                        out += curElem.infoToString() + "\n";
                    }
                    break;
                case "r":
                    if(curElem instanceof ReverseAuction){
                        out += curElem.infoToString() + "\n";
                    }
                    break;
                case "d":
                    if(curElem instanceof DoubleAuction){
                        out += curElem.infoToString() + "\n";
                    }
                    break;
                case "all":
                    out +=curElem.infoToStringWithTypeString() + "\n";
                default:
                    break;
                }
        }
        return new SignedMessage(out, privKey);
    }
    public SignedMessage availableItemsToString() throws RemoteException{
        String out = "";
        Enumeration<AuctionItem> list = auctionItems.elements();
        
        while(list.hasMoreElements()){
            out += list.nextElement().infoToString();
            out += "\n";            
        }
        return new SignedMessage(out, privKey);
    }

    public SignedMessage register(String name, String email, String password) throws RemoteException{
        try{
        if(passwordSalt.get(email) != null){
            System.out.println("Failed to register " + email);
            return new SignedMessage(false, privKey);
        }
        
        String salt = PasswordEncryptionHelper.genPasswordSalt(32);
        passwordSalt.put(email, salt);
        byte[] hashedPass = PasswordEncryptionHelper.encrypt(password, salt);
        UserAccount acc = new UserAccount(name, email);
        accounts.put(Base64.getEncoder().encodeToString(hashedPass), acc);
        System.out.println("Registered new account " + email);
        return new SignedMessage(true, privKey);

        } catch (Exception e){
            e.printStackTrace();
            return new SignedMessage(false, privKey);
        }
    }
    public SignedMessage login(String email, String password) throws RemoteException{
        if(passwordSalt.get(email) == null){
            System.out.println("Failed to log in " + email);
            return new SignedMessage(null, privKey);
        }
        String salt = passwordSalt.get(email);
        byte[] hashedPass = PasswordEncryptionHelper.encrypt(password, salt);
        UserAccount acc = accounts.get(Base64.getEncoder().encodeToString(hashedPass));
        if(acc.getEmail().equals(email)){
            return new SignedMessage(acc, privKey);
        } else {
            System.out.println("Failed to log in " + email);
            return new SignedMessage(null, privKey);
        }
    }

}
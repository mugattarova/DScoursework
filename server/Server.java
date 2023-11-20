import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Base64;
import java.security.*;

public class Server implements IAuction{
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
    }

    public static void main(String[] args) {
        try {
            Server s = new Server();
            String name = "myserver";
            IAuction stub = (IAuction) UnicastRemoteObject.exportObject(s, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);

            System.out.println("Server ready");

        } catch (Exception e) {
            System.err.println("Exception:");
            e.printStackTrace();
        }
    }
    
    public AuctionItem getSpec(int itemId, int clientId) throws RemoteException {
        try {
            return auctionItems.get(itemId);
        } catch (Exception e) {
            return null;
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
        System.out.println("New item stored, id " + aui.getItemId());
    }
    public void storeAuctionItem(String _itemTitle, String _itemDescription) throws RemoteException{
        AuctionItem newItem = new AuctionItem(_itemTitle, _itemDescription);
        auctionItems.put(newItem.getItemId(), newItem);
        System.out.println("New item stored, id " + newItem.getItemId());
    }
    public SignedMessage createAuction(AuctionItem aui, float startingPrice, float reservePrice) throws RemoteException {
        Auction newAu = new Auction(aui, startingPrice, reservePrice);
        openAuctions.put(newAu.getAuctionID(), newAu);
        System.out.println("New auction created, id " + newAu.getAuctionID());
        return new SignedMessage(newAu.getAuctionID(), privKey);
    }
    public SignedMessage closeAuction(int auctionID) throws RemoteException {
        try {
            Auction au = openAuctions.remove(auctionID);
            System.out.println("Auction closed, id " + au.getAuctionID());
            if(!au.hasBids()){
                return new SignedMessage(AuCloseOutcome.NoBids, privKey);
            } else if(!au.isReserveMet()) {
                return new SignedMessage(AuCloseOutcome.ReserveNotMet, privKey);
            } else {
                return new SignedMessage(AuCloseOutcome.Sold, privKey);
            }
        } catch (Exception e) {
            System.out.println("Cannot close auction, id " + auctionID);
            return new SignedMessage(AuCloseOutcome.DoesNotExist, privKey);
        }
    }
    public SignedMessage submitBid(int auctionID, Bid bid) throws RemoteException{
        try {
            Auction auction = openAuctions.get(auctionID);
            if(auction == null){
                return new SignedMessage("Auction not found. Bid was not successful.", privKey);
            } else {
                if(auction.placeBid(bid)){
                    System.out.println("Successful bid " + bid.getSum() + " at auction " + auction.getAuctionID());
                    return new SignedMessage("Bid " +bid.getSum()+ " was placed successfully.", privKey);
                } else {
                    System.out.println("Failed bid " + bid.getSum() + " at auction " + auction.getAuctionID());
                    return new SignedMessage("Bid " +bid.getSum()+ " was unsuccessful. Bid a higher amount.", privKey);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SignedMessage("", privKey);
    }
    public SignedMessage openAuctionsToString() throws RemoteException{
        String out = "";
        Enumeration<Auction> list = openAuctions.elements();
        while(list.hasMoreElements()){
            out += list.nextElement().infoToString();
            out += "\n";
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

    /*public boolean createAccount(UserAccount acc, PublicKey pub) throws RemoteException{
        if(accounts.get(email) == null){
            UserAccount acc = new UserAccount(0, name, email);
            accounts.put(email, acc);
            return true;
        } 
        return false;
    }*/

}
import java.rmi.RemoteException;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Hashtable;

import org.jgroups.JChannel;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;

public class BackendServer{
    private PrivateKey privKey;
    private Hashtable<Integer, AuctionItem> auctionItems;
    private Hashtable<Integer, Auction> openAuctions;
    // email - salt
    private Hashtable<String, String> passwordSalt;
    // hashed salted password - UserAccount
    private Hashtable<String, UserAccount> accounts;

    private JChannel channel;

    public BackendServer(){
        try{
            privKey = MessageEncryptionHelper.getPrivKey("./private_key.der");
            auctionItems = new Hashtable<Integer, AuctionItem>();
            openAuctions = new Hashtable<Integer, Auction>();
            passwordSalt = new Hashtable<String, String>();
            accounts = new Hashtable<String, UserAccount>();
            channel = new JChannel();
            channel.connect("AuctionChannel");
            RpcDispatcher disp = new RpcDispatcher(channel, this);
            BackendState state = disp.callRemoteMethod(channel.getView().getCoord(), "getState", new Object[]{}, new Class[]{}, new RequestOptions(ResponseMode.GET_ALL, 5000, false));
            setState(state);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        BackendServer bs = new BackendServer();
    }

    public SignedMessage<AuctionItem> getItem(int itemID) throws RemoteException{
        return new SignedMessage<AuctionItem>(auctionItems.get(itemID), privKey);
    }
    public SignedMessage<String> getAuctionInfo(int auctionID) throws RemoteException{
        return new SignedMessage<String>(openAuctions.get(auctionID).infoToString(), privKey);
    }
    public SignedMessage<Auction> getAuction(int auctionID) throws RemoteException{
        return new SignedMessage<Auction>(openAuctions.get(auctionID), privKey);
    }
    public synchronized void storeAuctionItem(AuctionItem aui) throws RemoteException{
        auctionItems.put(aui.getItemID(), aui);
    }
    public synchronized void storeAuctionItem(String _itemTitle, String _itemDescription) throws RemoteException{
        AuctionItem newItem = new AuctionItem(_itemTitle, _itemDescription);
        auctionItems.put(newItem.getItemID(), newItem);
    }
    public SignedMessage<Integer> createForwardAuction(UserAccount owner, AuctionItem aui, float startingPrice, float reservePrice) throws RemoteException {
        ForwardAuction newAu = new ForwardAuction(owner, aui, startingPrice, reservePrice);
        openAuctions.put(newAu.getAuctionID(), newAu);
        System.out.println("New forward auction created, id " + newAu.getAuctionID());
        return new SignedMessage<Integer>((Integer) newAu.getAuctionID(), privKey);
    }
    public SignedMessage<Integer> createReverseAuction(UserAccount owner, AuctionItem aui) throws RemoteException{
        ReverseAuction newAu = new ReverseAuction(owner, aui);
        openAuctions.put(newAu.getAuctionID(), newAu);
        System.out.println("New reverse auction created, id " + newAu.getAuctionID());
        return new SignedMessage<Integer>(newAu.getAuctionID(), privKey);
    }
    public SignedMessage<Integer> createDoubleAuction(UserAccount owner, AuctionItem aui) throws RemoteException{
        DoubleAuction newAu = new DoubleAuction(owner, aui);
        openAuctions.put(newAu.getAuctionID(), newAu);
        System.out.println("New double auction created, id " + newAu.getAuctionID());
        return new SignedMessage<Integer>(newAu.getAuctionID(), privKey);
    }
    public SignedMessage<String> closeAuction(UserAccount acc, int auctionID) throws RemoteException {
        try {
            Auction au = openAuctions.get(auctionID);
            if(au.verifyUser(acc)){
                openAuctions.remove(auctionID);
                System.out.println("Auction closed, id " + au.getAuctionID());
            }
            String msg = au.closeAuction(acc);
            return new SignedMessage<String>(msg, privKey);
        } catch (Exception e) {
            System.out.println("Cannot close auction, id " + auctionID);
            return new SignedMessage<String>("Cannot close auction, id " + auctionID, privKey);
        } 
    }
    public SignedMessage<String> submitForwAucBid(UserAccount acc, int auctionID, Bid bid) throws RemoteException{
        try{
            ForwardAuction forwAuction = (ForwardAuction) openAuctions.get(auctionID);
            if(forwAuction == null){
                return new SignedMessage<String>("Auction not found. Bid was not successful.", privKey);
            } else {
                if(forwAuction.placeBid(acc, bid)){
                    System.out.println("Successful bid " + bid.getSum() + " at auction " + forwAuction.getAuctionID());
                    return new SignedMessage<String>("Bid " + bid.getSum() + " was placed successfully.", privKey);
                } else {
                    System.out.println("Failed bid " + bid.getSum() + " at auction " + forwAuction.getAuctionID());
                    return new SignedMessage<String>("Bid " + bid.getSum() + " was unsuccessful. Bid a higher amount.", privKey);
                }
            }
        } catch (Exception e) {
            return new SignedMessage<String>("Bid was unsuccessful.", privKey);
        }
    }
    public SignedMessage<String> submitRevAucBid(UserAccount acc, int auctionID, Bid bid) throws RemoteException{
        try{
            ReverseAuction revAuction = (ReverseAuction) openAuctions.get(auctionID);
            if(revAuction == null){
                return new SignedMessage<String>("Auction not found. Bid was not successful.", privKey);
            } else {
                if(revAuction.placeBid(acc, bid)){
                    System.out.println("Successful bid " + bid.getSum() + " at auction " + revAuction.getAuctionID());
                    return new SignedMessage<String>("Bid " + bid.getSum() + " was placed successfully.", privKey);
                } else {
                    System.out.println("Failed bid " + bid.getSum() + " at auction " + revAuction.getAuctionID());
                    return new SignedMessage<String>("Bid " + bid.getSum() + " was unsuccessful", privKey);
                }
            }
        } catch (Exception e) {
            return new SignedMessage<String>("Bid was unsuccessful.", privKey);
        }
    }
    public SignedMessage<String> submitDoubleAucSell(UserAccount acc, int auctionID, Bid bid) throws RemoteException{
        try {
            DoubleAuction doubAuction = (DoubleAuction) openAuctions.get(auctionID);
            if(doubAuction == null){
                return new SignedMessage<String>("Auction not found. Bid was not successful.", privKey);
            } else {
                if(doubAuction.placeSell(bid)){
                    System.out.println("Successful sell request " + bid.getSum() + " at auction " + doubAuction.getAuctionID());
                    return new SignedMessage<String>("Sell request " + bid.getSum() + " was placed successfully.", privKey);
                } else {
                    System.out.println("Failed sell request " + bid.getSum() + " at auction " + doubAuction.getAuctionID());
                    return new SignedMessage<String>("Sell request " + bid.getSum() + " was unsuccessful", privKey);
                }
            }
            
        } catch (Exception e) {
            return new SignedMessage<String>("Sell request was uncsuccessful", privKey);
        }
    }
    public SignedMessage<String> submitDoubleAucBuy(UserAccount acc, int auctionID, Bid bid) throws RemoteException{
        try {
            DoubleAuction doubAuction = (DoubleAuction) openAuctions.get(auctionID);
            if(doubAuction == null){
                return new SignedMessage<String>("Auction not found. Bid was not successful.", privKey);
            } else {
                if(doubAuction.placeBuy(bid)){
                    System.out.println("Successful buy request " + bid.getSum() + " at auction " + doubAuction.getAuctionID());
                    return new SignedMessage<String>("Buy request " + bid.getSum() + " was placed successfully.", privKey);
                } else {
                    System.out.println("Failed buy request " + bid.getSum() + " at auction " + doubAuction.getAuctionID());
                    return new SignedMessage<String>("Buy request " + bid.getSum() + " was unsuccessful", privKey);
                }
            }
            
        } catch (Exception e) {
            return new SignedMessage<String>("Buy request was uncsuccessful", privKey);
        }
    }
    public SignedMessage<String> openAuctionsToString(String auctionType) throws RemoteException{
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
        return new SignedMessage<String>(out, privKey);
    }
    public SignedMessage<String> availableItemsToString() throws RemoteException{
        String out = "";
        Enumeration<AuctionItem> list = auctionItems.elements();
        
        while(list.hasMoreElements()){
            out += list.nextElement().infoToString();
            out += "\n";            
        }
        return new SignedMessage<String>(out, privKey);
    }

    public SignedMessage<Boolean> register(String name, String email, String password, String generatedSalt) throws RemoteException{
        try{
        if(passwordSalt.get(email) != null){
            System.out.println("Failed to register " + email);
            return new SignedMessage<Boolean>(false, privKey);
        }
        passwordSalt.put(email, generatedSalt);
        byte[] hashedPass = PasswordEncryptionHelper.encrypt(password, generatedSalt);
        UserAccount acc = new UserAccount(name, email);
        accounts.put(Base64.getEncoder().encodeToString(hashedPass), acc);
        System.out.println("Registered new account " + email);
        return new SignedMessage<Boolean>(true, privKey);

        } catch (Exception e){
            e.printStackTrace();
            return new SignedMessage<Boolean>(false, privKey);
        }
    }
    public SignedMessage<UserAccount> login(String email, String password) throws RemoteException{
        if(passwordSalt.get(email) == null){
            System.out.println("Failed to log in " + email);
            return new SignedMessage<UserAccount>(null, privKey);
        }
        String salt = passwordSalt.get(email);
        byte[] hashedPass = PasswordEncryptionHelper.encrypt(password, salt);
        UserAccount acc = accounts.get(Base64.getEncoder().encodeToString(hashedPass));
        if(acc.getEmail().equals(email)){
            return new SignedMessage<UserAccount>(acc, privKey);
        } else {
            System.out.println("Failed to log in " + email);
            return new SignedMessage<UserAccount>(null, privKey);
        }
    }

    public BackendState getState(){
        BackendState state = new BackendState(auctionItems, openAuctions, passwordSalt, accounts);
        return state;
    }
    public void setState(BackendState state){
        this.auctionItems = state.getAuctionItems();
        this.openAuctions = state.getOpenAuctions();
        this.passwordSalt = state.getPasswordSalt();
        this.accounts = state.getAccounts();
    }
}

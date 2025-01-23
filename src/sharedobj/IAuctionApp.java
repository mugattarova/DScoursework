import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IAuctionApp extends Remote{

    public SignedMessage<AuctionItem> getItem(int itemID) throws RemoteException;
    public SignedMessage<String> getAuctionInfo(int auctionID) throws RemoteException;
    public SignedMessage<Auction> getAuction(int auctionID) throws RemoteException;
    public void storeAuctionItem(AuctionItem aui) throws RemoteException;
    public void storeAuctionItem(String _itemTitle, String _itemDescription) throws RemoteException;
    public SignedMessage<Integer> createForwardAuction(UserAccount owner, AuctionItem aui, float startingPrice, float reservePrice) throws RemoteException;
    public SignedMessage<Integer> createReverseAuction(UserAccount owner, AuctionItem aui) throws RemoteException;
    public SignedMessage<Integer> createDoubleAuction(UserAccount owner, AuctionItem aui) throws RemoteException;
    public SignedMessage<String> closeAuction(UserAccount acc, int auctionID) throws RemoteException;
    public SignedMessage<String> submitForwAucBid(UserAccount acc, int auctionID, Bid bid) throws RemoteException;
    public SignedMessage<String> submitRevAucBid(UserAccount acc, int auctionID, Bid bid) throws RemoteException;
    public SignedMessage<String> submitDoubleAucSell(UserAccount acc, int auctionID, Bid bid) throws RemoteException;
    public SignedMessage<String> submitDoubleAucBuy(UserAccount acc, int auctionID, Bid bid) throws RemoteException;
    public SignedMessage<String> openAuctionsToString(String auctionType) throws RemoteException;
    public SignedMessage<String> availableItemsToString() throws RemoteException;

    public SignedMessage<Boolean> register(String name, String email, String password) throws RemoteException;
    public SignedMessage<UserAccount> login(String email, String password) throws RemoteException;

}

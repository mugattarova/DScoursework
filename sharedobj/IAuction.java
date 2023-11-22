import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IAuction extends Remote{

    public SignedMessage getItemInfo(int itemID) throws RemoteException;
    public SignedMessage getAuctionInfo(int auctionID) throws RemoteException;
    public SignedMessage getAuction(int auctionID) throws RemoteException;
    public void storeAuctionItem(AuctionItem aui) throws RemoteException;
    public void storeAuctionItem(String _itemTitle, String _itemDescription) throws RemoteException;
    public SignedMessage createForwardAuction(UserAccount owner, AuctionItem aui, float startingPrice, float reservePrice) throws RemoteException;
    public SignedMessage createReverseAuction(UserAccount owner, AuctionItem aui) throws RemoteException;
    public SignedMessage createDoubleAuction(UserAccount owner, AuctionItem aui) throws RemoteException;
    public SignedMessage closeAuction(UserAccount acc, int auctionID) throws RemoteException;
    public SignedMessage submitForwAucBid(UserAccount acc, int auctionID, Bid bid) throws RemoteException;
    public SignedMessage submitRevAucBid(UserAccount acc, int auctionID, Bid bid) throws RemoteException;
    public SignedMessage submitDoubleAucSell(UserAccount acc, int auctionID, Bid bid) throws RemoteException;
    public SignedMessage submitDoubleAucBuy(UserAccount acc, int auctionID, Bid bid) throws RemoteException;
    public SignedMessage openAuctionsToString(String auctionType) throws RemoteException;
    public SignedMessage availableItemsToString() throws RemoteException;
    public SignedMessage register(String name, String email, String password) throws RemoteException;
    public SignedMessage login(String email, String password) throws RemoteException;

}

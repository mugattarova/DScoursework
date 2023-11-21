import java.io.Serializable;

public abstract class Auction implements Serializable{

    UserAccount owner;
    private static int auctionIDCount = 0;
    protected int auctionID;
    protected AuctionItem auctionItem;
    protected boolean isClosed;

    public Auction(UserAccount _owner, AuctionItem _auctionItem){
        owner = _owner;
        auctionID = ++auctionIDCount;
        auctionItem = _auctionItem;
        isClosed = false;
    }
    
    public String infoToString(){
        return null;
    }
    public String infoToStringWithTypeString(){
        return null;
    }
    public String closeAuction(UserAccount acc){
        return null;
    }

    public boolean verifyUser(UserAccount acc){
        if(acc.equals(owner)){
            return true;
        } else {
            return false;
        }
    }
    public int getAuctionID() {
        return auctionID;
    }
    public AuctionItem getItem(){
        return auctionItem;
    }

}

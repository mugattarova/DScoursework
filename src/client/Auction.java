import java.io.Serializable;

public abstract class Auction implements Serializable{
    private static int auctionIDCount = 0;

    protected UserAccount owner;
    protected int auctionID;
    protected AuctionItem auctionItem;

    public Auction(UserAccount _owner, AuctionItem _auctionItem){
        owner = _owner;
        auctionID = ++auctionIDCount;
        auctionItem = _auctionItem;
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

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Auction)){
            return false;
        }
        Auction a = (Auction) obj;
        if(this.owner.equals(a.owner) && this.auctionID==a.auctionID && this.auctionItem.equals(a.auctionItem)){
            return true;
        } else{
            return false;
        }

    }
}

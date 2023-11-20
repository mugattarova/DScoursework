import java.io.Serializable;
import java.util.Hashtable;

public class Auction implements Serializable{

    private static int auctionIDCount = 0;
    private int auctionID;
    private AuctionItem auctionItem;
    private float startingPrice;
    private float reservePrice;
    private boolean isClosed;

    private Bid highestBid;

    public Auction(AuctionItem _auctionItem, float _startingPrice, float _reservePrice){
        if(_auctionItem != null){
            auctionID = ++auctionIDCount;
            auctionItem = _auctionItem;
            startingPrice = _startingPrice;
            reservePrice = _reservePrice;
            highestBid = null;
            isClosed = false;
        }
    }

    public int getAuctionID() {
        return auctionID;
    }
    
    public float getStartingPrice(){
        return startingPrice;
    }

    public AuctionItem getItem(){
        return auctionItem;
    }

    public synchronized boolean placeBid(Bid newBid){
        // first bid
        if(highestBid == null){
            highestBid = newBid;
            return true;

        // higher bid
        } else if(newBid.getSum() > highestBid.getSum()){
            highestBid = newBid;
            return true;
        }
        return false;
    }

    public String infoToString(){
        String out = "";

        out += "Auction ID: " + auctionID + "\n";
        out += "Starting Price: " + startingPrice + "\n";
        out += "Highest Bid: ";
        if(highestBid == null){
            out += "none";
        } else {   
            out += highestBid.getSum();
        }
        out += "\n";
        out += auctionItem.infoToString();

        return out;
    }

    public boolean hasBids(){
        if(highestBid != null){
            return true;
        }
        return false;
    }

    public boolean isReserveMet(){
        if(hasBids() == true){
            if( highestBid.getSum() >= reservePrice ){
                return true;
            }
        }
        return false;
    }

    public void closeAuction(){
        isClosed = true;
    }

    public Bid getWinnerBid(){
        if(isReserveMet() && isClosed){
            return highestBid;
        }
        return null;
    }
}

public class ForwardAuction extends Auction{
    
    private float startingPrice;
    private float reservePrice;
    private Bid highestBid;

    public ForwardAuction(UserAccount _account, AuctionItem _item, float _startingPrice, float _reservePrice){
        super(_account, _item);
        startingPrice = _startingPrice;
        reservePrice = _reservePrice;
        highestBid = null;
    }

    public float getStartingPrice(){
        return startingPrice;
    }
    public synchronized boolean placeBid(UserAccount acc, Bid newBid) throws InvalidBidException{
        if( !verifyUser(acc) ){
            // first bid
            if(highestBid == null){
                highestBid = newBid;
                return true;
    
            // higher bid
            } else if(newBid.getSum() > highestBid.getSum()){
                highestBid = newBid;
                return true;

            } else{
                throw new InvalidBidException("The bid is too low");
            }
        } else {
            throw new InvalidBidException("You cannot place a bid on your auction");
        }
    }
    @Override
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
    @Override
    public String infoToStringWithTypeString(){
        String out = "";
        out += "--- Forward Auction ---\n";
        out += infoToString();
        return out;
    }
    public boolean hasBids(){
        if(highestBid != null){
            return true;
        }
        return false;
    }
    private boolean isReserveMet(){
        if(hasBids() == true){
            if( highestBid.getSum() >= reservePrice ){
                return true;
            }
        }
        return false;
    }
    public Bid getWinnerBid(){
        if(isReserveMet()){
            return highestBid;
        }
        return null;
    }
    @Override
    public String closeAuction(UserAccount acc){
        if(verifyUser(acc)){
            if(!hasBids()){
                return "No bids were placed. The item was not sold.";
            } else if(!isReserveMet()){
                return "Reserve price was not met. The item was not sold.";
            } else {
                return "Auction item sold!\n" + "--- The winner ---\n" + highestBid.infoToString();
            }
        } else {
            return "You cannot close this auction";
        }
    }
}

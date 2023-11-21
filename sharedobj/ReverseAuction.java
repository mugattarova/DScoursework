import java.util.*;

public class ReverseAuction extends Auction{
    LinkedList<Bid> bids;
    Bid chosenBid;

    public ReverseAuction(UserAccount _owner, AuctionItem _auctionItem){
        super(_owner, _auctionItem);
        bids = new LinkedList<Bid>();
        chosenBid = null;
    }

    public boolean placeBid(UserAccount acc, Bid bid) throws InvalidBidException{
        if(!verifyUser(acc)){
            bids.add(bid);
            return true;
        } else {
            throw new InvalidBidException("You cannot bid on your auction");
        }
    }
    public boolean hasBids(){
        if(bids.isEmpty()){
            return false;
        } else {
            return true;
        }
    }
    public LinkedList<Bid> getBids() {
        return bids;
    }
    public boolean chooseBid(Bid bid){
        if(bids.contains(bid)){
            chosenBid = bid;
            return true;
        } else {
            return false;
        }
    }
    public Bid getMinBid(){
        int minInd = -1;
        float min = -1;
        float curVal;

        for(int i=0; i < bids.size(); i++){
            curVal = bids.get(i).getSum();
            if(min < 0){
                min = curVal;
                minInd = i;
            }
            if(curVal < min){
                min = curVal;
                minInd = i;
            }
        }
        return bids.get(minInd);
    }
    @Override
    public String closeAuction(UserAccount acc){
        if(verifyUser(acc)){
            isClosed = true;
            if(chosenBid != null){
                return "Auction closed successfully!\n--- The Winner ---\n" + chosenBid.infoToString();
            } else {
                if(hasBids()){
                    chosenBid = getMinBid();
                }
                if(chosenBid != null){
                    return "Auction closed successfully!\n--- The Winner ---\n" + chosenBid.infoToString();
                }
            }
            return "Auction closed. No bids were placed";
        }
        return "You cannot close this auction";
    }
    @Override
    public String infoToString(){
        String out = "";

        out += "Auction ID: " + auctionID + "\n";
        out += "Lowest bid: ";
        if(hasBids()){
            out += getMinBid().getSum();
        } else {
            out += "none";
        }
        out += "\n";
        out += auctionItem.infoToString();

        return out;
    }
    @Override
    public String infoToStringWithTypeString(){
        String out = "";
        out += "--- Reverse Auction ---\n";
        out += infoToString();
        return out;
    }
}

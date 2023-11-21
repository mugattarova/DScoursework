import java.util.*;
import java.util.Collections;
import java.lang.Math;

public class DoubleAuction extends Auction{
    LinkedList<Bid> sellers;
    LinkedList<Bid> buyers;

    public DoubleAuction(UserAccount _owner, AuctionItem _auctionItem){
        super(_owner, _auctionItem);
        sellers = new LinkedList<Bid>();
        buyers = new LinkedList<Bid>();
    }

    @Override
    public String infoToString(){
        String out = "";

        out += "Auction ID: " + auctionID + "\n";
        out += "Currently selling: " + sellers.size() + "\n";
        out += "Currently buying: " + buyers.size() + "\n";
        out += auctionItem.infoToString();
        return out;
    }
    @Override
    public String infoToStringWithTypeString(){
        String out = "";

        out += "--- Double Auction ---\n";
        out += infoToString();

        return out;
    }
    @Override
    public String closeAuction(UserAccount acc){
        isClosed = true;
        String out = "";
        if(sellers.size() == 0 || buyers.size() == 0){
            return "Auction closed successfully. No matches were made.";
        }

        Collections.sort(sellers, new SortBySum());
        Collections.reverse(sellers);
        Collections.sort(buyers, new SortBySum());
        out += "-Item on sale-\n";
        out += auctionItem.infoToString();
        for(int i=0; i < Math.min(sellers.size(), buyers.size()); i++){
            out += bidPairToString(sellers.get(i), buyers.get(i));
        }
        
        return out;

    }

    public boolean placeSell(Bid bid){
        sellers.add(bid);
        return true;
    }
    public boolean placeBuy(Bid bid){
        buyers.add(bid);
        return true;
    }
    public String bidPairToString(Bid seller, Bid buyer){
        String out = "";

        out += "-- Match made --\n";
        out += "Seller: \n";
        out += seller.infoToString();
        out += "\nBuyer: \n";
        out += buyer.infoToString();
        out += "\nProfit made: " + String.format("%.2f", Math.abs(buyer.getSum() - seller.getSum()));
        out += "\n";

        return out;
    }

    private class SortBySum implements Comparator<Bid> {
        public int compare(Bid a, Bid b){  
            return (int) (a.getSum() - b.getSum());  
        }  
    }  
}

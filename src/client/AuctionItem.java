
import java.io.Serializable;

public class AuctionItem implements Serializable{

    private static int itemIDCount = 0;
    private int itemID;
    private String itemTitle;
    private String itemDescription;

    public AuctionItem(String _itemTitle, String _itemDescription){
        itemID = ++itemIDCount;
        itemTitle = _itemTitle;
        itemDescription = _itemDescription;
    }

    public int getItemID(){
        return itemID;
    }

    public String getItemTitle(){
        return itemTitle;
    }

    public String getItemDescription(){
        return itemDescription;
    }

    public String infoToString(){
        String out = "";

        out += "Item ID: " + itemID + "\n";
        out += "Title: " + itemTitle + "\n";
        out += "Description: " + itemDescription + "\n";

        return out;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof AuctionItem)){
            return false;
        }
        AuctionItem ai = (AuctionItem) obj;
        if (( this.itemID == ai.itemID ) && ( this.itemTitle.equals(ai.itemTitle) ) && ( this.itemDescription.equals(ai.itemDescription) )){
            return true;
        } else{
            return false;
        }
    }
}

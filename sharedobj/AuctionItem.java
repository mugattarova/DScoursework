import java.io.Serializable;

public class AuctionItem implements Serializable{

    public enum ItemCondition {
        NEW,
        USED
    }

    private static int itemIDCount = 0;
    private int itemId;
    private String itemTitle;
    private String itemDescription;
    //private ItemCondition itemCond;

    public AuctionItem(String _itemTitle, String _itemDescription){
        itemId = ++itemIDCount;
        itemTitle = _itemTitle;
        itemDescription = _itemDescription;
        //itemCond = _itemCond;
    }

    public int getItemId(){
        return itemId;
    }

    public String getItemTitle(){
        return itemTitle;
    }

    public String getItemDescription(){
        return itemDescription;
    }

    public String infoToString(){
        String out = "";

        out += "Item ID: " + itemId + "\n";
        out += "Title: " + itemTitle + "\n";
        out += "Description: " + itemDescription + "\n";
        //out += "Condition: " + itemCond.toString() + "\n";

        return out;
    }
}

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.security.*;

public class MultiClient{
    private static UserAccount acc;
    private static IAuctionApp server;
    private static InputHandler in;

    public MultiClient(){
        acc = null;
        in = InputHandler.getInstance();
    }
    public static void main(String[] args) {

    new MultiClient();

    try {
    String nameServer = "myserver";
    Registry registry = LocateRegistry.getRegistry("localhost");
    server = (IAuctionApp) registry.lookup(nameServer);

    int externalMenuChoice = 0;
    int internalMenuChoice;
    LinkedList<String> options = new LinkedList<String>();

    in.clearConsole();
    do{ // Login menu
        options.clear();
        options.add("Log in");
        options.add("Register");
        externalMenuChoice = printCustomMenuWithQuit("LITTLE AUCTION APP LOGIN", options);
        switch (externalMenuChoice) {
            case 1: // Login
                acc = login();
                if(acc == null){
                    System.out.println("Failed to log in. Please try again");
                } else {
                    System.out.println("Login successfull. Welcome, " + acc.getName());
                }
                in.confirmUserProceed();
                break;
            case 2: // Register
                if(register()){
                    System.out.println("Account registered successfully. Please log in to enter the system.");
                } else {
                    System.out.println("Cannot register account. Please try again.");
                }
                in.confirmUserProceed();
                break;
            default:
                break;
        }
    } while(acc == null);

    String auctionType;
    do{ // Main Menu
        
        options.clear();
        options.add("All Available Items");
        options.add("All Open Auctions");
        options.add("Forward Auction");
        options.add("Reverse Auction");
        options.add("Double Auction");
        externalMenuChoice = printCustomMenuWithQuit("little auction app", options);
        switch (externalMenuChoice) {
            
            case 1: // Available Items
                System.out.println(customTitleToString("Available Items"));
                System.out.print((String) getVerifiedMessage(server.availableItemsToString()));
                in.confirmUserProceed();
                break;
                
            case 2: // All Open Auctions
                System.out.println(customTitleToString("All Open Auctions"));
                System.out.print((String) getVerifiedMessage(server.openAuctionsToString("all")));
                in.confirmUserProceed();
                break;

            case 3: // Forward Auction 
                auctionType = "f";
                options.clear();
                options.add("List of Open Forward Auctions");
                options.add("Place a Bid");
                options.add("Open an Auction");
                options.add("Close an Auction");
                internalMenuChoice = printCustomMenu("Forward Auction", options);
                switch (internalMenuChoice) {
                    case 1: // List open forward auctions
                        System.out.println(customTitleToString("Open Forward Auctions"));
                        System.out.print((String) getVerifiedMessage(server.openAuctionsToString(auctionType)));
                        in.confirmUserProceed();
                        break;
                
                    case 2: // Place a Bid
                        placeBid(auctionType);
                        break;
                        
                    case 3: // Open an Auction
                        options.clear();
                        options.add("For a new item");
                        options.add("For an existing item");
                        internalMenuChoice = printCustomMenu("Open an Auction", options);
                        switch (internalMenuChoice) {
                            case 1: // For a new item
                                openForwAucNewItem();
                                break;
                            case 2: // For an existing item
                                openForwAucExistItem();
                                break;
                            case 0:
                                continue;
                            default:
                                break;
                        }
                        break;
                        
                    case 4: // Close an Auction
                        closeAuction(auctionType);
                        break;
                                                            
                    case 0: // Back
                        continue;                                   
                    default:
                        break;
                }
                break;
            case 4: // Reverse Auction
                auctionType = "r";

                options.clear();
                options.add("List of Open Reverse Auctions");
                options.add("Place a Bid");
                options.add("Open an Auction");
                options.add("Close an Auction");
                internalMenuChoice = printCustomMenu("Reverse Auction", options);
                switch (internalMenuChoice) {
                    case 1: // List Reverse Auctions
                        System.out.println(customTitleToString("Open Reverse Auctions"));
                        server.openAuctionsToString(auctionType);
                        System.out.print((String) getVerifiedMessage(server.openAuctionsToString(auctionType)));
                        in.confirmUserProceed();
                        break;
                    case 2: // Place a bid
                        placeBid(auctionType);
                        break;
                    case 3: // Open an Auction
                        options.clear();
                        options.add("For a new item");
                        options.add("For an existing item");
                        internalMenuChoice = printCustomMenu("Open an Auction", options);
                        switch (internalMenuChoice) {
                            case 1: // For a new item
                                openRevAucNewItem();
                                break;
                            case 2: // For an existing item
                                openRevAucExistItem();
                                break;
                            case 0:
                                continue;
                            default:
                                break;
                        }
                        break;
                    case 4: // Close an Auction
                        closeAuction(auctionType);
                        break;
                    default:
                        break;
                }
                break;
            
            case 5: // Double Auction
                auctionType = "d";
                options.clear();
                options.add("List of Open Double Auctions");
                options.add("Place a Buy Request");
                options.add("Place a Sell Request");
                options.add("Open an Auction");
                options.add("Close an Auction");
                internalMenuChoice = printCustomMenu("Double Auction", options);
                switch (internalMenuChoice) {
                    case 1: // list
                        System.out.println(customTitleToString("All Open Auctions"));
                        System.out.print((String) getVerifiedMessage(server.openAuctionsToString(auctionType)));
                        in.confirmUserProceed();
                        break;
                    case 2: // place a buy
                        placeBuyReq();
                        break;  
                    case 3: // place a sell
                        placeSellReq();
                        break;   
                    case 4: //open auction
                        options.clear();
                        options.add("For a new item");
                        options.add("For an existing item");
                        internalMenuChoice = printCustomMenu("Open an Auction", options);
                        switch (internalMenuChoice) {
                            case 1: // For a new item
                                openDoubleAucNewItem();
                                in.confirmUserProceed();
                                break;
                            case 2: // For an existing item
                                openDoubleAucExistItem();
                                in.confirmUserProceed();
                                break;
                            case 0:
                                continue;
                            default:
                                break;
                        }                     
                        break; 
                    case 5: // close auction
                        closeAuction(auctionType);
                        break;                        
                    default:
                        break;
                }
                break;
            default:
                System.out.println("Invalid option");
                break;
        }
    } while(externalMenuChoice != 0);
    System.exit(0);

    } catch (Exception e) {
        System.err.println("Exception:");
        e.printStackTrace();
    }
    }

    public static boolean verify(SignedMessage msg){
        try {
            if(MessageEncryptionHelper.verify(MessageEncryptionHelper.getPubKey("D:/Personal/LU_Leipzig_University/3Y/311DS/coursework1/client/public_key.der"), msg)){
                return true;
            }
        } catch (Exception e) {e.printStackTrace();}
        return false;
    }
    public static Object getVerifiedMessage(SignedMessage msg) throws SignatureException{
        if(verify(msg)){
            return msg.getMessage();
        } else {
            throw new SignatureException("Received message failed to verify");
        }
    }
    public static String customTitleToString(String title){
        String out = "---------------" + title + "---------------";
        out += "\n";
        return out;
    }
    public static int printCustomMenu(String title, LinkedList<String> options){
        System.out.println(customTitleToString(title));
        int i = 1;
        for (String string : options) {
            System.out.println(i + ". " + string);
            i++;
        }
        System.out.println("0. Main menu");
        System.out.println();

        int out = 0;
        do{
            out = in.nextInt();
            if(out < 0 || out > i){
                System.out.println("Invalid input. Enter one of the menu option numbers");
            }
        } while(out < 0 || out > i);
        in.clearConsole();
        return out;
    }
    public static int printCustomMenuWithQuit(String title, LinkedList<String> options){
        System.out.println(customTitleToString(title));
        int i = 1;
        for (String string : options) {
            System.out.println(i + ". " + string);
            i++;
        }
        System.out.println("0. Quit");
        System.out.println();

        int out = 0;
        do{
            out = in.nextInt();
            if(out < 0 || out > i){
                System.out.println("Invalid input. Enter one of the menu option numbers");
            }
        } while(out < 0 || out > i);
        in.clearConsole();
        if(out == 0){
            System.out.println("Quitting...");
            in.close();
            System.exit(0);
        }
        return out;
    }
    public static void openForwAucNewItem(){
        AuctionItem newItem;
        String newItemTitle;
        String newItemDescription;
        float startingPrice;
        float reservePrice;
        
        try {
            System.out.println(customTitleToString("Open Forward Auction for a New Item"));
                                
            System.out.println("New item's title");
            newItemTitle = in.nextLine();
            System.out.println("New item's description");
            newItemDescription = in.nextLine();
            System.out.println("Starting price");
            startingPrice = in.nextPositiveFloat();
            System.out.println("Reserve price");
            reservePrice = in.nextPositiveFloat();

            newItem = new AuctionItem(newItemTitle, newItemDescription);
            server.storeAuctionItem(newItem);
            server.createForwardAuction(acc, newItem, startingPrice, reservePrice);

            in.clearConsole();
            System.out.println(customTitleToString("New Item Added"));
            System.out.print(newItem.infoToString());
            System.out.println("Starting price: " + startingPrice);

            in.confirmUserProceed();
            
        } catch (Exception e) {
            System.err.println("Exception:");
            e.printStackTrace();
        }
    } 
    public static void openForwAucExistItem(){
        AuctionItem item;
        int auctionID;
        int itemID;
        float startingPrice;
        float reservePrice;
        boolean stayInMenu = true;
        
        do {
            try {
                System.out.println(customTitleToString("Open Auction for an Existing Item"));
                System.out.print((String)getVerifiedMessage(server.availableItemsToString()));
                System.out.println("Item ID");
                itemID = in.nextInt();
                item = (AuctionItem) getVerifiedMessage(server.getItemInfo(itemID));

                // if id does not exist
                if(item == null){
                    System.out.println("Item ID does not exist");
                    continue;
                }

                // valid input handling                
                System.out.println("Starting price");
                startingPrice = in.nextPositiveFloat();
                System.out.println("Reserve price");
                reservePrice = in.nextPositiveFloat();

                auctionID = (int) getVerifiedMessage(server.createForwardAuction(acc, item, startingPrice, reservePrice));
                in.clearConsole();
                System.out.println(customTitleToString("New Auction Opened"));
                System.out.print((String) getVerifiedMessage(server.getAuctionInfo(auctionID)));
                in.confirmUserProceed();
                stayInMenu = false;

            } catch(SignatureException e){
                System.out.println("Signature Exception. Message received failed to verify.");
            } catch (Exception e) {
                System.err.println("Exception:");
                e.printStackTrace();
            }
        } while(stayInMenu);
    } 
    public static void openRevAucNewItem(){
        AuctionItem newItem;
        String newItemTitle;
        String newItemDescription;
        int auctionID;

        try {
            System.out.println(customTitleToString("Open Reverse Auction for a New Item"));
            
            System.out.println("New item's title");
            newItemTitle = in.nextLine();
            System.out.println("New item's description");
            newItemDescription = in.nextLine();
            
            newItem = new AuctionItem(newItemTitle, newItemDescription);
            server.storeAuctionItem(newItem);

            auctionID = (Integer) getVerifiedMessage(server.createReverseAuction(acc, newItem));
            System.out.println("New Auction");
            System.out.print((String)getVerifiedMessage(server.getAuctionInfo(auctionID)));
            in.clearConsole();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void openRevAucExistItem(){
        AuctionItem item;
        int itemID;
        int auctionID;
        boolean stayInMenu = true;

        do{
            try {
                System.out.println(customTitleToString("Open Reverse Auction for a New Item"));
                System.out.print((String)getVerifiedMessage(server.availableItemsToString()));
                System.out.println("Item ID");

                itemID = in.nextInt(); 
                item = (AuctionItem) getVerifiedMessage(server.getItemInfo(itemID));
                if(item == null){
                    System.out.println("Item ID does not exist");
                    continue;
                }
                in.clearConsole();
                auctionID = (Integer) getVerifiedMessage(server.createReverseAuction(acc, item));
                System.out.println("New Auction");
                System.out.print((String)getVerifiedMessage(server.getAuctionInfo(auctionID)));

                in.confirmUserProceed();
                stayInMenu = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while(stayInMenu);
    }
    public static void openDoubleAucNewItem(){
        AuctionItem newItem;
        String newItemTitle;
        String newItemDescription;
        int auctionID;

        try {
            System.out.println(customTitleToString("Open Double Auction for a New Item"));
            System.out.println("New item's title");
            newItemTitle = in.nextLine();
            System.out.println("New item's description");
            newItemDescription = in.nextLine();

            newItem = new AuctionItem(newItemTitle, newItemDescription);
            server.storeAuctionItem(newItem);
            auctionID = (Integer) getVerifiedMessage(server.createDoubleAuction(acc, newItem));
            System.out.println("New Auction");
            System.out.println((String) getVerifiedMessage(server.getAuctionInfo(auctionID)));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void openDoubleAucExistItem(){
        AuctionItem item;
        int itemID;
        int auctionID;
        boolean stayInMenu = true;

        do{
            try {
                System.out.println(customTitleToString("Open Double Auction for a New Item"));
                System.out.print((String) getVerifiedMessage(server.availableItemsToString()));
                System.out.println("Item ID");
                itemID = in.nextInt();
                item = (AuctionItem) getVerifiedMessage(server.getItemInfo(itemID));
                // if id does not exist
                if(item == null){
                    System.out.println("Item ID does not exist");
                    continue;
                }

                in.clearConsole();
                auctionID = (Integer) getVerifiedMessage(server.createDoubleAuction(acc, item));
                System.out.println("New Auction");
                System.out.println((String) getVerifiedMessage(server.getAuctionInfo(auctionID)));

                in.confirmUserProceed();
                stayInMenu = false;
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while(stayInMenu);

    }
    public static void closeAuction(String auctionType) throws SignatureException{
        int auctionID;
        Auction closedAuction;

        try {
            System.out.println(customTitleToString("Close an Auction"));
            System.out.print((String) getVerifiedMessage(server.openAuctionsToString(auctionType)));
            auctionID = in.nextInt();

            closedAuction = (Auction) getVerifiedMessage(server.getAuction(auctionID));
            System.out.print((String) getVerifiedMessage(server.closeAuction(acc, closedAuction.getAuctionID())));
            in.confirmUserProceed();

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public static void placeBid(String aucType) throws SignatureException{
        int auctionID;
        float bidPrice = -1;

        try {
            System.out.println(customTitleToString("Place a Bid"));
            System.out.println((String) getVerifiedMessage(server.openAuctionsToString(aucType)));
            System.out.println("Select an auction (input the ID)");
            auctionID = in.nextInt();
            System.out.println("Enter the bid amount");
            bidPrice = in.nextPositiveFloat();

            switch (aucType) {
                case "f":
                    System.out.print((String) getVerifiedMessage(server.submitForwAucBid(acc, auctionID, new Bid(bidPrice, acc.getName(), acc.getEmail()))));
                    break;
                case "r":
                    System.out.println((String) getVerifiedMessage(server.submitRevAucBid(acc, auctionID, new Bid(bidPrice, acc.getName(), acc.getEmail()))));
                    break;
                default:
                    break;
            }
            in.confirmUserProceed();

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public static void placeSellReq(){
        int auctionID;
        String aucType = "d";
        float amount;

        try {
            System.out.println(customTitleToString("Place a Sell Request"));
            System.out.println((String) getVerifiedMessage(server.openAuctionsToString(aucType)));
            System.out.println("Select an auction (input the ID)");
            auctionID = in.nextInt();
            System.out.println("Enter the bid amount");
            amount = in.nextPositiveFloat();
            System.out.println((String) getVerifiedMessage(server.submitDoubleAucSell(acc, auctionID, new Bid(amount, acc.getName(), acc.getEmail()))));
            in.confirmUserProceed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void placeBuyReq(){
        int auctionID;
        String aucType = "d";
        float amount;

        try {
            System.out.println(customTitleToString("Place a Buy Request"));
            System.out.println((String) getVerifiedMessage(server.openAuctionsToString(aucType)));
            System.out.println("Select an auction (input the ID)");
            auctionID = in.nextInt();
            System.out.println("Enter the bid amount");
            amount = in.nextPositiveFloat();
            System.out.println((String) getVerifiedMessage(server.submitDoubleAucBuy(acc, auctionID, new Bid(amount, acc.getName(), acc.getEmail()))));
            in.confirmUserProceed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static UserAccount login(){
        UserAccount acc;
        try{
            System.out.println(customTitleToString("Log in"));
            System.out.println("Email");
            String email = in.nextLine();
            System.out.println("Password");
            String pass = in.nextLine();
            acc = (UserAccount) getVerifiedMessage(server.login(email, pass));
            return acc;
        } catch(Exception e){
            e.printStackTrace(); 
            return null;
        }
    }
    public static boolean register(){
        System.out.println(customTitleToString("Register"));
        String name = "";
        String email = "";
        String password = "";
        try {
            System.out.println("Name");
            name = in.nextNonEmptyLine();
            System.out.println("Email");
            email = in.nextNonEmptyLine();
            System.out.println("Password");
            password = in.nextNonEmptyLine();
            return (boolean) getVerifiedMessage(server.register(name, email, password));

        } catch (Exception e) {
            e.printStackTrace(); 
            return false;
        }
    }
}
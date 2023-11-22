import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.security.*;

public class MultiClient extends Client{
    public static void main(String[] args) {
        
    //PublicKey pubKey;
    SignedMessage msg;
    UserAccount acc = null;

    try {
    String nameServer = "myserver";
    Registry registry = LocateRegistry.getRegistry("localhost");
    IAuction server = (IAuction) registry.lookup(nameServer);

    //pubKey = MessageEncryptionHelper.getPubKey("D:/Personal/LU_Leipzig_University/3Y/311DS/coursework1/client/public_key.der");
    
    Scanner in = new Scanner(System.in);
    int externalMenuChoice = 0;
    int internalMenuChoice;
    LinkedList<String> options = new LinkedList<String>();

    clearConsole();

    do{ // Login menu
        options.clear();
        options.add("Log in");
        options.add("Register");
        externalMenuChoice = printCustomMenuWithQuit(in, "LITTLE AUCTION APP LOGIN", options);
        switch (externalMenuChoice) {
            case 1: // Login
                acc = login(server, in);
                if(acc == null){
                    System.out.println("Failed to log in. Please try again");
                } else {
                    System.out.println("Login successfull. Welcome, " + acc.getName());
                }
                confirmUserProceed(in);
                break;
            case 2: // Register
                if(register(server, in)){
                    System.out.println("Account registered successfully. Please log in to enter the system.");
                } else {
                    System.out.println("Cannot register account. Please try again.");
                }
                confirmUserProceed(in);
                break;
            default:
                break;
        }
    } while(acc == null);

    // server.createForwardAuction(acc, ai1, 400, 500); 
    // server.createReverseAuction(acc, ai2); 
    // server.createDoubleAuction(acc, ai3);

    String auctionType;
    do{ // Main Menu
        
        options.clear();
        options.add("All Available Items");
        options.add("All Open Auctions");
        options.add("Forward Auction");
        options.add("Reverse Auction");
        options.add("Double Auction");
        externalMenuChoice = printCustomMenuWithQuit(in, "little auction app", options);
        switch (externalMenuChoice) {
            
            case 1: // Available Items
                
                System.out.println(customTitleToString("Available Items"));
                msg = server.availableItemsToString();
                if(verify(msg)){
                    System.out.print((String)msg.getMessage());
                } else {
                    throw new SignatureException("Received message failed to verify");
                }
                confirmUserProceed(in);
                break;
                
            case 2: // All Open Auctions

                System.out.println(customTitleToString("All Open Auctions"));
                msg = server.openAuctionsToString("all");
                if(verify(msg)){
                    System.out.print((String)msg.getMessage());
                } else {
                    throw new SignatureException("Received message failed to verify");
                }
                confirmUserProceed(in);
                break;

            case 3: // Forward Auction 
                auctionType = "f";
                options.clear();
                options.add("List of Open Forward Auctions");
                options.add("Place a Bid");
                options.add("Open an Auction");
                options.add("Close an Auction");
                internalMenuChoice = printCustomMenu(in, "Forward Auction", options);
                switch (internalMenuChoice) {
                    case 1: // List open forward auctions
                        System.out.println(customTitleToString("Open Forward Auctions"));
                        msg = server.openAuctionsToString(auctionType);
                        if(verify(msg)){
                            System.out.print((String)msg.getMessage());
                        } else {
                            throw new SignatureException("Received message failed to verify");
                        }
                        confirmUserProceed(in);
                        break;
                
                    case 2: // Place a Bid
                        placeBid(auctionType, acc, server, in);
                        break;
                        
                    case 3: // Open an Auction
                        options.clear();
                        options.add("For a new item");
                        options.add("For an existing item");
                        
                        internalMenuChoice = printCustomMenu(in, "Open an Auction", options);
                        switch (internalMenuChoice) {
                            case 1: // For a new item
                                openForwAucNewItem(acc, server, in);
                                break;
                            case 2: // For an existing item
                                openForwAucExistItem(acc, server, in);
                                break;
                            case 0:
                                continue;
                            default:
                                break;
                        }
                        break;
                        
                    case 4: // Close an Auction
                        closeAuction(auctionType, acc, server, in);
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
                internalMenuChoice = printCustomMenu(in, "Reverse Auction", options);
                switch (internalMenuChoice) {
                    case 1: // List Reverse Auctions
                        System.out.println(customTitleToString("Open Reverse Auctions"));
                        server.openAuctionsToString(auctionType);
                        msg = server.openAuctionsToString(auctionType);
                        if(verify(msg)){
                            System.out.print((String)msg.getMessage());
                        } else {
                            throw new SignatureException("Received message failed to verify");
                        }
                        confirmUserProceed(in);
                        break;
                    case 2: // Place a bid
                        placeBid(auctionType, acc, server, in);
                        break;
                    case 3: // Open an Auction
                        options.clear();
                        options.add("For a new item");
                        options.add("For an existing item");
                        internalMenuChoice = printCustomMenu(in, "Open an Auction", options);
                        switch (internalMenuChoice) {
                            case 1: // For a new item
                                openRevAucNewItem(acc, server, in);
                                break;
                            case 2: // For an existing item
                                openRevAucExistItem(acc, server, in);
                                break;
                            case 0:
                                continue;
                            default:
                                break;
                        }
                        break;
                    case 4: // Close an Auction
                        closeAuction(auctionType, acc, server, in);
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
                internalMenuChoice = printCustomMenu(in, "Double Auction", options);
                switch (internalMenuChoice) {
                    case 1: // list
                        System.out.println(customTitleToString("All Open Auctions"));
                        msg = server.openAuctionsToString(auctionType);
                        if(verify(msg)){
                            System.out.print((String)msg.getMessage());
                        } else {
                            throw new SignatureException("Received message failed to verify");
                        }
                        confirmUserProceed(in);
                        break;
                    case 2: // place a buy
                        placeBuyReq(acc, server, in);
                        break;  
                    case 3: // place a sell
                        placeSellReq(acc, server, in);
                        break;   
                    case 4: //open auction
                        options.clear();
                        options.add("For a new item");
                        options.add("For an existing item");
                        internalMenuChoice = printCustomMenu(in, "Open an Auction", options);
                        switch (internalMenuChoice) {
                            case 1: // For a new item
                                openDoubleAucNewItem(acc, server, in);
                                confirmUserProceed(in);
                                break;
                            case 2: // For an existing item
                                openDoubleAucExistItem(acc, server, in);
                                confirmUserProceed(in);
                                break;
                            case 0:
                                continue;
                            default:
                                break;
                        }                     
                        break; 
                    case 5: // close auction
                        closeAuction(auctionType, acc, server, in);
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
    
    public static boolean confirmUserProceed(Scanner in){
        System.out.println("\nEnter K to continue");
        String out;
        printInputChar();
        do{
            out = in.nextLine();
        } while(!(out.toLowerCase().equals("k")));
        clearConsole();
        return true;
    }
    public static String customTitleToString(String title){
        String out = "---------------" + title + "---------------";
        out += "\n";
        return out;
    }
    public static int printCustomMenu(Scanner in, String title, LinkedList<String> options){
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
            try {
                printInputChar();
                out = in.nextInt(); in.nextLine();
            } catch (Exception e) {
                System.out.println("Invalid input. Enter one of the menu option numbers");
                in.nextLine();
            }
        } while(out < 0 || out > i);
        clearConsole();
        return out;
    }
    public static int printCustomMenuWithQuit(Scanner in, String title, LinkedList<String> options){
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
            try {
                printInputChar();
                out = in.nextInt(); in.nextLine();
            } catch (Exception e) {
                System.out.println("Invalid input. Enter one of the menu option numbers");
                in.nextLine();
            }
        } while(out < 0 || out > i);
        clearConsole();
        if(out == 0){
            System.out.println("Quitting...");
            in.close();
            System.exit(0);
        }
        return out;
    }

    public static void openForwAucNewItem(UserAccount acc, IAuction server, Scanner in){
        AuctionItem newItem;
        String newItemTitle;
        String newItemDescription;
        float startingPrice;
        float reservePrice;
        
        try {
            System.out.println(customTitleToString("Open Forward Auction for a New Item"));
                                
            System.out.println("New item's title");
            printInputChar(); newItemTitle = in.nextLine();
            System.out.println("New item's description");
            printInputChar(); newItemDescription = in.nextLine();
            System.out.println("Starting price");
            printInputChar(); startingPrice = in.nextFloat();
            System.out.println("Reserve price");
            printInputChar(); reservePrice = in.nextFloat();

            newItem = new AuctionItem(newItemTitle, newItemDescription);
            server.storeAuctionItem(newItem);
            server.createForwardAuction(acc, newItem, startingPrice, reservePrice);

            clearConsole();
            System.out.println(customTitleToString("New Item Added"));
            System.out.print(newItem.infoToString());
            System.out.println("Starting price: " + startingPrice);

            confirmUserProceed(in);
            
        } catch (Exception e) {
            System.err.println("Exception:");
            e.printStackTrace();
        }
    } 
    public static void openForwAucExistItem(UserAccount acc, IAuction server, Scanner in){
        SignedMessage msg;
        AuctionItem item;
        int auctionID;
        int itemID;
        float startingPrice;
        float reservePrice;
        boolean stayInMenu = true;
        
        do {
            try {
                System.out.println(customTitleToString("Open Auction for an Existing Item"));
                msg = server.availableItemsToString();
                if(verify(msg)){
                    System.out.print((String)msg.getMessage());
                } else {
                    throw new SignatureException("Received message failed to verify");
                }
                System.out.println("Item ID");
                try {
                    printInputChar();
                    itemID = in.nextInt(); in.nextLine();
                } catch (Exception e) {
                    System.out.println("Invalid input. Enter an integer");
                    continue;
                }
                if(itemID <= 0){
                    System.out.println("Invalid item ID. Enter a positive integer");
                    continue;
                } 

                msg = server.getItemInfo(itemID);
                if(verify(msg)){
                    item = (AuctionItem) msg.getMessage();
                } else {
                    throw new SignatureException("Received message failed to verify");
                }

                // if id does not exist
                if(item == null){
                    System.out.println("Item ID does not exist");
                    continue;
                }

                // valid input handling                
                System.out.println("Starting price");
                printInputChar(); startingPrice = in.nextFloat();
                System.out.println("Reserve price");
                printInputChar(); reservePrice = in.nextFloat();

                msg = server.createForwardAuction(acc, item, startingPrice, reservePrice);
                if(verify(msg)){
                    auctionID = (int) msg.getMessage();
                } else {
                    throw new SignatureException("Received message failed to verify");
                }

                clearConsole();
                System.out.println(customTitleToString("New Auction Opened"));
                msg = server.getAuctionInfo(auctionID);
                if(verify(msg)){
                    System.out.print((String)msg.getMessage());
                } else {
                    throw new SignatureException("Received message failed to verify");
                }

                confirmUserProceed(in);
                stayInMenu = false;

            } catch (Exception e) {
                System.err.println("Exception:");
                e.printStackTrace();
            }
        } while(stayInMenu);
    } 
    public static void openRevAucNewItem(UserAccount acc, IAuction server, Scanner in){
        AuctionItem newItem;
        String newItemTitle;
        String newItemDescription;

        try {
            System.out.println(customTitleToString("Open Reverse Auction for a New Item"));
            
            System.out.println("New item's title");
            printInputChar(); newItemTitle = in.nextLine();
            System.out.println("New item's description");
            printInputChar(); newItemDescription = in.nextLine();
            
            newItem = new AuctionItem(newItemTitle, newItemDescription);
            server.storeAuctionItem(newItem);
            SignedMessage msg = server.createReverseAuction(acc, newItem);
            if(verify(msg)){
                int aucID = (Integer) msg.getMessage();
                msg = server.getAuctionInfo(aucID);
                if(verify(msg)){
                    System.out.println("New auction created");
                    System.out.println((String)msg.getMessage());
                }
            } else {
                throw new SignatureException("Received message failed to verify");
            }
            clearConsole();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void openRevAucExistItem(UserAccount acc, IAuction server, Scanner in){
        SignedMessage msg;
        AuctionItem item;
        int itemID;
        boolean stayInMenu = true;
        do{
            try {
                System.out.println(customTitleToString("Open Reverse Auction for a New Item"));
                msg = server.availableItemsToString();
                if(verify(msg)){
                    System.out.print((String)msg.getMessage());
                } else {
                    throw new SignatureException("Received message failed to verify");
                }
                System.out.println("Item ID");
                try {
                    printInputChar();
                    itemID = in.nextInt(); in.nextLine();
                } catch (Exception e) {
                    System.out.println("Invalid input. Enter an integer");
                    continue;
                }
                if(itemID <= 0){
                    System.out.println("Invalid item ID. Enter a positive integer");
                    continue;
                } 

                msg = server.getItemInfo(itemID);
                if(verify(msg)){
                    item = (AuctionItem) msg.getMessage();
                } else {
                    throw new SignatureException("Received message failed to verify");
                }

                // if id does not exist
                if(item == null){
                    System.out.println("Item ID does not exist");
                    continue;
                }

                clearConsole();
                msg = server.createReverseAuction(acc, item);
                if(verify(msg)){
                    int aucID = (Integer) msg.getMessage();
                    msg = server.getAuctionInfo(aucID);
                    if(verify(msg)){
                        System.out.println("New auction created");
                        System.out.println((String)msg.getMessage());
                    }
                } else {
                    throw new SignatureException("Received message failed to verify");
                }

                confirmUserProceed(in);
                stayInMenu = false;
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while(stayInMenu);
    }
    public static void openDoubleAucNewItem(UserAccount acc, IAuction server, Scanner in){
        AuctionItem newItem;
        String newItemTitle;
        String newItemDescription;

        try {
            System.out.println(customTitleToString("Open Double Auction for a New Item"));
            
            System.out.println("New item's title");
            printInputChar(); newItemTitle = in.nextLine();
            System.out.println("New item's description");
            printInputChar(); newItemDescription = in.nextLine();
            
            newItem = new AuctionItem(newItemTitle, newItemDescription);
            server.storeAuctionItem(newItem);
            SignedMessage msg = server.createDoubleAuction(acc, newItem);
            if(verify(msg)){
                int aucID = (Integer) msg.getMessage();
                msg = server.getAuctionInfo(aucID);
                if(verify(msg)){
                    System.out.println("New auction created");
                    System.out.println((String)msg.getMessage());
                }
            } else {
                throw new SignatureException("Received message failed to verify");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void openDoubleAucExistItem(UserAccount acc, IAuction server, Scanner in){
        SignedMessage msg;
        AuctionItem item;
        int itemID;
        boolean stayInMenu = true;
        do{
            try {
                System.out.println(customTitleToString("Open Double Auction for a New Item"));
                msg = server.availableItemsToString();
                if(verify(msg)){
                    System.out.print((String)msg.getMessage());
                } else {
                    throw new SignatureException("Received message failed to verify");
                }
                System.out.println("Item ID");
                try {
                    printInputChar();
                    itemID = in.nextInt(); in.nextLine();
                } catch (Exception e) {
                    System.out.println("Invalid input. Enter an integer");
                    continue;
                }
                if(itemID <= 0){
                    System.out.println("Invalid item ID. Enter a positive integer");
                    continue;
                } 

                msg = server.getItemInfo(itemID);
                if(verify(msg)){
                    item = (AuctionItem) msg.getMessage();
                } else {
                    throw new SignatureException("Received message failed to verify");
                }

                // if id does not exist
                if(item == null){
                    System.out.println("Item ID does not exist");
                    continue;
                }

                clearConsole();
                msg = server.createDoubleAuction(acc, item);
                if(verify(msg)){
                    int aucID = (Integer) msg.getMessage();
                    msg = server.getAuctionInfo(aucID);
                    if(verify(msg)){
                        System.out.println("New auction created");
                        System.out.println((String)msg.getMessage());
                    }
                } else {
                    throw new SignatureException("Received message failed to verify");
                }

                confirmUserProceed(in);
                stayInMenu = false;
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while(stayInMenu);

    }
    public static void closeAuction(String auctionType, UserAccount acc, IAuction server, Scanner in) throws SignatureException{
        int auctionID;
        Auction closedAu;
        SignedMessage msg;

        try {
            System.out.println(customTitleToString("Close an Auction"));
            msg = server.openAuctionsToString(auctionType);
            if(verify(msg)){
                System.out.print((String)msg.getMessage());
            } else {
                throw new SignatureException("Received message failed to verify");
            }
            printInputChar();
            auctionID = in.nextInt(); in.nextLine();

            msg = server.getAuction(auctionID);          
            if(verify(msg)){
                closedAu = (Auction) msg.getMessage();
                msg = server.closeAuction(acc, closedAu.getAuctionID());
                if(verify(msg)){
                    System.out.print((String)msg.getMessage());
                } else {
                    throw new SignatureException("Received message failed to verify");
                }
            } else {
                throw new SignatureException("Received message failed to verify");
            }
            confirmUserProceed(in);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public static void placeBid(String aucType, UserAccount acc, IAuction server, Scanner in) throws SignatureException{
        int auctionID;
        float bidPrice = -1;
        String output;
        SignedMessage msg;

        try {
            System.out.println(customTitleToString("Place a Bid"));
            msg = server.openAuctionsToString(aucType);
            if(verify(msg)){
                System.out.println((String) msg.getMessage());
            } else {
                throw new SignatureException("Received message failed to verify");
            }
            System.out.println("Select an auction (input the ID)");
            printInputChar();
            auctionID = in.nextInt(); in.nextLine();

            do{
            System.out.println("Enter the bid amount");
            printInputChar(); bidPrice = in.nextFloat();
            if(bidPrice <= 0){
                System.out.println("Enter a positive sum");
            }
            } while(bidPrice <= 0);

            switch (aucType) {
                case "f":
                    msg = server.submitForwAucBid(acc, auctionID, new Bid(bidPrice, acc.getName(), acc.getEmail()));
                    break;
                case "r":
                    msg = server.submitRevAucBid(acc, auctionID, new Bid(bidPrice, acc.getName(), acc.getEmail()));
                default:
                    break;
            }
            if(verify(msg)){
                output = (String) msg.getMessage();
                System.out.println(output);
            } else {
                throw new SignatureException("Received message failed to verify");
            }

            confirmUserProceed(in);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public static void placeSellReq(UserAccount acc, IAuction server, Scanner in){
        int auctionID;
        String output;
        SignedMessage msg;
        String aucType = "d";
        float amount;

        try {
            System.out.println(customTitleToString("Place a Sell Request"));
            msg = server.openAuctionsToString(aucType);
            if(verify(msg)){
                System.out.println((String) msg.getMessage());
            } else {
                throw new SignatureException("Received message failed to verify");
            }
            System.out.println("Select an auction (input the ID)");
            printInputChar();
            auctionID = in.nextInt(); in.nextLine();

            do{
            System.out.println("Enter the bid amount");
            printInputChar(); amount = in.nextFloat();
            if(amount <= 0){
                System.out.println("Enter a positive sum");
            }
            } while(amount <= 0);

            msg = server.submitDoubleAucSell(acc, auctionID, new Bid(amount, acc.getName(), acc.getEmail()));

            if(verify(msg)){
                output = (String) msg.getMessage();
                System.out.println(output);
            } else {
                throw new SignatureException("Received message failed to verify");
            }

            confirmUserProceed(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void placeBuyReq(UserAccount acc, IAuction server, Scanner in){
        int auctionID;
        String output;
        SignedMessage msg;
        String aucType = "d";
        float amount;

        try {
            System.out.println(customTitleToString("Place a Buy Request"));
            msg = server.openAuctionsToString(aucType);
            if(verify(msg)){
                System.out.println((String) msg.getMessage());
            } else {
                throw new SignatureException("Received message failed to verify");
            }
            System.out.println("Select an auction (input the ID)");
            printInputChar();
            auctionID = in.nextInt(); in.nextLine();

            do{
            System.out.println("Enter the bid amount");
            printInputChar(); amount = in.nextFloat();
            if(amount <= 0){
                System.out.println("Enter a positive sum");
            }
            } while(amount <= 0);

            msg = server.submitDoubleAucBuy(acc, auctionID, new Bid(amount, acc.getName(), acc.getEmail()));

            if(verify(msg)){
                output = (String) msg.getMessage();
                System.out.println(output);
            } else {
                throw new SignatureException("Received message failed to verify");
            }

            confirmUserProceed(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static UserAccount login(IAuction server, Scanner in){
        System.out.println(customTitleToString("Log in"));
        try{
        System.out.println("Email");
        printInputChar(); String email = in.nextLine();
        System.out.println("Password");
        printInputChar(); String pass = in.nextLine();

        SignedMessage msg = server.login(email, pass);
        if(verify(msg)){
            UserAccount acc = (UserAccount) msg.getMessage();
            return acc;
        } else {
            throw new SignatureException("Received message failed to verify");
        }
        } catch(Exception e){e.printStackTrace(); return null;}
    }
    public static boolean register(IAuction server, Scanner in){
        System.out.println(customTitleToString("Register"));
        String name = "";
        String email = "";
        String password = "";
        try {

            do{
            System.out.println("Name");
            printInputChar(); name = in.nextLine();
            if(name.equals("")){
                System.out.println("Enter a non-empty name");
            }
            } while(name.equals(""));

            do{
            System.out.println("Email");
            printInputChar(); email = in.nextLine();
            if(email.equals("")){
                System.out.println("Enter a non-empty email");
            }
            } while(email.equals(""));

            do{
            System.out.println("Password");
            printInputChar(); password = in.nextLine();
            if(password.equals("")){
                System.out.println("Enter a non-empty password");
            }
            } while(password.equals(""));

            SignedMessage msg = server.register(name, email, password);
            if(verify(msg)){
                return (boolean) msg.getMessage();
            } else {
                throw new SignatureException("Received message failed to verify");
            }
        } catch (Exception e) {e.printStackTrace(); return false;}
    }
    
}
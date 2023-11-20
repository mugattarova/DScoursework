import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.io.Serializable;
import java.security.*;
import java.math.BigInteger;

public class MultiClient extends Client{
    public static void main(String[] args) {
        
    PublicKey pubKey;
    SignedMessage msg;
    UserAccount acc = null;

    try {
    String nameServer = "myserver";
    Registry registry = LocateRegistry.getRegistry("localhost");
    IAuction server = (IAuction) registry.lookup(nameServer);

    //pubKey = MessageEncryptionHelper.getPubKey("D:/Personal/LU_Leipzig_University/3Y/311DS/coursework1/client/public_key.der");
    
    AuctionItem ai1 = new AuctionItem("Fancy vase", "A vase from the 18th century. Looks intricate.");
    AuctionItem ai2 = new AuctionItem("Spider-man Funko Pop", "An unopened funko pop. I guess someone will want it.");
    AuctionItem ai3 = new AuctionItem("Old horseshoe", "It is said to bring a lot of luck.");
    server.storeAuctionItem(ai1); server.storeAuctionItem(ai2); server.storeAuctionItem(ai3);
    server.createAuction(ai1, 400, 500); server.createAuction(ai2, 100, 50); server.createAuction(ai3, 950, 400);

    Scanner in = new Scanner(System.in);
    int externalMenuChoice = 0;
    int internalMenuChoice;
    LinkedList<String> options = new LinkedList<String>();

    clearConsole();

    do{
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

    do {
        options.clear();
        options.add("All Available Items");
        options.add("All Open Auctions");
        options.add("Forward Auction");
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
                msg = server.openAuctionsToString();
                if(verify(msg)){
                    System.out.print((String)msg.getMessage());
                } else {
                    throw new SignatureException("Received message failed to verify");
                }
                confirmUserProceed(in);
                break;

            case 3: // Forward Auction 
                String auctionType = "f";

                options.clear();
                options.add("List of Open Forward Auctions");
                options.add("Place a Bid");
                options.add("Open an Auction");
                options.add("Close an Auction");
                internalMenuChoice = printCustomMenu(in, "Forward Auction", options);
                switch (internalMenuChoice) {
                    case 1: // List open forward auctions
                        System.out.println(customTitleToString("Open Forward Auctions"));
                        msg = server.openAuctionsToString();
                        if(verify(msg)){
                            System.out.print((String)msg.getMessage());
                        } else {
                            throw new SignatureException("Received message failed to verify");
                        }
                        confirmUserProceed(in);
                        break;
                
                    case 2: // Place a Bid
                        placeBid(auctionType, server, in);
                        break;
                        
                    case 3: // Open an Auction
                        options.clear();
                        options.add("For a new item");
                        options.add("For an existing item");
                        
                        internalMenuChoice = printCustomMenu(in, "Open an Auction", options);
                        switch (internalMenuChoice) {
                            case 1: // For a new item
                                openAucNewItem(server, in);
                                break;

                            case 2: // For an existing item
                                openAucExistItem(server, in);
                                break;

                            case 0:
                                continue;

                            default:
                                break;
                        }
                        break;
                        
                    case 4: // Close an Auction
                        closeAuction(server, in);
                        break;
                                                            
                    case 0: // Back
                        
                        continue;                                   
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
        System.out.println("\nPress K to continue");
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

    public static void openAucNewItem(IAuction server, Scanner in){
        
        AuctionItem newItem;
        String newItemTitle;
        String newItemDescription;
        float startingPrice;
        float reservePrice;
        
        try {
            System.out.println(customTitleToString("Open Auction for a New Item"));
                                
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
            server.createAuction(newItem, startingPrice, reservePrice);

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
    public static void openAucExistItem(IAuction server, Scanner in){
        
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

                msg = server.createAuction(item, startingPrice, reservePrice);
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
    public static void closeAuction(IAuction server, Scanner in) throws SignatureException{
        int auctionID;
        Auction closedAu;
        AuCloseOutcome outcome;
        SignedMessage msg;

        try {
            System.out.println(customTitleToString("Close an Auction"));
            msg = server.openAuctionsToString();
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
            } else {
                throw new SignatureException("Received message failed to verify");
            }

            msg = server.closeAuction(auctionID);
            if(verify(msg)){
                outcome = (AuCloseOutcome) msg.getMessage();
            } else {
                throw new SignatureException("Received message failed to verify");
            }
            switch (outcome) {
                case Sold:
                    System.out.println("Auction item sold!");
                    System.out.print(closedAu.getWinnerBid().infoToString());
                    break;
                
                case NoBids:
                    System.out.println("No bids were placed. The item was not sold.");
                    break;

                case ReserveNotMet:
                    System.out.println("Reserve price was not met. The item was not sold.");
                    break;

                case NoClosePermission:
                    System.out.println("You have no permission to close this auction.");
                    break;

                case DoesNotExist:
                    System.out.println("Auction ID is invalid.");
                    break;
                default:
                    break;
            }

            confirmUserProceed(in);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public static void placeBid(String aucType, IAuction server, Scanner in) throws SignatureException{
        int auctionID;
        float bidPrice;
        String output;
        SignedMessage msg;

        try {
            System.out.println(customTitleToString("Place a Bid"));
            msg = server.openAuctionsToString();
            if(verify(msg)){
                System.out.println((String) msg.getMessage());
            } else {
                throw new SignatureException("Received message failed to verify");
            }
            System.out.println("Select an auction (input the ID)");
            printInputChar();
            auctionID = in.nextInt(); in.nextLine();
            System.out.println("Enter the bid amount");
            printInputChar();
            bidPrice = in.nextFloat();
            //TODO after accounts are implemented
            msg = server.submitBid(auctionID,  new Bid(bidPrice, "Nellie", "cool@email.com"));
            if(verify(msg)){
                output = (String) msg.getMessage();
            } else {
                throw new SignatureException("Received message failed to verify");
            }
            System.out.println(output);

            confirmUserProceed(in);
        } catch (RemoteException e) {
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
            return (UserAccount) msg.getMessage();
        } else {
            throw new SignatureException("Received message failed to verify");
        }
        } catch(Exception e){e.printStackTrace(); return null;}
    }
    public static boolean register(IAuction server, Scanner in){
        System.out.println(customTitleToString("Register"));
        try {
            System.out.println("Name");
            printInputChar(); String name = in.nextLine();
            System.out.println("Email");
            printInputChar(); String email = in.nextLine();
            System.out.println("Password");
            printInputChar(); String pass = in.nextLine();

            SignedMessage msg = server.register(name, email, pass);
            if(verify(msg)){
                return (boolean) msg.getMessage();
            } else {
                throw new SignatureException("Received message failed to verify");
            }
        } catch (Exception e) {e.printStackTrace(); return false;}
    }
    
    // public static void printHashVerif(SignedMessage received, Serializable decrypted){
    //     System.out.println(customTitleToString("Hash verification"));
    //     String recHash = String.format("%02X", received.getHashedMessage());
    //     System.out.println("Received hash digest: " + recHash);
    //     String ogHash = String.format("%02X", MessageEncryptionHelper.hashMessage(received.getMessage()));
    //     System.out.println("Original message hash digest: " + ogHash);
    // }
}
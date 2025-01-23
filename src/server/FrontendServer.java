import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.jgroups.JChannel;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.util.Rsp;
import org.jgroups.util.RspList;

import java.security.*;

public class FrontendServer implements IAuctionApp{
    private PrivateKey privKey;

    private RequestOptions opts;
    private JChannel channel;
    private RpcDispatcher disp;

    public FrontendServer(){
        try{
            privKey = MessageEncryptionHelper.getPrivKey("./private_key.der");
            opts=new RequestOptions(ResponseMode.GET_ALL, 5000, false);
            channel=new JChannel();
            channel.connect("AuctionChannel");
            channel.setDiscardOwnMessages(true);
            disp=new RpcDispatcher(channel, this);

            register("b1", "b1", "b1");
            register("b2", "b2", "b2");
            register("b3", "b3", "b3");
            register("s1", "s1", "s1");
            register("s2", "s2", "s2");
            register("s3", "s3", "s3");

            AuctionItem ai1 = new AuctionItem("Fancy vase", "A vase from the 18th century. Looks intricate.");
            AuctionItem ai2 = new AuctionItem("Spider-man Funko Pop", "An unopened funko pop. I guess someone will want it.");
            AuctionItem ai3 = new AuctionItem("Old horseshoe", "It is said to bring a lot of luck.");
            storeAuctionItem(ai1); storeAuctionItem(ai2); storeAuctionItem(ai3);
        
        } catch (Exception e) {e.printStackTrace();}
    }

    public static void main(String[] args) {
        try {
            FrontendServer s = new FrontendServer();
            String name = "frontendserver";
            IAuctionApp stub = (IAuctionApp) UnicastRemoteObject.exportObject(s, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);

            System.out.println("Frontend server ready");

        } catch (Exception e) {
            System.err.println("Exception:");
            e.printStackTrace();
        }
    }

    public <T extends Serializable> boolean isConsensusReached(RspList<SignedMessage<T>> rspList) throws Exception, ConsensusNotReachedException{
        T prev_val=null;
        int indexCount=0;
        for (Rsp<SignedMessage<T>> rsp : rspList) {
            if (rsp.hasException()){
                throw new Exception(rsp.getException());
            } else{
                T message = (T) rsp.getValue().getMessage();
                if(indexCount==0){
                    prev_val=message;
                } else{
                    if(message.equals(prev_val)){
                        prev_val=message;
                        continue;
                    } else{
                        throw new ConsensusNotReachedException("Consensus was not reached");
                    }
                }
            }
            indexCount++;
        }
        return true;
    }
    public <T extends Serializable> SignedMessage<T> getConsensusMessage(RspList<SignedMessage<T>> rspList){
        return rspList.getFirst();
    }

    public SignedMessage<AuctionItem> getItem(int itemID) throws RemoteException{
        try {
            RspList<SignedMessage<AuctionItem>> responces = disp.callRemoteMethods(null, "getItem", new Object[]{itemID}, new Class[]{int.class}, opts);
            if (isConsensusReached(responces)){
                return responces.getFirst();
            }
            return new SignedMessage<AuctionItem>(null, privKey);
        } catch (Exception e) {
            e.printStackTrace();
            return new SignedMessage<AuctionItem>(null, privKey);
        }
    }
    public SignedMessage<Auction> getAuction(int auctionID) throws RemoteException{
        try {
            RspList<SignedMessage<Auction>> responces = disp.callRemoteMethods(null, "getAuction", new Object[]{auctionID}, new Class[]{int.class}, opts);
            if (isConsensusReached(responces)){
                return responces.getFirst();
            }
            return new SignedMessage<Auction>(null, privKey);
        } catch (Exception e) {
            e.printStackTrace();
            return new SignedMessage<Auction>(null, privKey);
        }
    }
    public SignedMessage<String> getAuctionInfo(int auctionID) throws RemoteException{
        try {
            RspList<SignedMessage<String>> responces = disp.callRemoteMethods(null, "getAuctionInfo", new Object[]{auctionID}, new Class[]{int.class}, opts);
            if (isConsensusReached(responces)){
                return responces.getFirst();
            }
            return new SignedMessage<String>(null, privKey);
        } catch (Exception e) {
            e.printStackTrace();
            return new SignedMessage<String>(null, privKey);
        }
    }
    public void storeAuctionItem(AuctionItem aui) throws RemoteException{
        try {
            disp.callRemoteMethods(null, "storeAuctionItem", new Object[]{aui}, new Class[]{aui.getClass()}, opts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void storeAuctionItem(String _itemTitle, String _itemDescription) throws RemoteException{
        AuctionItem aui = new AuctionItem(_itemTitle, _itemDescription);
        try {
            disp.callRemoteMethods(null, "storeAuctionItem", new Object[]{aui}, new Class[]{aui.getClass()}, opts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public SignedMessage<Integer> createForwardAuction(UserAccount owner, AuctionItem aui, float startingPrice, float reservePrice) throws RemoteException {
        try {
            RspList<SignedMessage<Integer>> responces = disp.callRemoteMethods(null, "createForwardAuction", new Object[]{owner, aui, startingPrice, reservePrice}, new Class[]{owner.getClass(), aui.getClass(), float.class, float.class}, opts);
            if (isConsensusReached(responces)){
                return responces.getFirst();
            }
            return new SignedMessage<Integer>(null, privKey);
        } catch (Exception e) {
            e.printStackTrace();
            return new SignedMessage<Integer>(null, privKey);
        }
    }
    public SignedMessage<Integer> createReverseAuction(UserAccount owner, AuctionItem aui) throws RemoteException{
        try {
            RspList<SignedMessage<Integer>> responces = disp.callRemoteMethods(null, "createReverseAuction", new Object[]{owner, aui}, new Class[]{owner.getClass(), aui.getClass()}, opts);
            if (isConsensusReached(responces)){
                return responces.getFirst();
            }
            return new SignedMessage<Integer>(null, privKey);
        } catch (Exception e) {
            e.printStackTrace();
            return new SignedMessage<Integer>(null, privKey);
        }
    }
    public SignedMessage<Integer> createDoubleAuction(UserAccount owner, AuctionItem aui) throws RemoteException{
        try {
            RspList<SignedMessage<Integer>> responces = disp.callRemoteMethods(null, "createDoubleAuction", new Object[]{owner, aui}, new Class[]{owner.getClass(), aui.getClass()}, opts);
            if (isConsensusReached(responces)){
                return responces.getFirst();
            }
            return new SignedMessage<Integer>(null, privKey);
        } catch (Exception e) {
            e.printStackTrace();
            return new SignedMessage<Integer>(null, privKey);
        }    
    }
    public SignedMessage<String> closeAuction(UserAccount acc, int auctionID) throws RemoteException {
        try {
            RspList<SignedMessage<String>> responces = disp.callRemoteMethods(null, "closeAuction", new Object[]{acc, auctionID}, new Class[]{acc.getClass(), int.class}, opts);
            if (isConsensusReached(responces)){
                return responces.getFirst();
            }
            return new SignedMessage<String>(null, privKey);
        } catch (Exception e) {
            e.printStackTrace();
            return new SignedMessage<String>(null, privKey);
        } 
    }
    public SignedMessage<String> submitForwAucBid(UserAccount acc, int auctionID, Bid bid) throws RemoteException{
        try {
            RspList<SignedMessage<String>> responces = disp.callRemoteMethods(null, "closeAuction", new Object[]{acc, auctionID, bid}, new Class[]{acc.getClass(), int.class, bid.getClass()}, opts);
            if (isConsensusReached(responces)){
                return responces.getFirst();
            }
            return new SignedMessage<String>(null, privKey);
        } catch (Exception e) {
            e.printStackTrace();
            return new SignedMessage<String>(null, privKey);
        } 
    }
    public SignedMessage<String> submitRevAucBid(UserAccount acc, int auctionID, Bid bid) throws RemoteException{
        try {
            RspList<SignedMessage<String>> responces = disp.callRemoteMethods(null, "submitRevAucBid", new Object[]{acc, auctionID, bid}, new Class[]{acc.getClass(), int.class, bid.getClass()}, opts);
            if (isConsensusReached(responces)){
                return responces.getFirst();
            }
            return new SignedMessage<String>(null, privKey);
        } catch (Exception e) {
            e.printStackTrace();
            return new SignedMessage<String>(null, privKey);
        } 
    }
    public SignedMessage<String> submitDoubleAucSell(UserAccount acc, int auctionID, Bid bid) throws RemoteException{
        try {
            RspList<SignedMessage<String>> responces = disp.callRemoteMethods(null, "submitDoubleAucSell", new Object[]{acc, auctionID, bid}, new Class[]{acc.getClass(), int.class, bid.getClass()}, opts);
            if (isConsensusReached(responces)){
                return responces.getFirst();
            }
            return new SignedMessage<String>(null, privKey);
        } catch (Exception e) {
            e.printStackTrace();
            return new SignedMessage<String>(null, privKey);
        } 
    }
    public SignedMessage<String> submitDoubleAucBuy(UserAccount acc, int auctionID, Bid bid) throws RemoteException{
        try {
            RspList<SignedMessage<String>> responces = disp.callRemoteMethods(null, "submitDoubleAucBuy", new Object[]{acc, auctionID, bid}, new Class[]{acc.getClass(), int.class, bid.getClass()}, opts);
            if (isConsensusReached(responces)){
                return responces.getFirst();
            }
            return new SignedMessage<String>(null, privKey);
        } catch (Exception e) {
            e.printStackTrace();
            return new SignedMessage<String>(null, privKey);
        } 
    }
    public SignedMessage<String> openAuctionsToString(String auctionType) throws RemoteException{
        try {
            RspList<SignedMessage<String>> responces = disp.callRemoteMethods(null, "openAuctionsToString", new Object[]{auctionType}, new Class[]{auctionType.getClass()}, opts);
            if (isConsensusReached(responces)){
                return responces.getFirst();
            }
            return new SignedMessage<String>(null, privKey);
        } catch (Exception e) {
            e.printStackTrace();
            return new SignedMessage<String>(null, privKey);
        } 
    }
    public SignedMessage<String> availableItemsToString() throws RemoteException{
        try {
            RspList<SignedMessage<String>> responces = disp.callRemoteMethods(null, "availableItemsToString", new Object[]{}, new Class[]{}, opts);
            if (isConsensusReached(responces)){
                return responces.getFirst();
            }
            return new SignedMessage<String>(null, privKey);
        } catch (Exception e) {
            e.printStackTrace();
            return new SignedMessage<String>(null, privKey);
        } 
    }

    public SignedMessage<Boolean> register(String name, String email, String password) throws RemoteException{
        try {
            String salt = PasswordEncryptionHelper.genPasswordSalt(32);
            RspList<SignedMessage<Boolean>> responces = disp.callRemoteMethods(null, "register", new Object[]{name, email, password, salt}, new Class[]{name.getClass(), email.getClass(), password.getClass(), salt.getClass()}, opts);
            if (isConsensusReached(responces)){
                return responces.getFirst();
            }
            return new SignedMessage<Boolean>(null, privKey);
        } catch (Exception e) {
            e.printStackTrace();
            return new SignedMessage<Boolean>(null, privKey);
        } 
    }
    public SignedMessage<UserAccount> login(String email, String password) throws RemoteException{
        try {
            RspList<SignedMessage<UserAccount>> responces = disp.callRemoteMethods(null, "login", new Object[]{email, password}, new Class[]{email.getClass(), password.getClass()}, opts);
            if (isConsensusReached(responces)){
                return responces.getFirst();
            }
            return new SignedMessage<UserAccount>(null, privKey);
        } catch (Exception e) {
            e.printStackTrace();
            return new SignedMessage<UserAccount>(null, privKey);
        } 
    }

    public BackendState getState() throws Exception{
        return disp.callRemoteMethod(channel.getView().get(1), "getState", new Object[]{}, new Class[]{}, opts);
    }
}

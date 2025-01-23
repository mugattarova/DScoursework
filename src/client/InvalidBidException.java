public class InvalidBidException extends Exception{
    public InvalidBidException(String errorMessage){
        super(errorMessage);
    }
    public InvalidBidException(String errorMessage, Throwable err){
        super(errorMessage, err);
    }
}

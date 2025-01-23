public class ConsensusNotReachedException extends Exception{
    public ConsensusNotReachedException(String errorMessage){
        super(errorMessage);
    }
    public ConsensusNotReachedException(String errorMessage, Throwable err){
        super(errorMessage, err);
    }
}

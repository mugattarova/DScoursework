public class PermissionDeniedException extends Exception{
    public PermissionDeniedException(){
        super("Permission denied");
    }
    public PermissionDeniedException(String errorMessage){
        super(errorMessage);
    }
    public PermissionDeniedException(String errorMessage, Throwable err){
        super(errorMessage, err);
    }
}

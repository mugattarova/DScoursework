import java.io.Serializable;

public class UserAccount implements Serializable{

    private static int idCounter = 0;
    private int id;
    private String name;
    private String email;

    public UserAccount(String _name, String _email){
        id = ++idCounter;
        name = _name;
        email =_email;
    }

    public String getEmail() {
        return email;
    }
    
    public String getName() {
        return name;
    }

}

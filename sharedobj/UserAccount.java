import java.io.Serializable;

public class UserAccount implements Serializable{

    private int id;
    private String name;
    private String email;

    public UserAccount(int _id, String _name, String _email){
        id = _id;
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

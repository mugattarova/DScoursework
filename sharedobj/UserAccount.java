import java.io.Serializable;

public class UserAccount implements Serializable{

    private String name;
    private String email;

    public UserAccount(String _name, String _email){
        name = _name;
        email =_email;
    }

    public String getEmail() {
        return email;
    }
    public String getName() {
        return name;
    }
    @Override
    public boolean equals(Object o){
        if(!(o instanceof UserAccount)){
            return false;
        }
        UserAccount newAcc = (UserAccount) o;
        if((name.equals(newAcc.name)) && (email.equals(newAcc.email))) {
            return true;
        }
        return false;
    }
}

package account.errors;

public class UserNotHaveRoleException extends RuntimeException{
    public UserNotHaveRoleException() {
        super("The user does not have a role!");
    }
}

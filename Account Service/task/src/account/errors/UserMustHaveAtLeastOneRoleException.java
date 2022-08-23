package account.errors;

public class UserMustHaveAtLeastOneRoleException extends  RuntimeException{
    public UserMustHaveAtLeastOneRoleException() {
        super("The user must have at least one role!");
    }
}

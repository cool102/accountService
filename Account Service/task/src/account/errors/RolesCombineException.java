package account.errors;

public class RolesCombineException extends  RuntimeException{
    public RolesCombineException() {
        super("The user cannot combine administrative and business roles!");
    }
}

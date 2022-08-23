package account.errors;

public class AdminRoleRemoveException extends  RuntimeException{
    public AdminRoleRemoveException() {
        super("Can't remove ADMINISTRATOR role!");
    }
}

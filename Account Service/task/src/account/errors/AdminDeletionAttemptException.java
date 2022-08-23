package account.errors;

public class AdminDeletionAttemptException extends  RuntimeException{
    public AdminDeletionAttemptException() {
        super("Can't remove ADMINISTRATOR role!");
    }
}

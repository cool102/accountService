package account.errors;

public class UserExistException extends RuntimeException {
    public UserExistException() {
        super("User exist!");
    }
}

package account.errors;

public class SamePasswordException extends RuntimeException {
    public SamePasswordException() {
        super("The passwords must be different!");
    }
}

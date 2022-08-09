package account.errors;

public class CompromisedPasswordException extends RuntimeException {


    public CompromisedPasswordException() {
        super("The password is in the hacker's database!");
    }
}

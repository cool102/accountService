package account.errors;

public class WrongPeriodException extends RuntimeException {
    public WrongPeriodException() {
        super("Wrong date!");
    }
}

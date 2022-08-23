package account.errors;

public class AdminLockingAttemptException  extends  RuntimeException{

        public AdminLockingAttemptException() {
            super("Can't lock the ADMINISTRATOR!");
        }


}

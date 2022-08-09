package account.errors;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Constraint(validatedBy = SalaryPeriodValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)

public @interface SalaryPeriodConstraint {
    String message() default "Wrong dateeeee!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

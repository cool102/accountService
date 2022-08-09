package account.errors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SalaryPeriodValidator implements ConstraintValidator<SalaryPeriodConstraint, String> {
    @Override
    public boolean isValid(String periodField, ConstraintValidatorContext context) {
        return periodField.matches("([0][1-9]-2[0-9]{3}|[1][0-2]-2[0-9]{3})");
    }
}

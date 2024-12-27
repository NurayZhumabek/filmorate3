package by.nuray.filmorate3.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;



public class ReleaseDateValidator implements ConstraintValidator<ValidReleaseDate, LocalDate> {

    private final static LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {

        if (localDate == null) {
            return false;
        }
        return !localDate.isBefore(MIN_RELEASE_DATE);
    }
}

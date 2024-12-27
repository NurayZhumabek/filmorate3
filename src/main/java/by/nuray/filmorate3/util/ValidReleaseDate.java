package by.nuray.filmorate3.util;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ReleaseDateValidator.class)
public @interface ValidReleaseDate {

    String message() default "The release date is no earlier than the December 28,1895";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

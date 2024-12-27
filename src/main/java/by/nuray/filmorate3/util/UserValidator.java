package by.nuray.filmorate3.util;


import by.nuray.filmorate3.models.User;
import by.nuray.filmorate3.storage.UserStorage;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class UserValidator implements Validator {

    private final UserStorage userStorage;


    public UserValidator(UserStorage userStorage) {
        this.userStorage = userStorage;
    }




    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            errors.rejectValue("email", null, "Email cannot be empty");
            return;
        }

        System.out.println("Checking email: " + user.getEmail());

        if (userStorage.getByEmail(user.getEmail()).isPresent()) {
            errors.rejectValue("email", null, "This email is already in use");
        }

        if (userStorage.getByLogin(user.getLogin()).isPresent()) {
            errors.rejectValue("login", null, "This login is already in use");
        }
    }
}

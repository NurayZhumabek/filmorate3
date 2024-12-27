package by.nuray.filmorate3.service;



import by.nuray.filmorate3.models.User;
import by.nuray.filmorate3.storage.FilmStorage;
import by.nuray.filmorate3.storage.UserStorage;
import by.nuray.filmorate3.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;

@Service
public class UserService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final UserValidator userValidator;


    @Autowired
    public UserService(UserStorage userStorage, FilmStorage filmStorage, UserValidator userValidator) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.userValidator = userValidator;
    }


    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(int id) {
        return userStorage.getById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public void save(User user) {
        if (user == null) {
            throw new UserNotCreatedException("User object is null and cannot be created");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new UserAlreadyExistsException("Email cannot be empty");
        }

        if (userStorage.getByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("This email is already in use");
        }

        if (user.getLogin() == null || user.getLogin().trim().isEmpty()) {
            throw new UserNotCreatedException("Login cannot be empty");
        }

        if (user.getBirthday() == null || user.getBirthday().isAfter(java.time.LocalDate.now())) {
            throw new UserNotCreatedException("Invalid birthday");
        }

        try {
            userStorage.save(user);
        } catch (Exception e) {
            throw new UserNotCreatedException("Failed to save user: " + e.getMessage());
        }
    }



    public void update(User user, int id, BindingResult bindingResult) {

        getUserById(id);

        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new UserValidationException(bindingResult.getAllErrors().toString());
        }

        userStorage.update(user, id);
    }

    public void delete(int id) {
        getUserById(id);
        userStorage.delete(id);
    }

    public void addFriend(int userId, int anotherUserId) {

        User user = getUserById(userId);
        User anotherUser = getUserById(anotherUserId);

        List<User> userFriends = userStorage.getFriends(userId);
        if (userFriends.contains(anotherUser)) {
            throw new FriendshipAlreadyExistsException("These users are already friends");
        }

        userStorage.addFriend(userId, anotherUserId);
    }

    public void removeFriend(int userId, int anotherUserId) {
        User user = getUserById(userId);
        User anotherUser = getUserById(anotherUserId);

        List<User> userFriends = userStorage.getFriends(userId);
        List<User> anotherUserFriends = userStorage.getFriends(anotherUserId);

        if (!userFriends.contains(anotherUser) || !anotherUserFriends.contains(user)) {
            throw new FriendshipDoesNotExist("These users are not friends");
        }

        userStorage.removeFriend(userId, anotherUserId);
    }

    public List<User> getFriends(int userId) {
        getUserById(userId);
        return userStorage.getFriends(userId);
    }

    public List<User> mutualFriends(int userId, int anotherUserId) {

        getUserById(userId);
        getUserById(anotherUserId);

        return userStorage.mutualFriends(userId, anotherUserId);
    }

    public List<User> getPendingRequests(int userId) {
        getUserById(userId);
        return userStorage.getPendingRequests(userId);
    }

    public void accept(int userId, int anotherUserId, boolean isAccepted) {
        getUserById(userId);
        User anotherUser= getUserById(anotherUserId);

        List<User> waitingUsers =getPendingRequests(userId);
        if (!waitingUsers.contains(anotherUser)){
            throw new FriendshipRequestNotFound("No waiting request between users");
        }

        userStorage.accept(userId, anotherUserId, isAccepted);

    }

    public void like(int userId, int filmId) {
        getUserById(userId);
        filmStorage.getById(filmId).orElseThrow(() -> new FilmNotFoundException("Film not found"));

        if (userStorage.hasLiked(userId, filmId)) {
            throw new UserAlreadyExistsException("User has already liked ");
        }

        userStorage.like(userId, filmId);

    }

    public void dislike(int userId, int filmId) {
        getUserById(userId);
        filmStorage.getById(filmId).orElseThrow(() -> new FilmNotFoundException("Film not found"));

        if (!userStorage.hasLiked(userId, filmId)) {
            throw new UserAlreadyExistsException("User has not liked this film yet");
        }

        userStorage.dislike(userId, filmId);
    }




    }
package by.nuray.filmorate3.controller;


import by.nuray.filmorate3.models.User;
import by.nuray.filmorate3.service.UserService;
import by.nuray.filmorate3.util.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }


    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") int id) {
        try {
            return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
        }catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }


    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody @Valid User user,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("\n");
            }
            return new ResponseEntity<>(errors.toString(), HttpStatus.BAD_REQUEST);
        }
        try {
            userService.save(user);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (UserNotCreatedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @PutMapping()
    public ResponseEntity<?> updateUser(@RequestBody @Valid User user, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .collect(Collectors.toList());
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        try {
            userService.getUserById(user.getId());
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found", e);
        }

        userService.update(user, user.getId(), bindingResult);

        return new ResponseEntity<>(user, HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") int id) {
        try {
            userService.delete(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<?> addFriend(@PathVariable("id") int id, @PathVariable("friendId") int friendId) {


        try {
            userService.addFriend(id, friendId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (FriendshipAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<?> removeFriend(@PathVariable("id") int id, @PathVariable("friendId") int friendId) {
        try {
            userService.removeFriend(id, friendId);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (FriendshipDoesNotExist e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        catch (UserNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

        }
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<?> getFriends(@PathVariable("id") int id) {
        try {
            return new ResponseEntity<>(userService.getFriends(id), HttpStatus.OK);
        }catch (UserNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<?> getFriendsCommon(@PathVariable("id") int id, @PathVariable("otherId") int otherId) {
        try {
            return new ResponseEntity<>(userService.mutualFriends(id, otherId), HttpStatus.OK);
        }catch (UserNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}/pending")
    public ResponseEntity<?> getPendingRequests(@PathVariable("id") int id) {
        try {
            return new ResponseEntity<>(userService.getPendingRequests(id), HttpStatus.OK);
        }
        catch (UserNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/accept/{otherId}")
    public ResponseEntity<?> acceptFriend(@PathVariable("id") int id, @PathVariable("otherId") int otherId,
                                          @RequestParam(value="accept") boolean accept) {
        try {
            userService.accept(id, otherId, accept);
            return new ResponseEntity<>("You added user",HttpStatus.OK);
        }catch (UserNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }catch (FriendshipRequestNotFound e){
            return new ResponseEntity<>("Friendship request not found", HttpStatus.NOT_FOUND);
        }
    }






    @PutMapping("/films/{id}/like/{userId}")
    public ResponseEntity<?> likeFilm(@PathVariable("id") int filmId, @PathVariable("userId") int userId) {
        try{
            userService.like(filmId, userId);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (UserNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        catch (FilmNotFoundException e){
            return new ResponseEntity<>("Film not found", HttpStatus.NOT_FOUND);
        }
        catch (LikeAlreadyExistsException e){
            return new ResponseEntity<>("Like already exists", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/films/{id}/dislike/{userId}")
    public ResponseEntity<?> dislikeFilm(@PathVariable("id") int filmId, @PathVariable("userId") int userId) {
        try{
            userService.dislike(filmId, userId);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (UserNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }catch (FilmNotFoundException e){
            return new ResponseEntity<>("Film not found", HttpStatus.NOT_FOUND);
        }catch (LikeNotFoundException e){
            return new ResponseEntity<>("Like not found", HttpStatus.NOT_FOUND);
        }
    }
















    @ExceptionHandler
    public ResponseEntity<UserErrorResponse> handle(UserNotFoundException e) {

        UserErrorResponse errorResponse=new UserErrorResponse(
                e.getMessage(),System.currentTimeMillis());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<UserErrorResponse> handleException(UserNotCreatedException ex) {
        UserErrorResponse err=new UserErrorResponse(
                ex.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }



    @ExceptionHandler(FriendshipAlreadyExistsException.class)
    public ResponseEntity<?> handleFriendshipAlreadyExists(FriendshipAlreadyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<FilmErrorResponse> handleException(FilmNotFoundException ex) {
        FilmErrorResponse err=new FilmErrorResponse(
                ex.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
    }
}

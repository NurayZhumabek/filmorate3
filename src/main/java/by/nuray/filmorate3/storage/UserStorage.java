package by.nuray.filmorate3.storage;


import by.nuray.filmorate3.models.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {


        public Optional<User> getById(int id);
        public List<User> getAllUsers();
        public void save(User user);
        public void update(User updatedUser,int id);
        public void delete(int id);

        public Optional<User> getByEmail(String email);
        public Optional<User> getByLogin(String login);



        public void addFriend(int userId, int anotherUserId);
        public void removeFriend(int id1, int id2);
        public List<User> getFriends(int id);
        public List<User> mutualFriends(int id1, int id2);


        List<User> getPendingRequests(int userId);

        void accept(int userId, int anotherUserId, boolean isAccepted);

        void like(int userId, int filmId);

        void dislike(int userId, int filmId);

        boolean hasLiked(int userId, int filmId);


}


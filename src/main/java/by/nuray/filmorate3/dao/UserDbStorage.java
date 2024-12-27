package by.nuray.filmorate3.dao;

import by.nuray.filmorate3.models.User;
import by.nuray.filmorate3.storage.UserStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<User> getById(int id) {
        return jdbcTemplate.query("SELECT * FROM users WHERE id = ?",
                new Object[]{id}, new BeanPropertyRowMapper<>(User.class)).stream().findFirst();
    }

    @Override
    public List<User> getAllUsers() {
        return jdbcTemplate.query("SELECT * FROM users", new BeanPropertyRowMapper<>(User.class));
    }

    @Override
    public void save(User user) {
        jdbcTemplate.update("INSERT INTO users (email, login, birthday) VALUES (?, ?, ?)",
                user.getEmail(), user.getLogin(), user.getBirthday());
    }

    @Override
    public void update(User user, int id) {
        jdbcTemplate.update("UPDATE users SET email = ?, login = ?, birthday = ? WHERE id = ?",
                user.getEmail(), user.getLogin(), user.getBirthday(), id);
    }

    @Override
    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
    }

    @Override
    public Optional<User> getByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        return jdbcTemplate.query(sql, new Object[]{email}, new BeanPropertyRowMapper<>(User.class))
                .stream()
                .findFirst();
    }
    @Override
    public Optional<User> getByLogin(String login) {
        return jdbcTemplate.query("SELECT * FROM users WHERE login = ?",
                new Object[]{login}, new BeanPropertyRowMapper<>(User.class)).stream().findFirst();
    }

    @Override
    public void addFriend(int userId, int anotherUserId) {
        jdbcTemplate.update("INSERT INTO friendship (user_id, another_user_id, is_accepted) VALUES (?, ?, false)",
                userId, anotherUserId);
    }

    @Override
    public void removeFriend(int id1, int id2) {
        jdbcTemplate.update("DELETE FROM friendship WHERE user_id = ? AND another_user_id = ?", id1, id2);
    }

    @Override
    public List<User> getFriends(int id) {
        return jdbcTemplate.query("SELECT u.* " +
                        "FROM users u " +
                        "JOIN friendship f ON (f.user_id = u.id OR (f.another_user_id = u.id AND f.is_accepted = TRUE)) " +
                        "WHERE f.user_id = ? OR f.another_user_id = ?",
                new Object[]{id, id}, new BeanPropertyRowMapper<>(User.class));
    }

    @Override
    public List<User> mutualFriends(int userId, int anotherUserId) {
        List<User> userFriends = getFriends(userId);
        List<User> anotherUserFriends = getFriends(anotherUserId);

        Set<User> userFriendsSet = new HashSet<>(userFriends);
        userFriendsSet.retainAll(anotherUserFriends);

        return new ArrayList<>(userFriendsSet);
    }

    @Override
    public List<User> getPendingRequests(int userId) {
        return jdbcTemplate.query("SELECT u.* " +
                        "FROM users u " +
                        "JOIN friendship f ON f.user_id = u.id " +
                        "WHERE f.another_user_id = ? AND f.is_accepted = FALSE",
                new Object[]{userId}, new BeanPropertyRowMapper<>(User.class));
    }

    @Override
    public void accept(int userId, int anotherUserId, boolean isAccepted) {
        jdbcTemplate.update("UPDATE friendship SET is_accepted = ? WHERE user_id = ? AND another_user_id = ?",
                isAccepted, userId, anotherUserId);
    }

    @Override
    public void like(int userId, int filmId) {
        jdbcTemplate.update("INSERT INTO likes (user_id, film_id) VALUES (?, ?)",
                userId, filmId);
    }

    @Override
    public void dislike(int userId, int filmId) {
        jdbcTemplate.update("DELETE FROM likes WHERE user_id = ? AND film_id = ?",
                userId, filmId);
    }

    @Override
    public boolean hasLiked(int userId, int filmId) {
        String sql = "SELECT EXISTS (SELECT 1 FROM likes WHERE user_id = ? AND film_id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, userId, filmId);
    }
}
package by.nuray.filmorate3.dao;

import by.nuray.filmorate3.models.Film;
import by.nuray.filmorate3.storage.FilmStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Film> getById(int id) {
        return jdbcTemplate.query("SELECT * FROM film WHERE id = ?", new Object[]{id},
                new BeanPropertyRowMapper<>(Film.class)).stream().findFirst();
    }

    @Override
    public List<Film> getAllFilms() {
        return jdbcTemplate.query("SELECT * FROM film",
                new BeanPropertyRowMapper<>(Film.class));
    }

    @Override
    public void save(Film film) {
        jdbcTemplate.update("INSERT INTO Film(name,description,release_date,duration) VALUES (?,?,?,?)",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
    }

    @Override
    public void update(int id, Film film) {
        jdbcTemplate.update("UPDATE film SET name=?,description=?,release_date=?,duration=? WHERE id =?",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), id);

    }

    @Override
    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM film WHERE id =?", id);
    }


    @Override
    public List<Film> getPopularFilms(int count) {

        return jdbcTemplate.query("SELECT f.* FROM Film f " +
                "JOIN Likes l ON f.id = l.film_id " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(l.film_id) DESC " +
                "LIMIT ?", new Object[]{count}, new BeanPropertyRowMapper<>(Film.class));

    }


}

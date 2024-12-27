package by.nuray.filmorate3.storage;

import by.nuray.filmorate3.models.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    public Optional<Film> getById(int  id);

    public List<Film> getAllFilms();

    public void save(Film film);
    public void update(int id, Film film);

    public void delete(int id);



    public List<Film> getPopularFilms(int count);
}

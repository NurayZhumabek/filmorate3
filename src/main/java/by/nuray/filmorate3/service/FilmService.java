package by.nuray.filmorate3.service;


import by.nuray.filmorate3.models.Film;
import by.nuray.filmorate3.storage.FilmStorage;
import by.nuray.filmorate3.util.FilmNotCreatedException;
import by.nuray.filmorate3.util.FilmNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(int id) {
        return filmStorage.getById(id).orElseThrow(() -> new FilmNotFoundException("Film not found"));
    }

    public void save(Film film) {

        if (film == null) {
            throw new FilmNotCreatedException("Film not created");
        }
        filmStorage.save(film);
    }

    public void update(int id, Film film) {

        getFilmById(id);
        filmStorage.update(id, film);

    }

    public void delete(int id) {

        getFilmById(id);
        filmStorage.delete(id);

    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);

    }
}

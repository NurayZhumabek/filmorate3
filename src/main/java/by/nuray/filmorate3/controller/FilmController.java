package by.nuray.filmorate3.controller;


import by.nuray.filmorate3.models.Film;
import by.nuray.filmorate3.service.FilmService;
import by.nuray.filmorate3.util.FilmErrorResponse;
import by.nuray.filmorate3.util.FilmNotCreatedException;
import by.nuray.filmorate3.util.FilmNotFoundException;
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
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping()
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();

    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable("id") int id) {
        try {
            return new ResponseEntity<>(filmService.getFilmById(id), HttpStatus.OK);
        } catch (FilmNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping()
    public ResponseEntity<?> createFilm(@RequestBody @Valid Film film, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("\n");
            }
            return new ResponseEntity<>(errors.toString(), HttpStatus.BAD_REQUEST);
        }
        try {
            filmService.save(film);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (FilmNotCreatedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping()
    public ResponseEntity<?> updateFilm(@RequestBody @Valid Film film, BindingResult bindingResult) {

        try {
            filmService.getFilmById(film.getId());
        } catch (FilmNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film not found", e);
        }

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .collect(Collectors.toList());
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        filmService.update(film.getId(), film);
        return new ResponseEntity<>(film, HttpStatus.OK);

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFilm(@PathVariable("id") int id) {
        try {
            filmService.delete(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (FilmNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count",defaultValue = "3") int count) {

        if (count <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The value of count must be greater than 0");
        }
        return filmService.getPopularFilms(count);

    }


    @ExceptionHandler
    public ResponseEntity<FilmErrorResponse> handleException(FilmNotFoundException ex) {
        FilmErrorResponse err=new FilmErrorResponse(
                ex.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler
    public ResponseEntity<FilmErrorResponse> handleException(FilmNotCreatedException ex) {
        FilmErrorResponse err=new FilmErrorResponse(
                ex.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }

}

package dao;

import model.game.Genre;

public class GenreDAO extends GenericDAO<Genre> {

    public GenreDAO() {
        super(Genre.class);
    }
}

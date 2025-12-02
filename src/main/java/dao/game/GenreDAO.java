package dao.game;

import model.game.Genre;
import dao.GenericDAO;

public class GenreDAO extends GenericDAO<Genre> {

    public GenreDAO() {
        super(Genre.class);
    }
}

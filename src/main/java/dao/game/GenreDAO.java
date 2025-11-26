package dao.game;

import dao.GenericDAO;
import model.game.Genre;

public class GenreDAO extends GenericDAO<Genre> {

    public GenreDAO() {
        super(Genre.class);
    }
}

package dao;

import javax.persistence.EntityManager;
import model.Genre;

public class GenreDAO extends GenericDAO<Genre, Long> {

    // Constructor
    public GenreDAO(EntityManager em) {
        super(em);
    }
}

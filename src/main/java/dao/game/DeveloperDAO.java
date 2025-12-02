package dao.game;

import model.game.Developer;
import dao.GenericDAO;

public class DeveloperDAO extends GenericDAO<Developer> {

    public DeveloperDAO() {
        super(Developer.class);
    }
}
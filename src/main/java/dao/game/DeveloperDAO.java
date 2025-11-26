package dao.game;

import dao.GenericDAO;
import model.game.Developer;

public class DeveloperDAO extends GenericDAO<Developer> {

    public DeveloperDAO() {
        super(Developer.class);
    }
}
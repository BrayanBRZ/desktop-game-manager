package dao.game;

import model.game.Platform;
import dao.GenericDAO;

public class PlatformDAO extends GenericDAO<Platform> {

    public PlatformDAO() {
        super(Platform.class);
    }
}

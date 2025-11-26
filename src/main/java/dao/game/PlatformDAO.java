package dao.game;

import dao.GenericDAO;
import model.game.Platform;

public class PlatformDAO extends GenericDAO<Platform> {

    public PlatformDAO() {
        super(Platform.class);
    }
}

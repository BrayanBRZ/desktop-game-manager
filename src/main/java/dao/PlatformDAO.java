package dao;

import javax.persistence.EntityManager;
import model.Platform;

public class PlatformDAO extends GenericDAO<Platform, Long> {

    // Constructor
    public PlatformDAO(EntityManager em) {
        super(em);
    }
}

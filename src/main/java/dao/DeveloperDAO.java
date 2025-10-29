package dao;

import javax.persistence.EntityManager;
import model.Developer;

public class DeveloperDAO extends GenericDAO<Developer, Long> {

    // Constructor
    public DeveloperDAO(EntityManager em) {
        super(em);
    }
}
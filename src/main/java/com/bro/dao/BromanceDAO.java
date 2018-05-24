package com.bro.dao;

import com.bro.entity.Bromance;
import com.bro.entity.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.dao.BasicDAO;

import java.util.Optional;


public class BromanceDAO  extends BasicDAO<Bromance, ObjectId> {
    public BromanceDAO(Class<Bromance> entityClass, Datastore ds) { super(entityClass, ds); }


    public Key<Bromance> save(User firstBro, User secondBro) {
        //@TODO: verification que le sender n'est pas la liste des bloqués du receiver avant
        Bromance bromance = new Bromance(firstBro, secondBro);
        return super.save(bromance);
    }

    public Optional<Bromance> getBromance(Bromance bromance) {
        return createQuery()
                .field("_id").equal(bromance.getId())
                .asList().stream().findAny();
    }
}

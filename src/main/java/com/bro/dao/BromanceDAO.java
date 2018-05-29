package com.bro.dao;

import com.bro.entity.Bromance;
import com.bro.entity.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.dao.BasicDAO;

import javax.management.Query;
import java.util.Optional;


public class BromanceDAO  extends BasicDAO<Bromance, ObjectId> {

    public BromanceDAO(Datastore ds) { super(ds); }

    public Key<Bromance> save(User sender, User receiver) {

        Optional<User> R = getDatastore().createQuery(User.class)
                .field("token").equal(receiver.getToken())
                .asList().stream().findAny();

        if(R.isPresent() && !R.get().getEnemies().contains(sender)){
            Bromance bromance = new Bromance(sender, receiver);
            return super.save(bromance);
        }
        return null;
    }

    public Optional<Bromance> getBromance(Bromance bromance) {
        return createQuery()
                .field("_id").equal(bromance.getId())
                .asList().stream().findAny();
    }
}

package com.bro.dao;

import com.bro.entity.Brotherhood;
import com.bro.entity.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.dao.BasicDAO;

import java.util.Optional;


public class BrotherhoodDAO extends BasicDAO<Brotherhood, ObjectId> {

    public BrotherhoodDAO(Datastore ds) { super(ds); }

    public Key<Brotherhood> save(User sender, User receiver) {

        Optional<User> R = getDatastore().createQuery(User.class)
                .field("token").equal(receiver.getToken())
                .asList().stream().findAny();

        if(R.isPresent() && !R.get().getEnemies().contains(sender)){
            Brotherhood brotherhood = new Brotherhood(sender, receiver);
            return super.save(brotherhood);
        }
        return null;
    }

    public Optional<Brotherhood> getBrotherhood(Brotherhood brotherhood) {
        return createQuery()
                .field("_id").equal(brotherhood.getId())
                .asList().stream().findAny();
    }
}

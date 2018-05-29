package com.bro.dao;

import com.bro.entity.Brotherhood;
import com.bro.entity.User;
import com.sun.xml.internal.bind.v2.TODO;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Criteria;
import org.mongodb.morphia.query.Query;

import java.util.List;
import java.util.Optional;


public class BrotherhoodDAO extends BasicDAO<Brotherhood, ObjectId> {

    public BrotherhoodDAO(Datastore ds) { super(ds); }

    public Key<Brotherhood> create(User sender, User receiver) {

        Optional<User> S = getDatastore().createQuery(User.class)
                .field("username").equal(sender.getUsername())
                .asList().stream().findAny();

        Optional<User> R = getDatastore().createQuery(User.class)
                .field("username").equal(receiver.getUsername())
                .asList().stream().findAny();

        if(R.isPresent() && S.isPresent()) {
            Brotherhood brotherhood = new Brotherhood(S.get(), R.get());
            return save(brotherhood);
        }
        return null;
    }

    // TODO : à compléter
    public Optional<Brotherhood> getBrotherhood(Brotherhood brotherhood, String token) {
        return null;
    }
}

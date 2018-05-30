package com.bro.dao;

import com.bro.entity.Brotherhood;
import com.bro.entity.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.dao.BasicDAO;
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

    public Optional<Brotherhood> getBrotherhood(Brotherhood brotherhood, String token) {

        Optional<User> user = getDatastore().createQuery(User.class)
                .field("token").equal(token)
                .asList().stream().findAny();

        if(user.isPresent()) {
            Query<Brotherhood> query_brotherhood = getDatastore().find(Brotherhood.class);
            query_brotherhood.field("_id").equal(brotherhood.getId());
            query_brotherhood.or(
                    query_brotherhood.criteria("sender").equal(user.get()),
                    query_brotherhood.criteria("receiver").equal(user.get()));

            return query_brotherhood.asList().stream().findAny();
        }
        return Optional.empty();
    }

    public List<Brotherhood> getBrotherhoods(String token) {

         Optional<User> user = getDatastore().createQuery(User.class)
         .field("token").equal(token)
         .asList().stream().findAny();

         if(user.isPresent()) {
             Query<Brotherhood> query_brotherhoods = getDatastore().find(Brotherhood.class);
             query_brotherhoods.or(
                 query_brotherhoods.criteria("sender").equal(user.get()),
                 query_brotherhoods.criteria("receiver").equal(user.get()));

             return query_brotherhoods.asList();
         }
         return null;
    }
}

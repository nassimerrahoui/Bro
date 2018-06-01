package com.bro.dao;

import com.bro.entity.Brotherhood;
import com.bro.entity.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class BrotherhoodDAO extends BasicDAO<Brotherhood, ObjectId> {

    public BrotherhoodDAO(Datastore ds) { super(ds); }

    /** Crée une brotherhood si elle n'existe pas **/
    public Key<Brotherhood> create(User sender, User receiver) {

        Optional<User> S = getDatastore().createQuery(User.class)
                .field("username").equal(sender.getUsername())
                .asList().stream().findAny();

        Optional<User> R = getDatastore().createQuery(User.class)
                .field("username").equal(receiver.getUsername())
                .asList().stream().findAny();

        if(R.isPresent() && S.isPresent()) {

            Query<Brotherhood> query_brotherhood = getDatastore().find(Brotherhood.class);
            query_brotherhood.or(
                    query_brotherhood.and(
                            query_brotherhood.criteria("sender").equal(S.get()),
                            query_brotherhood.criteria("receiver").equal(R.get())
                    ),
                    query_brotherhood.and(
                            query_brotherhood.criteria("sender").equal(R.get()),
                            query_brotherhood.criteria("receiver").equal(S.get())
                    )
            );

            if(query_brotherhood.asList().isEmpty()) {
                Brotherhood brotherhood = new Brotherhood(S.get(), R.get());
                return save(brotherhood);
            }
        }
        return null;
    }

    /** Retourne une brotherhood pour un user et son bro **/
    public Brotherhood getBrotherhood(User user, User bro) {

        Optional<User> u = getDatastore().createQuery(User.class)
                .field("username").equal(user.getUsername())
                .asList().stream().findAny();

        Optional<User> b = getDatastore().createQuery(User.class)
                .field("username").equal(bro.getUsername())
                .asList().stream().findAny();

        if(u.isPresent() && b.isPresent()) {
            Query<Brotherhood> query_brotherhood = getDatastore().find(Brotherhood.class);
            query_brotherhood.and(
                    query_brotherhood.or(
                            query_brotherhood.criteria("sender").equal(u.get()),
                            query_brotherhood.criteria("receiver").equal(u.get())),
                    query_brotherhood.or(
                            query_brotherhood.criteria("sender").equal(b.get()),
                            query_brotherhood.criteria("receiver").equal(b.get()))
            );
            return query_brotherhood.get();
        }
        return null;
    }

    /** Accepte une brotherhood **/
    public UpdateResults accept(Brotherhood brotherhood){
        Query<Brotherhood> query = getDatastore().find(Brotherhood.class);
        query.field("_id").equal(brotherhood.getId());

        UpdateOperations<Brotherhood> ops = getDatastore()
                .createUpdateOperations(Brotherhood.class)
                .set("brolationship", Brotherhood.Brolationship.ACCEPTED);
        return update(query, ops);
    }

    /** Décline une brotherhood **/
    public UpdateResults deny(Brotherhood brotherhood){
        Query<Brotherhood> query = getDatastore().find(Brotherhood.class);
        query.field("_id").equal(brotherhood.getId());

        UpdateOperations<Brotherhood> ops = getDatastore()
                .createUpdateOperations(Brotherhood.class)
                .set("brolationship", Brotherhood.Brolationship.DENIED);
        return update(query, ops);
    }

    /** Retourne la liste des brotherhoods **/
    public List<User> getBrotherhoods(String token) {

         Optional<User> user = getDatastore().createQuery(User.class)
         .field("token").equal(token)
         .asList().stream().findAny();

         if(user.isPresent()) {
             Query<Brotherhood> query_brotherhoods = getDatastore().find(Brotherhood.class);
             query_brotherhoods.or(
                 query_brotherhoods.criteria("sender").equal(user.get()),
                 query_brotherhoods.criteria("receiver").equal(user.get()));

             if(!query_brotherhoods.asList().isEmpty()){
                 List<User> bros = new ArrayList<>();
                 for(Brotherhood b : query_brotherhoods.asList()) {
                     if(b.getSender().getUsername().equals(user.get().getUsername())){
                         bros.add(b.getReceiver());
                     }
                     else {
                         bros.add(b.getSender());
                     }
                 }
                 return bros;
             }
         }
         return null;
    }
}

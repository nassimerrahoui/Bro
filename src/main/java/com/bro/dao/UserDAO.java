package com.bro.dao;


import java.util.Optional;
import java.util.UUID;
import com.bro.entity.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

public class UserDAO extends BasicDAO<User, ObjectId> {
    public UserDAO(Datastore ds) {
        super( ds);
    }

    public Optional<User> getUser(String token){
        return createQuery()
                .field("token").equal(token)
                .asList().stream().findAny();
    }

    public Optional<User> getUserByEmail(String email){
        return createQuery()
                .field("email").equal(email)
                .asList().stream().findAny();
    }

    public Boolean emailExists(String email){
        return createQuery()
                .field("email").equal(email)
                .asList().stream().findAny().isPresent();
    }


    public String authenticate(String email, String password){
        Query<User> query = createQuery().
                field("email").equal(email).
                field("password").equal(password);

        Boolean passed = !query.asList().isEmpty();

        if(passed) {
            String token = UUID.randomUUID().toString();
            UpdateOperations<User> ops = getDatastore()
                    .createUpdateOperations(User.class)
                    .set("token", token);
            update(query, ops);
            return token;
        }
        return "";
    }

    public void logout(String token){
        Query<User> query = createQuery().
                field("token").equal(token);

        UpdateOperations<User> ops = getDatastore()
                .createUpdateOperations(User.class)
                .set("token", "");
        update(query, ops);
    }

    public UpdateResults updateUser(User user){

        Query<User> query = createQuery()
                .field("token").equal(user.getToken());

        UpdateOperations<User> ops = getDatastore()
                .createUpdateOperations(User.class)
                .set("username", user.getUsername())
                .set("firstName", user.getFirstName())
                .set("lastName", user.getLastName())
                .set("email", user.getEmail())
                .set("password", user.getPassword());
        return update(query, ops);
    }

    // @TODO: add and remove ennemy
}


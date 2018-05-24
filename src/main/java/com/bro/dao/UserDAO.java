package com.bro.dao;


import java.util.Optional;
import java.util.UUID;

import com.bro.entity.Brotherhood;
import com.bro.entity.User;
import com.mongodb.WriteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

public class UserDAO extends BasicDAO<User, ObjectId> {
    public UserDAO(Class<User> entityClass, Datastore ds) {
        super(entityClass, ds);
    }

    public Optional<User> getUser(String token){
        return createQuery()
                .field("token").equal(token)
                .asList().stream().findAny();
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

    public UpdateResults logout(String user){
        Query<User> query = createQuery().
                field("_id").equal(user);

        String token = "";
        UpdateOperations<User> ops = getDatastore()
                .createUpdateOperations(User.class)
                .set("token", token);
        return update(query, ops);
    }

    public UpdateResults updateUser(User user){

        Query<User> query = createQuery()
                .field("_id").equal(user.getId());

        UpdateOperations<User> ops = getDatastore()
                .createUpdateOperations(User.class)
                .set("firstName", user.getFirstName())
                .set("lastName", user.getLastName())
                .set("email", user.getEmail())
                .set("password", user.getPassword());
        return update(query, ops);
    }
}


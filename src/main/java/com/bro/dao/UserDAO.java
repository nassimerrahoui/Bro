package com.bro.dao;


import java.util.Optional;
import java.util.UUID;

import com.bro.entity.Brotherhood;
import com.bro.entity.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

public class UserDAO extends BasicDAO<User, ObjectId> {
    public UserDAO(Datastore ds) {
        super(ds);
    }

    public Optional<User> getUserById(String userID) {
        return createQuery()
                .field("_id").equal(userID)
                .asList().stream().findAny();
    }

    /**
     * Constructeur
     *
     * @param token
     * @return Optional<User>
     */
    public Optional<User> getUser(String token) {
        return createQuery()
                .field("token").equal(token)
                .asList().stream().findAny();
    }

    /**
     * checks if email exists
     *
     * @param email
     * @return Boolean
     */
    public Boolean emailExists(String email) {
        return createQuery()
                .field("email").equal(email)
                .asList().stream().findAny().isPresent();
    }


    /**
     * Authenticate
     *
     * @param email
     * @param password
     * @return String, token
     */
    public String authenticate(String email, String password) {
        Query<User> query = createQuery().
                field("email").equal(email).
                field("password").equal(password);

        Boolean passed = !query.asList().isEmpty();

        if (passed) {
            String token = UUID.randomUUID().toString();
            UpdateOperations<User> ops = getDatastore()
                    .createUpdateOperations(User.class)
                    .set("token", token);
            update(query, ops);
            return token;
        }
        return "";
    }

    /**
     * Logout and removes token
     *
     * @param token
     */
    public void logout(String token) {
        Query<User> query = createQuery().
                field("token").equal(token);

        String empty = "";
        UpdateOperations<User> ops = getDatastore()
                .createUpdateOperations(User.class)
                .set("token", empty);
        update(query, ops);
    }


    /**
     * Updates a user
     *
     * @param user
     * @return UpdateResults
     */
    public UpdateResults updateUser(User user) {

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

    // TODO : chercher l'enemie avec son username
    /**
     * Ajout enemy dans la liste enemies et suppression de la bromance
     **/
    public void addEnemy(User user, User enemy) {

        Query<User> query = createQuery()
                .field("token").equal(user.getToken());
        UpdateOperations<User> ops = getDatastore()
                .createUpdateOperations(User.class)
                .push("enemies", enemy);
        update(query, ops);
        Query<Brotherhood> bromance = getDatastore().createQuery(Brotherhood.class)
                .field("sender").equal(user)
                .field("receiver").equal(enemy);
        getDatastore().delete(bromance);
    }

    // TODO : chercher le bro avec son username
    /**
     * Suppression du bro dans la liste enemies
     **/
    public void deleteEnemy(User user, User bro) {

        Query<User> query = createQuery()
                .field("token").equal(user.getToken());
        UpdateOperations<User> ops = getDatastore()
                .createUpdateOperations(User.class)
                .removeAll("enemies", bro);
        update(query, ops);
    }
}


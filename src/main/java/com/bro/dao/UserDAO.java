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

    /**
     * Gets an user with a given token
     *
     * @param token token associated to targeted user
     * @return User
     */
    public Optional<User> getUser(String token) {
        return createQuery()
                .field("token").equal(token)
                .asList().stream().findAny();
    }

    /**
     * Gets an user with a given username
     *
     * @param username username associated to targeted user
     * @return User
     */
    public Optional<User> getUserByUsername(String username) {
        return createQuery()
                .field("username").equal(username)
                .asList().stream().findAny();
    }

    /**
     * Gets an user with a given email
     *
     * @param email email associated to targeted user
     * @return User
     */
    public Optional<User> getUserByEmail(String email) {
        return createQuery()
                .field("email").equal(email)
                .asList().stream().findAny();
    }


    /**
     * Checks if an email is already associate with an user
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
     * Generates and sets a token if given email or username is associated to given encrypted password
     *
     * @param emailOrUsername email of user
     * @param password        encrypted password
     * @return generated token or empty string
     */
    public String authenticate(String emailOrUsername, String password) {
        Query<User> query = createQuery().field("password").equal(password);
        query.or(
                query.criteria("email").equal(emailOrUsername),
                query.criteria("username").equal(emailOrUsername)
        );
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
     * Disconnects an user and removes is associated token
     *
     * @param token token of user to be disconnect
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
     * Updates user data
     *
     * @param user an user
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

    /**
     * Add a enemy relationship and removes brotherhood if it exists
     *
     * @param user user who add enemy
     * @param enemy user to be add to enemy list
     */
    public void addEnemy(User user, User enemy) {

        boolean enemyDoesntExists = getDatastore().find(User.class)
                .field("enemies").equal(enemy).asList().isEmpty();

        if (enemyDoesntExists){
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
    }

    /**
     * Removes an enemy
     *
     * @param user     an user
     * @param enemy username of enemy to be removed
     */
    public void removeEnemy(User user, User enemy) {

        Query<User> query = createQuery()
                .field("token").equal(user.getToken());
        UpdateOperations<User> ops = getDatastore()
                .createUpdateOperations(User.class)
                .removeAll("enemies", enemy);
        update(query, ops);
    }

    /**
     * Enables localization
     *
     * @param user an user
     */
    public void raising(User user) {

        Query<User> query = createQuery()
                .field("token").equal(user.getToken());
        UpdateOperations<User> ops = getDatastore()
                .createUpdateOperations(User.class)
                .removeAll("isLocation", true);
        update(query, ops);
    }

    /**
     * Disables localization
     *
     * @param user an user
     */
    public void shadow(User user) {

        Query<User> query = createQuery()
                .field("token").equal(user.getToken());
        UpdateOperations<User> ops = getDatastore()
                .createUpdateOperations(User.class)
                .removeAll("isLocation", false);
        update(query, ops);
    }
}


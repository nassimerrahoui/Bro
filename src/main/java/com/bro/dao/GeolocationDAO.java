package com.bro.dao;

import com.bro.entity.Geolocation;
import com.bro.entity.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.FindOptions;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import javax.swing.text.html.Option;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class GeolocationDAO extends BasicDAO<Geolocation, ObjectId> {

    public GeolocationDAO(Datastore ds) {
        super(ds);
    }

    /**
     * Saves a GPS position at current time
     *
     * @param geo Geolocation object
     * @return Key<T> statement or null
     */
    public Key<Geolocation> create(Geolocation geo, String token) {
        Optional<User> user = getDatastore().createQuery(User.class)
                .field("token").equal(token)
                .asList().stream().findAny();
        if (user.isPresent()) {
            geo.setUser(user.get());
            return save(geo);
        }
        return null;
    }

    /**
     * Takes a token and a number n and returns n last geolocations
     * associated with this user using the timestamp creation
     *
     * @param username a username
     * @param nbGeo number of geolocation to be returned
     * @return List<Geolocation>
     */
    public List<Geolocation> getNLastLocations(String username, int nbGeo) {
        Optional<User> userQuery = getDatastore().createQuery(User.class)
                .field("username").equal(username)
                .asList().stream().findAny();

        if (!userQuery.isPresent()) {
            return null;
        }

        Query<Geolocation> geolocationQuery = getDatastore().find(Geolocation.class);
        geolocationQuery.criteria("user").equal(userQuery.get());
        return geolocationQuery.order("-_id").asList(new FindOptions().limit(nbGeo));
    }

    /**
     * Gets last known location for a given user through his token
     *
     * @param username a username
     * @return Geolocation
     */
    public Optional<Geolocation> getLastLocation(String username) {
        return Optional.ofNullable(this.getNLastLocations(username, 1).get(0));
    }


    /**
     * Gets GPS distance between two users by their username
     *
     * @param user  an user
     * @param user2 a second user
     * @return double distance or NaN
     */
    public Double getDistance(User user, User user2) {
        Optional<Geolocation> geo = getLastLocation(user.getUsername());
        Optional<Geolocation> geo2 = getLastLocation(user2.getUsername());
        if (!geo.isPresent()|| !geo2.isPresent()){
            return Double.NaN;
        }
        double latUser = (geo.get().getLat() * Math.PI / 180);
        double lngUser = (geo.get().getLng() * Math.PI / 180);
        double latUser2 = (geo2.get().getLat() * Math.PI / 180);
        double lngUser2 = (geo2.get().getLng() * Math.PI / 180);
        double dlong = (lngUser2 - lngUser);
        double dlat = (latUser2 - latUser);

        // formule Haversine:
        int R = 6371;
        double a = (Math.sin(dlat / 2) * Math.sin(dlat / 2) + Math.cos(latUser) * Math.cos(latUser2) * Math.sin(dlong / 2) * Math.sin(dlong / 2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * Takes an user and his bros. Then calculates distance in km
     * between each of them who activated isLocalizable option.
     *
     * @param user an user
     * @param bros  a list of broship
     * @return HashMap<String ,   Double>
     */
    public HashMap<String, Double> getBrosDistance(User user, List<User> bros) {
        HashMap<String, Double> mapDistance = new HashMap<>();
            for (User bro : bros) {
                if (user.isLocalizable() &&
                        bro.isLocalizable() &&
                        !this.getDistance(user, bro).isNaN()) {
                    mapDistance.put(bro.getUsername(), this.getDistance(user, bro));
                }

            }
        return mapDistance;
    }


    /**
     * Takes an user and his bros. Then calculates distance in km
     * between each of them who activated isLocalizable option.
     *
     * @param token a token of an user
     * @param bros  a list of broship
     */
    public HashMap<String, Geolocation> getBrosPositions(String token, List<User> bros) {
        HashMap<String, Geolocation> mapPositions = new HashMap<String, Geolocation>();
        Optional<User> userQuery = getDatastore().createQuery(User.class)
                .field("token").equal(token)
                .asList().stream().findAny();

        if (userQuery.isPresent()) {
            for (User bro : bros) {
                Optional<Geolocation> location = getLastLocation(bro.getUsername());
                if (location.isPresent()){
                    mapPositions.put(bro.getUsername(), location.get());
                }

            }
        }
        return mapPositions;
    }
}

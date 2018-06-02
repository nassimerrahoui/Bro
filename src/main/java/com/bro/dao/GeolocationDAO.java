package com.bro.dao;

import com.bro.entity.Geolocation;
import com.bro.entity.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.FindOptions;
import org.mongodb.morphia.query.Query;

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
    public Key<Geolocation> create(Geolocation geo) {
        Optional<User> user = getDatastore().createQuery(User.class)
                .field("username").equal(geo.getUser().getUsername())
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
     * @param token a token
     * @param nbGeo number of geolocation to be returned
     * @return List<Geolocation>
     */
    public List<Geolocation> getNLastLocations(String token, int nbGeo) {
        Optional<User> userQuery = getDatastore().createQuery(User.class)
                .field("token").equal(token)
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
     * @param token
     * @return Geolocation
     */
    public Geolocation getLastLocation(String token) {
        return this.getNLastLocations(token, 1).get(0);
    }


    /**
     * Gets GPS distance between two users by their username
     *
     * @param username  an username
     * @param username2 a second username
     * @return double distance or NaN
     */
    public Double getDistance(String username, String username2) {
        Optional<User> userQuery = getDatastore().createQuery(User.class)
                .field("username").equal(username)
                .asList().stream().findAny();

        Optional<User> user2Query = getDatastore().createQuery(User.class)
                .field("username").equal(username2)
                .asList().stream().findAny();

        if (userQuery.isPresent() && user2Query.isPresent()) {

            Geolocation geo = getLastLocation(userQuery.get().getToken());
            Geolocation geo2 = getLastLocation(user2Query.get().getToken());

            double latUser = (geo.getLat() * Math.PI / 180);
            double lngUser = (geo.getLng() * Math.PI / 180);
            double latUser2 = (geo2.getLat() * Math.PI / 180);
            double lngUser2 = (geo2.getLng() * Math.PI / 180);
            double dlong = (lngUser2 - lngUser);
            double dlat = (latUser2 - latUser);

            // formule Haversine:
            int R = 6371;
            double a = (Math.sin(dlat / 2) * Math.sin(dlat / 2) + Math.cos(latUser) * Math.cos(latUser2) * Math.sin(dlong / 2) * Math.sin(dlong / 2));
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            return R * c;
        }
        return null;
    }

    /**
     * Takes an user and his bros. Then calculates distance in km
     * between each of them who activated isLocalizable option.
     *
     * @param token a token of an user
     * @param bros  a list of broship
     * @return HashMap<String                                                                                                                                                                                                                                                               ,                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               Double> key: bro username, value: distance from main user.
     * @TODO: corriger le bug des isLocalizable() toujours à false
     */
    public HashMap<String, Double> getBrosDistance(String token, List<User> bros) {


        HashMap<String, Double> mapDistance = new HashMap<String, Double>();
        Optional<User> userQuery = getDatastore().createQuery(User.class)
                .field("token").equal(token)
                .asList().stream().findAny();
        if (userQuery.isPresent()) {
            for (User bro : bros) {
                if (userQuery.get().isLocalizable() &&
                        bro.isLocalizable() &&
                        this.getDistance(userQuery.get().getUsername(), bro.getUsername()) != null) {
                    mapDistance.put(bro.getUsername(), this.getDistance(userQuery.get().getUsername(), bro.getUsername()));
                }

            }
        }
        return mapDistance;
    }


}

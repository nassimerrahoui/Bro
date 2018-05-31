package com.bro.dao;

import com.bro.entity.Geolocation;
import com.bro.entity.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.FindOptions;

import java.util.List;
import java.util.Optional;

import static java.lang.Double.NaN;

public class GeolocationDAO extends BasicDAO<Geolocation, ObjectId> {

    public GeolocationDAO(Datastore ds) {
        super(ds);
    }


    public Key<Geolocation> create(Geolocation geo) {
        Optional<User> user = getDatastore().createQuery(User.class)
                .field("username").equal(geo.getUser().getUsername())
                .asList().stream().findAny();
        if(user.isPresent()){
            geo.setUser(user.get());
            geo.updateTimestamp();
            return save(geo);
        }
        return null;
    }

    public List<Geolocation> getLastLocation(String token){

        return createQuery()
                .filter("user.token", token)
                .asList(new FindOptions().limit(100));
    }

    public double getDistance(String username, String username2){
        System.out.println("try");
        Optional<User> userQuery = getDatastore().createQuery(User.class)
                .field("username").equal(username)
                .asList().stream().findAny();

        Optional<User> user2Query = getDatastore().createQuery(User.class)
                .field("username").equal(username2)
                .asList().stream().findAny();

        if(!(userQuery.isPresent() && user2Query.isPresent()) ){
            System.out.println("Le premier ou le second user n'est pas présent");
            return NaN;
        }
        User user = userQuery.get();
        User user2 = user2Query.get();

        Geolocation geo = getDatastore().createQuery(Geolocation.class)
                .filter("user.username", user.getUsername()).get();
        Geolocation geo2 = getDatastore().createQuery(Geolocation.class)
                .filter("user.username", user2.getUsername()).get();

        double latUser = (geo.getLat() * Math.PI/180);
        double lngUser = (geo.getLng() * Math.PI/180);
        double latUser2 = (geo2.getLat() * Math.PI/180);
        double lngUser2 = (geo2.getLng()* Math.PI/180);

        double dlong = (lngUser2 - lngUser);
        double dlat = (latUser2 - latUser);
        System.out.println(dlat);

        // formule Haversine:
        int R = 6371;
        double a = (Math.sin(dlat/2)*Math.sin(dlat/2) + Math.cos(latUser)*Math.cos(latUser2)*Math.sin(dlong/2)*Math.sin(dlong/2));
        double c = 2 * Math.atan2( Math.sqrt(a), Math.sqrt(1-a) );
        return R * c;

    }


}

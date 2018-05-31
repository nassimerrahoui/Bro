package com.bro.dao;

import com.bro.entity.Geolocation;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.FindOptions;

import java.util.List;

public class GeolocationDAO extends BasicDAO<Geolocation, ObjectId> {

    public GeolocationDAO(Datastore ds) {
        super(ds);
    }

    public List<Geolocation> getLastLocation(String token){

        return createQuery()
                .filter("user.token", token)
                .asList(new FindOptions().limit(100));
    }
}

package com.bro.dao;

import com.bro.entity.Bromance;
import com.bro.entity.Geolocation;
import com.bro.entity.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.FindOptions;
import org.mongodb.morphia.query.Query;

import java.util.List;
import java.util.Optional;

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

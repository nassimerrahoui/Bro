package com.bro.dao;

import com.bro.entity.Geolocation;
import com.bro.entity.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import java.util.Optional;

public class GeolocationDAO extends BasicDAO<Geolocation, ObjectId> {
    public GeolocationDAO(Class<Geolocation> entityClass, Datastore ds) {
        super(entityClass, ds);
    }

    public Optional<Geolocation> getLocation(User user){
        return createQuery()
                .field("user").equal(user)
                .asList().stream().findAny();
    }
}

package com.bro.dao;

import com.bro.entity.Bromance;
import com.bro.entity.Brotherhood;
import com.bro.entity.User;
import com.mongodb.WriteResult;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BrotherhoodDAO extends BasicDAO<Brotherhood, ObjectId> {

    public BrotherhoodDAO(Datastore ds) {
        super(ds);
    }

    /**
     * @TODO brotherhood: à déplacer dans BrotherhoodManager
     * @param user
     * @return
     */
    public List<Brotherhood> getBrotherhoods(String user){
        return createQuery()
                .field("users").hasThisOne(user)
                .asList();
    }

    /**
     * @TODO brotherhood: à déplacer dans BrotherhoodManager
     * @param name
     * @param user
     * @return
     */
    public Optional<Brotherhood> getBrotherhood(String name, User user){
        return createQuery()
                .field("name").equal(name)
                .field("users.User").hasThisOne(user)
                .asList().stream().findAny();
    }

    /**
     * @TODO brotherhood: à deplacer dans BrotherhoodManager
     * @param brotherhood
     * @param user
     * @return
     */
    public UpdateResults addBro(Brotherhood brotherhood, User user){
        Query<Brotherhood> query = createQuery()
                .field("_id").equal(brotherhood.getId());

        UpdateOperations<Brotherhood> ops = getDatastore()
                .createUpdateOperations(Brotherhood.class)
                .addToSet("users", Collections.singletonMap(false, user));

        return update(query, ops);
    }


    /**
     * Permet de supprimer un membre du groupe
     * @param brotherhood
     * @param user
     * @return
     * @TODO brotherhood: Supprimer le HashMap + Transfert BrotherhoodManager + empêcher de supprimer le leader
     */
    public UpdateResults removeBro(Brotherhood brotherhood, User user) {
        Query<Brotherhood> query = createQuery()
                .field("_id").equal(brotherhood.getId());


        UpdateOperations<Brotherhood> ops = getDatastore()
                .createUpdateOperations(Brotherhood.class)
                .removeAll("users", Collections.singleton(user));

        return update(query, ops);
    }
    // @TODO brotherhood: Modifier nom du groupe
}

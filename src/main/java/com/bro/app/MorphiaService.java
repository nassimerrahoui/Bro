package com.bro.app;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import com.mongodb.MongoClient;

/**
 * Manages morphia ORM that allows entity class mapping
 */
public class MorphiaService {

    private Morphia morphia;
    private Datastore datastore;

    MorphiaService() {

        // MongoClient pour se connecter sur le localhost
        // Lancer MongoDB avant de run l'application
        // port 27017 (port par défaut)
        MongoClient mongoClient = new MongoClient("127.0.0.1:27017");

        // création d'une instance DAO
        this.morphia = new Morphia();
        String databaseName = "bro";
        this.datastore = morphia.createDatastore(mongoClient, databaseName);
    }


    public Morphia getMorphia() {
        return morphia;
    }

    public void setMorphia(Morphia morphia) {
        this.morphia = morphia;
    }

    Datastore getDatastore() {
        return datastore;
    }
}
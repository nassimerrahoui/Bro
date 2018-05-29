package com.bro.app;


import com.bro.dao.GeolocationDAO;
import com.bro.entity.Geolocation;
import com.bro.entity.User;
import com.google.gson.Gson;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Key;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@Path("/geolocation")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GeolocationService {

    private GeolocationDAO geolocationDAO = new GeolocationDAO(BroApp.getDatastore());

    @POST
    @Path("/create")
    public Response create(Geolocation geolocation){

        Key<Geolocation> key = geolocationDAO.save(geolocation);
        if(key == null){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Path("{token}/history")
    public Response getLocationHistory(@PathParam("token") String token){

        List<Geolocation> history = geolocationDAO.getLastLocation(token);

        if(!history.isEmpty()) {
            return Response.status(Response.Status.OK).entity(history).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
package com.bro.app;


import com.bro.dao.GeolocationDAO;
import com.bro.entity.Geolocation;
import com.bro.entity.User;
import com.google.gson.JsonObject;
import org.mongodb.morphia.Key;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/geolocation")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GeolocationService {

    private GeolocationDAO geolocationDAO = new GeolocationDAO(BroApp.getDatastore());

    @POST
    @Path("/create")
    public Response create(Geolocation geolocation){
        Key<Geolocation> key = geolocationDAO.create(geolocation);
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


    @GET
    @Path("distance/{username}/{username2}")
    public Response getDistance(@PathParam("username") String username, @PathParam("username2") String username2){

        double distance = geolocationDAO.getDistance(username, username2);
        System.out.println(distance);
        try{
            JsonObject tokenJSON = new JsonObject();
            tokenJSON.addProperty("distance",distance);
            return Response.status(Response.Status.OK).entity(tokenJSON).build();
        }
        catch (Exception e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }

    }




}
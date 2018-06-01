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

/**
 * Manages GPS location of our bro'ths
 **/
@Path("/geolocation")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GeolocationService {

    private GeolocationDAO geolocationDAO = new GeolocationDAO(BroApp.getDatastore());

    /**
     * Creates a Geolocation into database
     *
     * @param geolocation Geolocation Data
     * @return Response HTTP Status
     */
    @POST
    @Path("/create")
    public Response create(Geolocation geolocation) {
        Key<Geolocation> key = geolocationDAO.create(geolocation);
        if (key == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.CREATED).build();
    }

    // TODO : A TESTER

    /**
     * Returns a history of 100 last GPS locations
     *
     * @param token a user token
     * @return Response HTTP Status
     */
    @GET
    @Path("/{token}/history")
    public Response getLocationHistory(@PathParam("token") String token) {

        List<Geolocation> history = geolocationDAO.getLastLocation(token);

        if (!history.isEmpty()) {
            return Response.status(Response.Status.OK).entity(history).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }


    /**
     * Takes two users and returns GPS distance between them
     *
     * @param bro1 a User
     * @param bro2 a User
     * @return JSON with property distance associated with GPS distance
     */
    @GET
    @Path("/distance")
    public Response getDistance(User bro1, User bro2) {

        double distance = geolocationDAO.getDistance(bro1.getUsername(), bro2.getUsername());
        System.out.println(distance);
        try {
            JsonObject tokenJSON = new JsonObject();
            tokenJSON.addProperty("distance", distance);
            return Response.status(Response.Status.OK).entity(tokenJSON).build();
        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

    }
}
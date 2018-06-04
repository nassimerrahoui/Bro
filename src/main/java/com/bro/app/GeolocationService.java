package com.bro.app;


import com.bro.dao.BrotherhoodDAO;
import com.bro.dao.GeolocationDAO;
import com.bro.entity.Brotherhood;
import com.bro.entity.Geolocation;
import com.bro.entity.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mongodb.morphia.Key;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * Manages GPS location of our bro'ths
 **/
@Path("/geolocation")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GeolocationService {

    private GeolocationDAO geolocationDAO = new GeolocationDAO(BroApp.getDatastore());
    private BrotherhoodDAO brotherhoodDAO = new BrotherhoodDAO(BroApp.getDatastore());

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

    /**
     * Returns a history of n last known GPS locations
     *
     * @param username an user token
     * @return Response HTTP Status
     */
    @GET
    @Path("/history")
    public Response getLocationHistory(@HeaderParam("username") String username, @HeaderParam("nbGeo") int nbGeo) {

        List<Geolocation> history = geolocationDAO.getNLastLocations(username, nbGeo);

        if (history.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        JsonArray array = new JsonArray();
        for (Geolocation geolocation : history) {
            JsonObject tokenJSON = new JsonObject();
            array.add(tokenJSON);
            tokenJSON.addProperty("user", geolocation.getUser().getUsername());
            tokenJSON.addProperty("lng", geolocation.getLng());
            tokenJSON.addProperty("lat", geolocation.getLat());
            tokenJSON.addProperty("timestamp", geolocation.getId().getTimestamp());
        }
        return Response.status(Response.Status.OK).entity(array).build();

    }

    /**
     * Takes two users and returns GPS distance between them
     *
     * @param users a list of User
     * @return JSON with property distance associated with GPS distance
     */
    @POST
    @Path("/distance")
    public Response getDistance(List<User> users) {
        double distance = geolocationDAO.getDistance(users.get(0).getUsername(), users.get(1).getUsername());
        System.out.println(distance);
        try {
            JsonObject tokenJSON = new JsonObject();
            tokenJSON.addProperty("distance", distance);
            return Response.status(Response.Status.OK).entity(tokenJSON).build();
        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

    }


    /**
     * Returns distance in km for each bro
     *
     * @param token
     * @return JSON with property distance associated with GPS distance for each bro
     */
    @GET
    @Path("/get_bros_distance")
    public Response getBrosDistance(@HeaderParam("token") String token) {
        List<User> bros = brotherhoodDAO.getBrotherhoods(token, Brotherhood.Brolationship.ACCEPTED);
        HashMap<String, Double> distance = geolocationDAO.getBrosDistance(token, bros);
        String resp = new Gson().toJson(distance);
        if (!distance.isEmpty()) {
            return Response.status(Response.Status.OK).entity(resp).build();
        }
        return Response.status(Response.Status.FORBIDDEN).build();
    }


    @GET
    @Path("/get_bro_locations")
    public Response getBrosPositions(@HeaderParam("token") String token) {
        List<User> bros = brotherhoodDAO.getBrotherhoods(token, Brotherhood.Brolationship.ACCEPTED);
        HashMap<String, Geolocation> locations = geolocationDAO.getBrosPositions(token, bros);

        Collection<JSONObject> items = new ArrayList<JSONObject>();
        for (Map.Entry location : locations.entrySet()) {
            Geolocation geo = (Geolocation) location.getValue();

            JSONObject jsonO = new JSONObject();
            jsonO.put("username", location.getKey().toString());
            jsonO.put("localizable", geo.getUser().isLocalizable());

            jsonO.put("position", new JSONObject()
                    .put("lat", geo.getLat())
                    .put("lng", geo.getLng()));
            items.add(jsonO);
        }

        if (!bros.isEmpty()) {
            return Response.status(Response.Status.OK).entity(items).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
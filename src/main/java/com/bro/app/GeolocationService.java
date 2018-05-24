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
import java.util.Optional;

@Path("/geolocation")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GeolocationService {

    private GeolocationDAO geolocationDAO = new GeolocationDAO(Geolocation.class, new MorphiaService().getDatastore());
    private String geolocationURL = "https://www.googleapis.com/geolocation/v1/geolocate?key=AIzaSyBnTPSFkjYo08YsOS1rev2xrS8CbWg1qxU";

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
    @Path("/test")
    public Response testGoogleMapApi() throws IOException {

        URL geolocationAPI = new URL(geolocationURL);
        HttpURLConnection connection = (HttpURLConnection) geolocationAPI.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        Gson g = new Gson();
        String mygeolocation = g.toJson("{\n" +
                "  \"considerIp\": \"false\",\n" +
                "  \"wifiAccessPoints\": [\n" +
                "    {\n" +
                "        \"macAddress\": \"00:25:9c:cf:1c:ac\",\n" +
                "        \"signalStrength\": -43,\n" +
                "        \"signalToNoiseRatio\": 0\n" +
                "    },\n" +
                "    {\n" +
                "        \"macAddress\": \"00:25:9c:cf:1c:ad\",\n" +
                "        \"signalStrength\": -55,\n" +
                "        \"signalToNoiseRatio\": 0\n" +
                "    }\n" +
                "  ]\n" +
                "}");

        DataOutputStream wr = new DataOutputStream (connection.getOutputStream());
        wr.writeBytes(mygeolocation);
        wr.close();

        InputStream is = connection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        try{
            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                return Response.ok(br).build();
            }
            else {
                return Response.status(connection.getResponseCode()).build();
            }
        }
        catch (Exception e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        finally {
            br.mark(0);
            br.reset();
        }
    }

    @GET
    @Path("/{user}")
    public Response getLocationOfBro(@PathParam("user") String user){
        return null;
    }
}
package com.bro.app;

import com.bro.dao.BrotherhoodDAO;
import com.bro.entity.Brotherhood;
import com.bro.entity.User;
import com.google.gson.Gson;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.UpdateResults;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Manage a friendship between two users
 */
@Path("/brotherhood")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BrotherhoodService {

    private BrotherhoodDAO brotherhoodDAO = new BrotherhoodDAO(BroApp.getDatastore());

    /**
     * Takes two users and create a brotherhood into database
     *
     * @param users a list of user
     * @return Response HTTP status
     */
    @POST
    @Path("/create")
    public Response create(List<User> users) {

        User sender = users.get(0);
        User receiver = users.get(1);

        Key<Brotherhood> key = brotherhoodDAO.create(sender, receiver);

        if (key == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.CREATED).build();
    }

    /**
     * Accepts a brotherhood
     *
     * @return Response HTTP status
     */
    @POST
    @Path("/accept")
    public Response accept(List<User> users) {

        User user = users.get(0);
        User bro = users.get(1);
        Brotherhood thisBrotherhood = brotherhoodDAO.getBrotherhood(user, bro);

        try {
            if (thisBrotherhood != null) {

                UpdateResults results = brotherhoodDAO.accept(thisBrotherhood);
                return Response.status(Response.Status.OK).entity(results.getUpdatedCount()).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    /**
     * Reject a brotherhood
     *
     * @param users 2 users, first is the user who deny the second
     * @return Results
     */
    @POST
    @Path("/deny")
    public Response denyBrotherhood(List<User> users) {

        User user = users.get(0);
        User bro = users.get(1);
        Brotherhood thisBrotherhood = brotherhoodDAO.getBrotherhood(user, bro);

        try {
            if (thisBrotherhood != null) {

                brotherhoodDAO.deny(thisBrotherhood);
                return Response.status(Response.Status.OK).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }


    /**
     * Sends the list of brotherhood with status ACCEPTED
     *
     * @param token a token of a user
     * @returns brotherhood list
     */
    @GET
    @Path("/accepted_bros")
    public Response getAcceptedBrotherhood(@HeaderParam("token") String token) {
        List<User> bros = brotherhoodDAO.getBrotherhoods(token, Brotherhood.Brolationship.ACCEPTED);
        ArrayList<String> brosUsernames = new ArrayList<String>();
        for (User bro : bros) {
            brosUsernames.add(bro.getUsername());
        }
        return getResponseBros(bros, brosUsernames);
    }

    /**
     * Sends the list of brotherhood with status AWAITING
     *
     * @param token a token of a user
     * @returns brotherhood list
     */
    @GET
    @Path("/awaiting_bros")
    public Response getAwaitingBrotherhood(@HeaderParam("token") String token) {
        List<User> bros = brotherhoodDAO.getBrotherhoods(token, Brotherhood.Brolationship.AWAITING);
        ArrayList<String> brosUsernames = new ArrayList<String>();
        for (User bro : bros) {
            brosUsernames.add(bro.getUsername());
        }
        return getResponseBros(bros, brosUsernames);
    }

    private Response getResponseBros(List<User> bros, ArrayList<String> brosUsernames) {
        try {
            if (!bros.isEmpty()) {
                return Response.ok(new Gson().toJson(brosUsernames)).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }
}

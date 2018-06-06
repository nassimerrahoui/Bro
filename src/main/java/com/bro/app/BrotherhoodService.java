package com.bro.app;

import com.bro.dao.BrotherhoodDAO;
import com.bro.dao.UserDAO;
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
import java.util.Optional;

/**
 * Manage a friendship between two users
 */
@Path("/brotherhood")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BrotherhoodService {

    private BrotherhoodDAO brotherhoodDAO = new BrotherhoodDAO(BroApp.getDatastore());
    private UserDAO userDAO = new UserDAO(BroApp.getDatastore());

    
    @POST
    @Path("/create")
    public Response create(@HeaderParam("token") String token, User receiver) {

        Optional<User> sender = userDAO.getUser(token);
        if (sender.isPresent()) {
            Key<Brotherhood> key = brotherhoodDAO.create(sender.get(), receiver);
            return Response.status(Response.Status.CREATED).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    /**
     * Accepts a brotherhood
     *
     * @return Response HTTP status
     */
    @POST
    @Path("/accept")
    public Response acceptBrotherhood(@HeaderParam("token") String token, User receiver) {

        try {
            Optional<User> sender = userDAO.getUser(token);
            if (sender.isPresent()) {
                Brotherhood thisBrotherhood = brotherhoodDAO.getBrotherhood(sender.get(), receiver);
                if (thisBrotherhood != null) {

                    brotherhoodDAO.accept(thisBrotherhood);
                    return Response.status(Response.Status.OK).build();
                }
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
        catch (Exception e) { return Response.status(Response.Status.FORBIDDEN).build(); }
        return Response.status(Response.Status.FORBIDDEN).build();
    }

    @POST
    @Path("/deny")
    public Response denyBrotherhood(@HeaderParam("token") String token, User receiver) {

        try {
            Optional<User> sender = userDAO.getUser(token);
            if (sender.isPresent()) {
                Brotherhood thisBrotherhood = brotherhoodDAO.getBrotherhood(sender.get(), receiver);
                if (thisBrotherhood != null) {

                    brotherhoodDAO.deny(thisBrotherhood);
                    return Response.status(Response.Status.OK).build();
                }
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
        catch (Exception e) { return Response.status(Response.Status.FORBIDDEN).build(); }
        return Response.status(Response.Status.FORBIDDEN).build();
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
        User user = userDAO.getUser(token).get();
        List<User> bros = brotherhoodDAO.getBrotherhoods(user, Brotherhood.Brolationship.ACCEPTED);
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
        User user = userDAO.getUser(token).get();
        List<User> bros = brotherhoodDAO.getBrotherhoods(user, Brotherhood.Brolationship.AWAITING);
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

package com.bro.app;

import com.bro.dao.BrotherhoodDAO;
import com.bro.entity.Brotherhood;
import com.bro.entity.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.UpdateResults;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
    public Response create(List<User> users){

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
     * @param token token of user
     * @param id    id of brotherhood
     * @return Response HTTP status
     * @TODO : À Tester (ID)
     */
    @POST
    @Path("/accept")
    public Response accept(List<User> users){

        User user = users.get(0);
        User bro = users.get(1);
        Brotherhood thisBrotherhood = brotherhoodDAO.getBrotherhood(user, bro);

        try {
            if(thisBrotherhood != null){

                UpdateResults results = brotherhoodDAO.accept(thisBrotherhood);
                return Response.status(Response.Status.OK).entity(results.getUpdatedCount()).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch (Exception e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    /**
     * Reject a brotherhood
     *
     * @param token an user token
     * @param id    a brotherhood id
     * @return Response HTTP Status
     */
    @POST
    @Path("/deny")
    public Response shutDown(List<User> users){

        User user = users.get(0);
        User bro = users.get(1);
        Brotherhood thisBrotherhood = brotherhoodDAO.getBrotherhood(user, bro);

        try {
            if(thisBrotherhood != null){

                UpdateResults results = brotherhoodDAO.deny(thisBrotherhood);
                return Response.status(Response.Status.OK).entity(results.getUpdatedCount()).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch (Exception e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }


    /**
     * @param token
     * @return
     * @TODO: La fonction ne fait pas ce qu'elle doit faire
     * Return brotherhood list
     */
    @GET
    @Path("/bros")
    public Response getBros(@HeaderParam("token") String token){

        List<User> bros = brotherhoodDAO.getBrotherhoods(token);

        try{
            if(!bros.isEmpty()){
                return Response.ok(bros).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch (Exception e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }
}

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

/** Service pour gérer une relation entre deux bros **/
@Path("/brotherhood")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BrotherhoodService {

    private BrotherhoodDAO brotherhoodDAO = new BrotherhoodDAO(BroApp.getDatastore());

    /** Demande d'une brotherhood **/
    @POST
    @Path("/create")
    public Response create(List<User> users){

        User sender = users.get(0);
        User receiver = users.get(1);

        Key<Brotherhood> key = brotherhoodDAO.create(sender, receiver);

        if(key == null){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.CREATED).build();
    }

    // TODO : A TESTER (ID)
    /** Accepte une brotherhood **/
    @POST
    @Path("/{token}/{id}/accept")
    public Response accept(@PathParam("token") String token, @PathParam("id") String id){

        Brotherhood thisBrotherhood = brotherhoodDAO.getBrotherhood(token, id);

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

    // TODO : A TESTER (ID)
    /** Décline une brotherhood **/
    @POST
    @Path("/{token}/{id}/deny")
    public Response shutDown(@PathParam("token") String token, @PathParam("id") String id){

        Brotherhood thisBrotherhood = brotherhoodDAO.getBrotherhood(token, id);

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

    /** Retourne la liste des brotherhood**/
    @GET
    @Path("/{token}/bros")
    public Response getBros(@PathParam("token") String token){

        List<User> bros = brotherhoodDAO.getBrotherhoods(token);

        try{
            if(!bros.isEmpty()){
                return Response.status(Response.Status.OK).entity(bros).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch (Exception e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }
}

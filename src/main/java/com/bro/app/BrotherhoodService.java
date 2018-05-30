package com.bro.app;

import com.bro.dao.BrotherhoodDAO;
import com.bro.entity.Brotherhood;
import com.bro.entity.User;
import org.mongodb.morphia.Key;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Path("/brotherhood")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BrotherhoodService {

    private BrotherhoodDAO brotherhoodDAO = new BrotherhoodDAO(BroApp.getDatastore());

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

    @POST
    @Path("/{token}/accept")
    public Response giveHimFive(Brotherhood brotherhood, @PathParam("token") String token){

        Optional<Brotherhood> thisBrotherhood = brotherhoodDAO.getBrotherhood(brotherhood, token);

        try {
            if(thisBrotherhood.isPresent()){

                thisBrotherhood.get().setBrolationship(Brotherhood.Brolationship.ACCEPTED);
                return Response.status(Response.Status.OK).entity(thisBrotherhood.get().getBrolationship()).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch (Exception e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @POST
    @Path("/{token}/deny")
    public Response shutDown(Brotherhood brotherhood, @PathParam("token") String token){

        Optional<Brotherhood> thisBrotherhood = brotherhoodDAO.getBrotherhood(brotherhood, token);

        try {
            if(thisBrotherhood.isPresent()){

                thisBrotherhood.get().setBrolationship(Brotherhood.Brolationship.DENIED);
                return Response.status(Response.Status.OK).entity(thisBrotherhood.get().getBrolationship()).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch (Exception e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @GET
    @Path("/{token}/bros")
    public Response findBros(@PathParam("token") String token){

        List<Brotherhood> bros = brotherhoodDAO.getBrotherhoods(token);

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

package com.bro.app;

import com.bro.dao.BrotherhoodDAO;
import com.bro.entity.Brotherhood;
import com.bro.entity.User;
import org.mongodb.morphia.Key;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("/brotherhood")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BrotherhoodService {

    private BrotherhoodDAO brotherhoodDAO = new BrotherhoodDAO(new MorphiaService().getDatastore());

    @POST
    @Path("/create")
    public Response create(User sender, User receiver){

        Key<Brotherhood> key = brotherhoodDAO.save(sender, receiver);

        if(key == null){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.CREATED).build();
    }

    @POST
    @Path("/{brotherhood}/accept")
    public Response giveHimFive(Brotherhood brotherhood){

        Optional<Brotherhood> thisBrotherhood = brotherhoodDAO.getBrotherhood(brotherhood);

        try {
            if(thisBrotherhood.isPresent()){

                thisBrotherhood.get().setBrolationship(Brotherhood.Brolationship.ACCEPTED);
                return Response.ok(thisBrotherhood.get().getBrolationship()).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch (Exception e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @POST
    @Path("/{brotherhood}/deny")
    public Response shutDown(Brotherhood brotherhood){

        Optional<Brotherhood> thisBrotherhood = brotherhoodDAO.getBrotherhood(brotherhood);

        try {
            if(thisBrotherhood.isPresent()){

                thisBrotherhood.get().setBrolationship(Brotherhood.Brolationship.DENIED);
                return Response.ok(thisBrotherhood.get().getBrolationship()).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch (Exception e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }
}

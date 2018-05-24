package com.bro.app;

import com.bro.dao.BrotherhoodDAO;
import com.bro.entity.Brotherhood;
import org.mongodb.morphia.Key;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/brotherhood")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BrotherhoodService {

    private BrotherhoodDAO brotherhoodDAO = new BrotherhoodDAO(Brotherhood.class, new MorphiaService().getDatastore());

    @POST
    @Path("/create")
    public Response create(Brotherhood brotherhood){

        Key<Brotherhood> key = brotherhoodDAO.save(brotherhood);
        if(key == null){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.CREATED).build();
    }

    // @TODO brotherhood: à dégager
    @GET
    @Path("/{user}")
    public Response getBrotherhoods(@PathParam("user") String user) {

        List<Brotherhood> brotherhoods = brotherhoodDAO.getBrotherhoods(user);
        try {
            if(!brotherhoods.isEmpty()){
                return Response.ok(brotherhoods.stream().sorted()).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch (Exception e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }
}

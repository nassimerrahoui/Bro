package com.bro.app;

import com.bro.dao.BromanceDAO;
import com.bro.entity.Bromance;
import com.bro.entity.User;
import org.mongodb.morphia.Key;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("/bromance")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BromanceService {

    private BromanceDAO bromanceDAO = new BromanceDAO(new MorphiaService().getDatastore());

    @POST
    @Path("/create")
    public Response create(User sender, User receiver){

        Key<Bromance> key = bromanceDAO.save(sender, receiver);

        if(key == null){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.CREATED).build();
    }

    @POST
    @Path("/{bromance}/accept")
    public Response giveHimFive(Bromance bromance){

        Optional<Bromance> thisBromance = bromanceDAO.getBromance(bromance);

        try {
            if(thisBromance.isPresent()){

                thisBromance.get().setBrolationship(Bromance.Brolationship.ACCEPTED);
                return Response.ok(thisBromance.get().getBrolationship()).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch (Exception e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @POST
    @Path("/{bromance}/deny")
    public Response shutDown(Bromance bromance){

        Optional<Bromance> thisBromance = bromanceDAO.getBromance(bromance);

        try {
            if(thisBromance.isPresent()){

                thisBromance.get().setBrolationship(Bromance.Brolationship.DENIED);
                return Response.ok(thisBromance.get().getBrolationship()).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch (Exception e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }
}

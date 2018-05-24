package com.bro.app;

import com.bro.dao.BrotherhoodManagerDAO;
import com.bro.entity.BrotherhoodManager;
import org.mongodb.morphia.Key;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/brotherhoodmanager")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BrotherhoodManagerService {

    private BrotherhoodManagerDAO BrotherhoodManagerDAO = new BrotherhoodManagerDAO(BrotherhoodManager.class, new MorphiaService().getDatastore());

    @POST
    @Path("/{Brother}/create")
    public Response create(BrotherhoodManager BrotherhoodManager){

        Key<BrotherhoodManager> key = BrotherhoodManagerDAO.save(BrotherhoodManager);
        if(key == null){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.CREATED).build();
    }
    // @TODO brotherhoodmanager: récupérer la liste de tous les groupes d'un utilisateur
    // @TODO brotherhoodmanager: faire un accept/denied/awaiting/...
}

package com.bro.app;

import com.bro.dao.UserDAO;
import com.bro.entity.User;
import org.apache.commons.validator.routines.EmailValidator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserService {

    private UserDAO userDAO = new UserDAO(new MorphiaService().getDatastore());

    @POST
    @Path("/create")
    public Response create(User user) {
        user.encrypt();
        if (!EmailValidator.getInstance().isValid(user.getEmail())){
            System.out.println("Format de l'email invalide");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if (userDAO.emailExists(user.getEmail())) {
            System.out.println("L'email existe déjà");
            return Response.status(Response.Status.CONFLICT).build();
        }
        userDAO.save(user);
        return Response.status(Response.Status.CREATED).build();
    }

    @POST
    @Path("/auth")
    public Response auth(User user){
        try{
            user.encrypt();
            authenticate(user.getEmail(), user.getPassword());
            return Response.ok(Response.Status.OK).entity(user.getToken()).build();
        }
        catch (Exception e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    private void authenticate(String email, String password) throws Exception {
        String token = userDAO.authenticate(email, password);
        if(token.isEmpty()){
            throw new Exception("Authentication failed");
        }
    }

    @GET
    @Path("/{token}")
    public Response getUser(@PathParam("token") String token) {
        Optional<User> user = userDAO.getUser(token);
        try {
            if(user.isPresent()){
                return Response.ok(user.get().getId()).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch (Exception e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @POST
    @Path("/{token}/logout")
    public Response logout(@PathParam("token") String token) {

        if(token != null){
            userDAO.logout(token);
            return Response.status(Response.Status.RESET_CONTENT).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @PUT
    @Path("/{token}/settings")
    public Response updateUser(User user){

        Boolean updated = userDAO.updateUser(user).getUpdatedCount() > 0;

        if(updated){
            return Response.status(Response.Status.OK).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    // @TODO pouvoir récupérer des bloqués
    // @TODO ajouter des bloqués et supprimer la bromance
    // @TODO supprimer un blocage.
}

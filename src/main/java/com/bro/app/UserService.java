package com.bro.app;

import com.bro.dao.UserDAO;
import com.bro.entity.User;
import org.apache.commons.validator.routines.EmailValidator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserService {

    private UserDAO userDAO = new UserDAO(BroApp.getDatastore());

    /** Creation du compte dans l'application **/
    @POST
    @Path("/create")
    public Response create(User user) {
        user.encrypt();
        if (!EmailValidator.getInstance().isValid(user.getEmail())){
            return Response.status(Response.Status.BAD_REQUEST).entity("Format de l'email invalide").build();
        }
        if (userDAO.emailExists(user.getEmail())) {
            return Response.status(Response.Status.CONFLICT).entity("L'email existe déjà").build();
        }
        userDAO.save(user);
        return Response.status(Response.Status.CREATED).entity(
                "Quand les hommes suivent la vérité aveuglement, rappelle toi que rien n’est vrai. " +
                "Quand la morale ou les lois bâillonne l’esprit des hommes, rappelle toi que tous est permis." +
                "Salutations Bro ! " +
                "A toi de répandre les valeurs d'un véritable Bro." +
                "Nous comptons sur toi !"
        ).build();
    }

    /** Connexion avec email et password **/
    @POST
    @Path("/auth")
    public Response auth(User user){
        try{
            user.encrypt();
            String token = authenticate(user.getEmail(), user.getPassword());
            return Response.ok(token).build();
        }
        catch (Exception e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }
    private String authenticate(String email, String password) throws Exception {
        String token = userDAO.authenticate(email, password);
        if(token.isEmpty()){
            throw new Exception("Authentication failed");
        }
        return token;
    }

    /** Connexion persistante **/
    @GET
    @Path("/{token}")
    public Response getUser(@PathParam("token") String token) {
        Optional<User> user = userDAO.getUser(token);
        try {
            if(user.isPresent()){
                return Response.ok(token).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch (Exception e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    /** Déconnexion **/
    @POST
    @Path("/{token}/logout")
    public Response logout(@PathParam("token") String token) {

        if(token != null){
            userDAO.logout(token);
            return Response.status(Response.Status.RESET_CONTENT).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    /** Mettre à jours les informations de ton compte **/
    @PUT
    @Path("/settings")
    public Response updateUser(User user){

        Boolean updated = userDAO.updateUser(user).getUpdatedCount() > 0;

        if(updated){
            return Response.status(Response.Status.OK).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    /** Liste des bro devenu des enemies**/
    @GET
    @Path("/{token}/enemies")
    public Response getEnemies(@PathParam("token") String token) {

        Optional<User> user = userDAO.getUser(token);

        if(user.isPresent()) {
            List<User> enemies = user.get().getEnemies();
            if(enemies.isEmpty())
                return Response.status(Response.Status.NO_CONTENT).entity("No enemy").build();
            return Response.status(Response.Status.OK).entity(enemies).build();
        }
        return  Response.status(Response.Status.BAD_REQUEST).build();
    }


    /** Bloquer un ennemi **/
    @POST
    @Path("{token}/enemies/add")
    public Response addEnemy(@PathParam("token") String token, User enemy) {
        Optional<User> user = userDAO.getUser(token);
        if(user.isPresent()) {
            userDAO.addEnemy(user.get(), enemy);
            return Response.status(Response.Status.OK).build();
        }
        return  Response.status(Response.Status.BAD_REQUEST).build();
    }


    /** Débloquer un bro **/
    @POST
    @Path("{token}/enemies/delete")
    public Response deleteEnemy(@PathParam("token") String token, User bro) {
        Optional<User> user = userDAO.getUser(token);
        if(user.isPresent()) {
            userDAO.deleteEnemy(user.get(), bro);
            return Response.status(Response.Status.OK).build();
        }
        return  Response.status(Response.Status.BAD_REQUEST).build();
    }
}

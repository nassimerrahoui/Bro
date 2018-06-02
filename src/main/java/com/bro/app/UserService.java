package com.bro.app;

import com.bro.dao.UserDAO;
import com.bro.entity.User;
import com.google.gson.JsonObject;
import org.apache.commons.validator.routines.EmailValidator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

/** Service pour gérer un bro **/
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
            JsonObject tokenJSON = new JsonObject();
            tokenJSON.addProperty("token",token);
            return Response.status(Response.Status.OK).entity(tokenJSON).build();
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
    @Path("/isconnected")
    public Response getUser(@HeaderParam("token") String token) {
        Optional<User> user = userDAO.getUser(token);
        try {
            if(user.isPresent()){
                return Response.status(Response.Status.OK).entity(user).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch (Exception e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    /** Déconnexion **/
    @POST
    @Path("/logout")
    public Response logout(@HeaderParam("token") String token) {
        System.out.println(token);
        if(token != null){
            userDAO.logout(token);
            return Response.status(Response.Status.RESET_CONTENT).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    // TODO : A TESTER
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

    // TODO : A TESTER
    /** Liste des bro devenu des enemies**/
    @GET
    @Path("/{token}/enemies")
    public Response getEnemies(@HeaderParam("token") String token) {

        Optional<User> user = userDAO.getUser(token);

        if(user.isPresent()) {
            List<User> enemies = user.get().getEnemies();
            if(enemies.isEmpty())
                return Response.status(Response.Status.NO_CONTENT).entity("No enemy").build();
            return Response.status(Response.Status.OK).entity(enemies).build();
        }
        return  Response.status(Response.Status.BAD_REQUEST).build();
    }


    // TODO : A TESTER
    /** Bloquer un ennemi **/
    @POST
    @Path("/enemies/add")
    public Response addEnemy(@HeaderParam("token") String token, User username) {
        Optional<User> user = userDAO.getUser(token);
        if(user.isPresent()) {
            userDAO.addEnemy(user.get(), username);
            return Response.status(Response.Status.OK).build();
        }
        return  Response.status(Response.Status.BAD_REQUEST).build();
    }

    // TODO : A TESTER
    /** Débloquer un bro **/
    @POST
    @Path("/enemies/delete")
    public Response deleteEnemy(@HeaderParam("token") String token, User username) {
        Optional<User> user = userDAO.getUser(token);
        if(user.isPresent()) {
            userDAO.deleteEnemy(user.get(), username);
            return Response.status(Response.Status.OK).build();
        }
        return  Response.status(Response.Status.BAD_REQUEST).build();
    }

    // TODO : A TESTER
    /** Activer la geolocation **/
    @POST
    @Path("/lightside")
    public Response lightSide(@HeaderParam("token") String token) {
        Optional<User> user = userDAO.getUser(token);
        if(user.isPresent()) {
            userDAO.raising(user.get());
            return Response.status(Response.Status.OK).build();
        }
        return  Response.status(Response.Status.BAD_REQUEST).build();
    }

    // TODO : A TESTER
    /** Désactiver la geolocation **/
    @POST
    @Path("/darkside")
    public Response darkSide(@HeaderParam("token") String token) {
        Optional<User> user = userDAO.getUser(token);
        if(user.isPresent()) {
            userDAO.shadow(user.get());
            return Response.status(Response.Status.OK).build();
        }
        return  Response.status(Response.Status.BAD_REQUEST).build();
    }
}

package com.bro.app;

import com.bro.dao.UserDAO;
import com.bro.entity.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.validator.routines.EmailValidator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Manages a bro
 */
@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserService {

    private UserDAO userDAO = new UserDAO(BroApp.getDatastore());

    /**
     * Creates a new bro user
     * @param user a user
     * @return 42, response of the universe of bros
     */
    @POST
    @Path("/create")
    public Response create(User user) {
        user.encrypt();
	user.setDefaultValue();
        if (!EmailValidator.getInstance().isValid(user.getEmail())){
            return Response.status(Response.Status.BAD_REQUEST).entity("Format de l'email invalide").build();
        }
        if (userDAO.emailExists(user.getEmail())) {
            return Response.status(Response.Status.CONFLICT).entity("L'email existe déjà").build();
        }
        userDAO.save(user);
        return Response.status(Response.Status.CREATED).entity(
                "Quand les hommes suivent la vérité aveuglement, souviens-toi que rien n’est vrai. " +
                "Quand la morale ou les lois bâillonne l’esprit des hommes, souviens-toi que tout est permis." +
                "Salutations Bro ! " +
                "A toi de répandre les valeurs d'un véritable Bro." +
                "Nous comptons sur toi !"
        ).build();
    }

    /**
     * Connects an user calling authenticate method
     * @param user a user
     * @return JSONified token
     */
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

    /**
     * Authenticate a user
     * @param email email of user
     * @param password clear password user
     * @return token
     * @throws Exception
     */
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

    /**
     * Updates information related to an user
     *
     * @param user an user
     * @return HTTP Status
     */
    @PUT
    @Path("/settings")
    public Response updateUser(@HeaderParam("token") String token, User user){

        Boolean updated = userDAO.updateUser(token, user).getUpdatedCount() > 0;

        if(updated){
            return Response.status(Response.Status.OK).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    /**
     *  get all enemies
     *
     * @param token
     * @return JSON List
     */
    @GET
    @Path("/enemies")
    public Response getEnemies(@HeaderParam("token") String token) {

        Optional<User> user = userDAO.getUser(token);

        if(user.isPresent()) {
            List<User> enemies = user.get().getEnemies();
            List<String> enemiesStr = new ArrayList<>();
            for (User enemy:
                 enemies) {
                enemiesStr.add(enemy.getUsername());
            }
            if(enemies.isEmpty())
                return Response.status(Response.Status.NO_CONTENT).entity("No enemy").build();
            return Response.status(Response.Status.OK).entity(new Gson().toJson(enemiesStr)).build();
        }
        return  Response.status(Response.Status.BAD_REQUEST).build();
    }

    /**
     * Add an enemy
     *
     * @param token token of user 1
     * @param username username of user 2
     * @return
     */
    @POST
    @Path("/enemies/add")
    public Response addEnemy(@HeaderParam("token") String token, @HeaderParam("username") String username) {
        if(userDAO.addEnemy(token, username).isUpdateOfExisting()){
            return Response.status(Response.Status.OK).build();
        }
        return  Response.status(Response.Status.BAD_REQUEST).build();
    }

    /**
     * Removes an enemy
     *
     * @param token    token of user who wants to remove an enemy
     * @param username enemy to be removed
     * @return Response HTTP Status
     */
    @POST
    @Path("/enemies/remove")
    public Response removeEnemy(@HeaderParam("token") String token, @HeaderParam("username") String username) {
        if(userDAO.removeEnemy(token, username) != null){
            return Response.status(Response.Status.OK).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    /**
     * Gets all users
     *
     * @return List of Users in JSON or HTTP Status 400
     */
    @GET
    @Path("/all")
    public Response getAllUsers() {
        List<User> users = userDAO.getAllUsers();
        if(!users.isEmpty()){
            List<String> usersString = new ArrayList<>();
            for (User user: users) {
                usersString.add(user.getUsername());
            }
            return Response.status(Response.Status.OK).entity(new Gson().toJson(usersString)).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}

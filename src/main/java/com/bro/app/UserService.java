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
        if (!EmailValidator.getInstance().isValid(user.getEmail())) {
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
     * @TODO pouvoir tester avec le username aussi
     * Connects an user calling authenticate method
     * @param user a user
     * @return JSONified token
     */
    @POST
    @Path("/auth")
    public Response auth(User user) {
        try {
            user.encrypt();
            String token = authenticate(user.getEmail(), user.getPassword());
            JsonObject tokenJSON = new JsonObject();
            tokenJSON.addProperty("token", token);
            return Response.status(Response.Status.OK).entity(tokenJSON).build();
        } catch (Exception e) {
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
        if (token.isEmpty()) {
            throw new Exception("Authentication failed");
        }
        return token;
    }

    /**
     * Makes a persistant connection
     *
     * @param token token of a user
     * @return JSONified token
     */
    @GET
    @Path("/{token}")
    public Response getUser(@PathParam("token") String token) {
        Optional<User> user = userDAO.getUser(token);
        try {
            if (user.isPresent()) {
                JsonObject tokenJSON = new JsonObject();
                tokenJSON.addProperty("token", token);
                return Response.status(Response.Status.OK).entity(tokenJSON).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    /**
     * Disconnects an user
     *
     * @param token token of user to be disconnect
     * @return Response HTTP Status
     */
    @POST
    @Path("/{token}/logout")
    public Response logout(@PathParam("token") String token) {
        System.out.println(token);
        if (token != null) {
            userDAO.logout(token);
            return Response.status(Response.Status.RESET_CONTENT).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    // TODO : A TESTER

    /**
     * Update user informations
     *
     * @param user user
     * @return Response HTTP Status
     */
    @PUT
    @Path("/settings")
    public Response updateUser(User user) {

        Boolean updated = userDAO.updateUser(user).getUpdatedCount() > 0;

        if (updated) {
            return Response.status(Response.Status.OK).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    // TODO : A TESTER

    /**
     * Gets all enemies of an user
     *
     * @param token token of an user
     * @return Response HTTP Status
     */
    @GET
    @Path("/{token}/enemies")
    public Response getEnemies(@PathParam("token") String token) {

        Optional<User> user = userDAO.getUser(token);

        if (user.isPresent()) {
            List<User> enemies = user.get().getEnemies();
            if (enemies.isEmpty())
                return Response.status(Response.Status.NO_CONTENT).entity("No enemy").build();
            return Response.status(Response.Status.OK).entity(enemies).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }


    // TODO : A TESTER

    /**
     * add an enemy
     *
     * @param token    token of user who wants to add an enemy
     * @param username enemy to be add
     * @return Response HTTP Status
     */
    @POST
    @Path("/{token}/enemies/add")
    public Response addEnemy(@PathParam("token") String token, User username) {
        Optional<User> user = userDAO.getUser(token);
        if (user.isPresent()) {
            userDAO.addEnemy(user.get(), username);
            return Response.status(Response.Status.OK).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    // TODO : A TESTER

    /**
     * Removes an enemy
     *
     * @param token    token of user who wants to remove an enemy
     * @param username enemy to be removed
     * @return Response HTTP Status
     */
    @POST
    @Path("/{token}/enemies/delete")
    public Response deleteEnemy(@PathParam("token") String token, User username) {
        Optional<User> user = userDAO.getUser(token);
        if (user.isPresent()) {
            userDAO.deleteEnemy(user.get(), username);
            return Response.status(Response.Status.OK).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}

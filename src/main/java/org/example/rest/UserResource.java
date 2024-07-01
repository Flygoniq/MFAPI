package org.example.rest;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.example.model.ResponseBody;
import org.example.model.SignupRequest;
import org.example.model.User;
import org.example.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@Component
@Path("/")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class UserResource {
  @Inject
  private UserService userService;

  @POST
  @Path("/signup")
  public ResponseEntity<ResponseBody> createUser(final SignupRequest signupRequest) {
    return userService.createUser(signupRequest);
  }

  @GET
  @Path("/users/{user_id}")
  public ResponseEntity<ResponseBody> getUser(@PathParam("user_id") String userId) {
    return userService.getUser(userId);
  }

  @PATCH
  @Path("/users/{user_id}")
  public ResponseEntity<ResponseBody> patchUser(@PathParam("user_id") String userId, final User user) {
    return userService.patchUser(userId, user);
  }

  @POST
  @Path("/close")
  public ResponseEntity<ResponseBody> deleteUser() {
    return userService.deleteUser();
  }
}

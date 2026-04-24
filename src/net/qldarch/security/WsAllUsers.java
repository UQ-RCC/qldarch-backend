package net.qldarch.security;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import net.qldarch.jaxrs.ContentType;

@Path("/accounts/all")
public class WsAllUsers {

  @Inject
  private UserStore users;

  @GET
  @Produces(ContentType.JSON)
  @Admin
  public List<User> all() {
    return users.all();
  }

}

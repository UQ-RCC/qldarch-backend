package net.qldarch.security;

import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import net.qldarch.jaxrs.ContentType;

@Path("/user")
public class WsCurrentUser {

  @Inject @Nullable
  private User user;

  @GET
  @Produces(ContentType.JSON)
  public User currentUser() {
    return user;
  }
}

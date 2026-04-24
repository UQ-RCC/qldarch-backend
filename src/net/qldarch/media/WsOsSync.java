package net.qldarch.media;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import net.qldarch.jaxrs.ContentType;
import net.qldarch.security.Admin;

@Path("ossync")
public class WsOsSync {

  @Inject
  private ObjectStoreSync osSync;

  @GET
  @Produces(ContentType.TEXT_PLAIN)
  @Admin
  public String sync() {
    osSync.wake();
    return "ok";
  }
}

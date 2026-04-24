package net.qldarch;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/version")
public class WsVersion {

  @Inject
  private Version version;

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String version() {
    return version.get();
  }

}

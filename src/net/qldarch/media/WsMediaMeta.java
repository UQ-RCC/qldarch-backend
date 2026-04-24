package net.qldarch.media;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

import net.qldarch.archobj.ArchObj;
import net.qldarch.gson.JsonSkipField;
import net.qldarch.hibernate.HS;
import net.qldarch.jaxrs.ContentType;

@Path("media")
public class WsMediaMeta {

  @Inject
  private HS hs;

  @GET
  @Path("/meta/{id}")
  @Produces(ContentType.JSON)
  @JsonSkipField(type=ArchObj.class, field="media")
  public Media get(@PathParam("id") Long id) {
    // TODO if deleted only admin can view
    return hs.get(Media.class, id);
  }
}

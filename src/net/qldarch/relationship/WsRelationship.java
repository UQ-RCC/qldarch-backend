package net.qldarch.relationship;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

import net.qldarch.hibernate.HS;
import net.qldarch.jaxrs.ContentType;

@Path("/relationship/{id}")
public class WsRelationship {

  @Inject
  private HS hs;

  @GET
  @Produces(ContentType.JSON)
  public Relationship get(@PathParam("id") Long id) {
    return hs.get(Relationship.class, id);
  }
}

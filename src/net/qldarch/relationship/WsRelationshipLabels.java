package net.qldarch.relationship;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import net.qldarch.jaxrs.ContentType;

@Path("/relationship/labels")
public class WsRelationshipLabels {

  @GET
  @Produces(ContentType.JSON)
  public Map<String, String> get() {
    return Arrays.stream(RelationshipType.values()).collect(
        Collectors.toMap(rt -> rt.name(), rt -> rt.label()));
  }
}

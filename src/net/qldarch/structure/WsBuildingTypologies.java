package net.qldarch.structure;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import net.qldarch.jaxrs.ContentType;

@Path("/buildingtypologies")
public class WsBuildingTypologies {

  @Inject
  private BuildingTypologies buildingTypologies;

  @GET
  @Produces(ContentType.JSON)
  public Map<Integer, String> get() {
    Set<String> items = buildingTypologies.getTypes();
    Iterator<String> it = items.iterator();
    return IntStream.range(0, items.size()).boxed().collect(Collectors.toMap(i -> i, i -> it.next()));
  }
}

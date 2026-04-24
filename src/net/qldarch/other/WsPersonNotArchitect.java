package net.qldarch.other;

import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import net.qldarch.db.Db;
import net.qldarch.db.Rsc;
import net.qldarch.db.Sql;
import net.qldarch.jaxrs.ContentType;

@Path("others/person")
public class WsPersonNotArchitect {

  @Inject
  private Db db;

  @Path("/notarchitect")
  @GET
  @Produces(ContentType.JSON)
  public List<Map<String, Object>> get() throws Exception {
    return db.executeQuery(new Sql(this).prepare(), Rsc::fetchAll);
  }

}

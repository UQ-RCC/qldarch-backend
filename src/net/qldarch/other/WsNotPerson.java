package net.qldarch.other;

import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import net.qldarch.db.Db;
import net.qldarch.db.Rsc;
import net.qldarch.jaxrs.ContentType;

@Path("others/notperson")
public class WsNotPerson {

  @Inject
  private Db db;

  @GET
  @Produces(ContentType.JSON)
  public List<Map<String, Object>> get() throws Exception {
    return db.executeQuery("select id, label, summary, note, type from archobj where type != 'article' "
        + "and type != 'person' and type != 'firm' and type != 'structure' "
        + "and type != 'interview' and deleted is null and pubts is not null order by id", Rsc::fetchAll);
  }
}

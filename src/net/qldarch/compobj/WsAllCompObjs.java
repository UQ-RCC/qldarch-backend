package net.qldarch.compobj;

import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import net.qldarch.db.Db;
import net.qldarch.db.Rsc;
import net.qldarch.jaxrs.ContentType;

@Path("/compobjs/all")
public class WsAllCompObjs {

  @Inject
  private Db db;

  @GET
  @Produces(ContentType.JSON)
  public List<Map<String, Object>> all() throws Exception {
    return db.executeQuery("select compobj.id, compobj.type, compobj.label as title,"
        + " compobj.created as modified, appuser.displayname as user"
        + " from compobj inner join appuser on compobj.owner = appuser.id", Rsc::fetchAll);
  }
}

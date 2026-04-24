package net.qldarch.structure;

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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/projects")
public class WsStructures {

  @Inject
  private Db db;

 
  /* @GET 
  @Produces(ContentType.JSON)
  public List<Map<String, Object>> get() throws Exception {
    return db.executeQuery(new Sql(this).prepare(), Rsc::fetchAll);
  } */
  @GET
  @Produces(ContentType.JSON)
  public List<Map<String, Object>> get() throws Exception {
    List<Map<String, Object>> projects = db.executeQuery(new Sql(this).prepare(), Rsc::fetchAll);
    log.info("Returned {} projects", projects.size());
    return projects;
  }
}

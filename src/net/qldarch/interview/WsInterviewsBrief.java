package net.qldarch.interview;

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

@Path("/interviews/brief")
public class WsInterviewsBrief {

  @Inject
  private Db db;

  @GET
  @Produces(ContentType.JSON)
  public List<Map<String, Object>> interviews() throws Exception {
    // selects all interviewees that also have a photograph
    return db.executeQuery(new Sql(this).prepare(), Rsc::fetchAll);
  }
}

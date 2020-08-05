package net.qldarch;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import net.qldarch.archobj.ArchObj;
import net.qldarch.db.Db;
import net.qldarch.db.Rsc;
import net.qldarch.db.Sql;
import net.qldarch.jaxrs.ContentType;
import net.qldarch.security.User;


@Produces(ContentType.JSON)
public abstract class WsBase<T> {

  @Inject 
  private Db db;

  @GET
  @Produces(ContentType.JSON)
  public List<Map<String, Object>> get() throws Exception {
    return db.executeQuery(new Sql(this).prepare(), Rsc::fetchAll);
  }

}

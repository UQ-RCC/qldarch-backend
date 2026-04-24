package net.qldarch.search.update;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import net.qldarch.jaxrs.ContentType;
import net.qldarch.security.Admin;

@Path("/search/updateall")
public class WsUpdateAll {

  @Inject
  private IndexUpdater updater;

  @Inject
  private UpdateAllJob updateAllJob;

  @POST
  @Produces(ContentType.TEXT_PLAIN)
  @Admin
  public boolean updateAll() {
    return updater.addTasks(updateAllJob);
  }

}

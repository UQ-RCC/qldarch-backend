package net.qldarch.archobj;

import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import net.qldarch.hibernate.HS;
import net.qldarch.jaxrs.ContentType;
import net.qldarch.search.update.UpdateArchObjJob;
import net.qldarch.search.update.SearchIndexWriter;
import net.qldarch.security.UpdateEntity;
import net.qldarch.security.User;
import net.qldarch.util.M;
import net.qldarch.util.ObjUtils;
import net.qldarch.util.UpdateUtils;

@Path("/archobj")
public class WsRevertArchObj {

  @Inject
  private HS hs;

  @Inject @Nullable
  private User user;

  @Inject
  private SearchIndexWriter searchindexwriter;

  @POST
  @Path("/revert/{id}")
  @Consumes("application/x-www-form-urlencoded")
  @Produces(ContentType.JSON)
  @UpdateEntity(entityClass=ArchObj.class)
  public Response revert(@PathParam("id") Long id, MultivaluedMap<String, Object> params) {
    return hs.execute(session -> {
      final ArchObj archobj = hs.get(ArchObj.class, id);
      final Map<String, Object> m = UpdateUtils.asMap(params);
      if(archobj != null) {
        final Long reqVersion = ObjUtils.asLong(m.get("version"));
        if(reqVersion != null && !reqVersion.equals(archobj.getVersion())) {
          ArchObjVersion version = hs.get(ArchObjVersion.class, reqVersion);
          if(version != null) {
            archobj.copyFrom(version.getDocumentAsMap());
            archobj.setVersion(version.getId());
            VersionUtils.createNewVersion(hs, user, archobj,
                String.format("revert to version '%s'", version.getId()));
            hs.update(archobj);
            try {
              new UpdateArchObjJob(archobj).run(searchindexwriter.getWriter());
              searchindexwriter.getWriter().commit();
            } catch(Exception e) {
              throw new RuntimeException("update search index failed", e);
            }
          } else {
            return Response.status(404).entity(M.of("msg","Version not found")).build();
          }
        }
        return Response.ok().entity(M.of("id", archobj.getId(), "label", archobj.getLabel())).build();
      } else {
        return Response.status(404).entity(M.of("msg","Archive object not found")).build();
      }
    });
  }
}

package net.qldarch.media;

import java.util.Map;

import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import net.qldarch.hibernate.HS;
import net.qldarch.jaxrs.ContentType;
import net.qldarch.search.update.UpdateMediaJob;
import net.qldarch.search.update.SearchIndexWriter;
import net.qldarch.security.SignedIn;
import net.qldarch.security.User;
import net.qldarch.util.DateUtil;
import net.qldarch.util.M;
import net.qldarch.util.ObjUtils;
import net.qldarch.util.UpdateUtils;

@Path("media")
public class WsUpdateMedia {

  @Inject
  @Nullable
  private User user;

  @Inject
  private HS hs;

  @Inject
  private MediaArchive archive;

  @Inject
  private SearchIndexWriter searchindexwriter;

  @POST
  @Path("/{id}")
  @SignedIn
  @Produces(ContentType.JSON)
  public Response post(@PathParam("id") Long id, MultivaluedMap<String, Object> params) {
    if(user != null) {
      Media media = hs.get(Media.class, id);
      if(media != null) {
        if(user.isAdmin() || user.getId().equals(media.getOwner())) {
          final Map<String, Object> m = UpdateUtils.asMap(params);
          if(m.containsKey("label")) {
            media.setLabel(ObjUtils.asString(m.get("label")));
          }
          if(m.containsKey("description")) {
            media.setDescription(ObjUtils.asString(m.get("description")));
          }
          if(m.containsKey("type")) {
            media.setType(MediaType.valueOf(ObjUtils.asString(m.get("type"))));
          }
          if(m.containsKey("creator")) {
            media.setCreator(ObjUtils.asString(m.get("creator")));
          }
          if(m.containsKey("created")) {
            media.setCreated(DateUtil.toSqlDate(ObjUtils.asDate(m.get("created"), "yyyy-MM-dd")));
          }
          if(m.containsKey("rights")) {
            media.setRights(ObjUtils.asString(m.get("rights")));
          }
          if(m.containsKey("identifier")) {
            media.setIdentifier(ObjUtils.asString(m.get("identifier")));
          }
          if(m.containsKey("location")) {
            media.setLocation(ObjUtils.asString(m.get("location")));
          }
          if(m.containsKey("projectnumber")) {
            media.setProjectnumber(ObjUtils.asString(m.get("projectnumber")));
          }
          hs.update(media);
          try {
            new UpdateMediaJob(media, archive).run(searchindexwriter.getWriter());
            searchindexwriter.getWriter().commit();
          } catch(Exception e) {
            throw new RuntimeException("update search index failed", e);
          }
          return Response.ok().entity(media).build();
        }
      } else {
        return Response.status(404).entity(M.of("msg", "Media not found")).build();
      }
    }
    return Response.status(403).entity(M.of("msg", "Unauthorised user")).build();
  }

}

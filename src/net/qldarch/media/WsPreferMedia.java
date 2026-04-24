package net.qldarch.media;

import java.sql.Timestamp;
import java.time.Instant;

import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import net.qldarch.hibernate.HS;
import net.qldarch.jaxrs.ContentType;
import net.qldarch.search.update.UpdateMediaJob;
import net.qldarch.search.update.SearchIndexWriter;
import net.qldarch.security.SignedIn;
import net.qldarch.security.User;
import net.qldarch.util.M;

@Path("media/prefer")
public class WsPreferMedia {

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
  public Response post(@PathParam("id") Long id) {
    if(user != null) {
      Media media = hs.get(Media.class, id);
      if(media != null) {
        if(user.isAdmin() || user.getId().equals(media.getOwner())) {
          media.setPreferred(new Timestamp(Instant.now().toEpochMilli()));
          hs.update(media);
          try {
            new UpdateMediaJob(media, archive).run(searchindexwriter.getWriter());
            searchindexwriter.getWriter().commit();
          } catch(Exception e) {
            throw new RuntimeException("update search index failed", e);
          }
          return Response.ok().entity(M.of("id", media.getId(), "filename", media.getFilename())).build();
        }
      } else {
        return Response.status(404).entity(M.of("msg", "Media not found")).build();
      }
    }
    return Response.status(403).entity(M.of("msg", "Unauthorised user")).build();
  }

}

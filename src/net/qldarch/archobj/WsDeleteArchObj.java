package net.qldarch.archobj;

import java.sql.Timestamp;
import java.time.Instant;

import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import net.qldarch.hibernate.HS;
import net.qldarch.interview.Interview;
import net.qldarch.jaxrs.ContentType;
import net.qldarch.person.Person;
import net.qldarch.search.update.DeleteDocumentJob;
import net.qldarch.search.update.SearchIndexWriter;
import net.qldarch.security.SignedIn;
import net.qldarch.security.User;
import net.qldarch.util.M;

@Path("/archobj")
public class WsDeleteArchObj {

  @Inject
  private HS hs;

  @Inject @Nullable
  private User user;

  @Inject
  private SearchIndexWriter searchindexwriter;

  @DELETE
  @Path("/{id}")
  @SignedIn
  @Produces(ContentType.JSON)
  public Response delete(@PathParam("id") Long id) {
    if(user != null) {
      ArchObj o = hs.get(ArchObj.class, id);
      if(o != null) {
        if(user.isAdmin() || o.getOwner().equals(user.getId())) {
          Timestamp deleted = new Timestamp(Instant.now().toEpochMilli());
          if(o instanceof Person) {
            final Person person = (Person) o;
            for(Interview interview : person.getInterviews()) {
              interview.setDeleted(deleted);
              hs.update(interview);
            }
          }
          o.setDeleted(deleted);
          hs.update(o);
          try {
            new DeleteDocumentJob(o.getId(), o.getType().toString()).run(searchindexwriter.getWriter());
            searchindexwriter.getWriter().commit();
          } catch(Exception e) {
            throw new RuntimeException("delete search index failed", e);
          }
          return Response.ok().entity(M.of("id", o.getId(), "label", o.getLabel())).build();
        }
      } else {
        return Response.status(404).entity(M.of("msg", "Archive object not found")).build();
      }
    }
    return Response.status(403).entity(M.of("msg", "Unauthorised user")).build();
  }
}

package net.qldarch.media;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import net.qldarch.hibernate.HS;
import net.qldarch.jaxrs.ContentType;
import net.qldarch.search.Index;
import net.qldarch.search.update.DeleteDocumentJob;
import net.qldarch.security.SignedIn;
import net.qldarch.security.User;
import net.qldarch.util.M;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;

@Path("media")
public class WsDeleteMedia {

  @Inject @Nullable
  private User user;

  @Inject
  private HS hs;

  @Inject
  private Index index;

  @DELETE
  @Path("/{id}")
  @SignedIn
  @Produces(ContentType.JSON)
  public Response delete(@PathParam("id") Long id) {
    if(user != null) {
      Media media = hs.get(Media.class, id);
      if(media != null) {
        if(user.isAdmin() || user.getId().equals(media.getOwner())) {
          media.setDeleted(new Timestamp(Instant.now().toEpochMilli()));
          hs.update(media);
          Analyzer analyzer = new StandardAnalyzer();
          IndexWriterConfig config = new IndexWriterConfig(analyzer);
          try(Directory directory = index.directory()) {
            try(IndexWriter writer = new IndexWriter(directory, config)) {
              new DeleteDocumentJob(media.getId(), media.getType().toString()).run(writer);
              writer.commit();
            } catch(Exception e) {
              throw new RuntimeException("delete search index failed", e);
            }
          } catch(IOException e) {
            throw new RuntimeException("failed to open search directory", e);
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

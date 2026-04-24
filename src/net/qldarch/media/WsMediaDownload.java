package net.qldarch.media;

import java.io.InputStream;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import jakarta.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;

import net.qldarch.hibernate.HS;
import net.qldarch.util.ContentDispositionSupport;
import net.qldarch.util.M;

@Path("media/download")
public class WsMediaDownload {

  @Inject
  private HS hs;

  @Inject
  private MediaArchive archive;

  private Response notFound() {
    return Response.status(Status.NOT_FOUND).entity(M.of("msg", "Media not found")).build();
  }

  private StreamingOutput stream(InputStream in) {
    return out -> IOUtils.copy(in, out);
  }

  @GET
  @Path("/{id}")
  public Response download(@PathParam("id") Long id) {
    Media media = hs.get(Media.class, id);
    if((media != null) && (media.getMimetype() != null) && (!media.isDeleted())) {
      final InputStream in = archive.stream(media);
      if(in != null) {
        return Response.ok(stream(in)).type(media.getMimetype()).header(HttpHeaders.CONTENT_LENGTH,
            media.getFilesize()).header("Content-Disposition", ContentDispositionSupport.attachment(
                media.getFilename())).build();
      }
    }
    return notFound();
  }
}

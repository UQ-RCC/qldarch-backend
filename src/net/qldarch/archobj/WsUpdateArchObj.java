package net.qldarch.archobj;

import java.sql.Timestamp;
import java.time.Instant;
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

import org.apache.commons.lang3.StringUtils;

import net.qldarch.gson.JsonSkipField;
import net.qldarch.gson.serialize.CollectionRemoveNullsSerializer;
import net.qldarch.gson.serialize.JsonSerializer;
import net.qldarch.hibernate.HS;
import net.qldarch.interview.InterviewUtteranceSerializer;
import net.qldarch.interview.Utterance;
import net.qldarch.jaxrs.ContentType;
import net.qldarch.media.Media;
import net.qldarch.search.update.UpdateArchObjJob;
import net.qldarch.search.update.DeleteDocumentJob;
import net.qldarch.search.update.SearchIndexWriter;
import net.qldarch.security.UpdateEntity;
import net.qldarch.security.User;
import net.qldarch.util.M;
import net.qldarch.util.ObjUtils;
import net.qldarch.util.UpdateUtils;

@Path("/archobj")
public class WsUpdateArchObj {

  @Inject
  private HS hs;

  @Inject @Nullable
  private User user;

  @Inject
  private SearchIndexWriter searchindexwriter;

  @POST
  @Path("/{id}")
  @Consumes("application/x-www-form-urlencoded")
  @Produces(ContentType.JSON)
  @UpdateEntity(entityClass=ArchObj.class)
  @JsonSkipField(type=Media.class, field="depicts")
  @JsonSerializer(type=Utterance.class, serializer=InterviewUtteranceSerializer.class)
  @JsonSerializer(path="$.precededby", serializer=SimpleArchObjSerializer.class)
  @JsonSerializer(path="$.succeededby", serializer=SimpleArchObjSerializer.class)
  @JsonSerializer(path="$.interviews", serializer=CollectionRemoveNullsSerializer.class)
  @JsonSerializer(path="$.interviews.*", serializer=IdArchObjSerializer.class)
  @JsonSerializer(path="$.interviewer.*", serializer=SimpleArchObjSerializer.class)
  @JsonSerializer(path="$.interviewee.*", serializer=SimpleArchObjSerializer.class)
  public Response post(@PathParam("id") Long id, MultivaluedMap<String, Object> params) {
    return hs.execute(session -> {
      final ArchObj archobj = hs.get(ArchObj.class, id);
      final Map<String, Object> m = UpdateUtils.asMap(params);
      final String comment = ObjUtils.asString(m.get("comment"));
      if (archobj == null) {
        return Response.status(404).entity(M.of("msg","Archive object not found")).build();
      }
      try {
        final Long reqVersion = ObjUtils.asLong(m.get("version"));
        if((reqVersion != null) && !reqVersion.equals(archobj.getVersion()) && (archobj.getVersion() != null)) {
          ArchObjVersion version = hs.get(ArchObjVersion.class, reqVersion);
          if((version != null) && version.getOid().equals(archobj.getId())) {
            archobj.copyFrom(version.getDocumentAsMap());
            archobj.setVersion(version.getId());
            if(archobj.updateFrom(m)) {
              final String revertComment = String.format("reverting to version '%s' before change",
                  version.getId());
              final String newComment = StringUtils.isBlank(comment)?revertComment:
                String.format("%s (%s)", comment, revertComment);
              VersionUtils.createNewVersion(hs, user, archobj, newComment);
              hs.update(archobj);
              updateIndex(archobj);
            } else {
              throw new RuntimeException("no change detected, rollback revert to earlier version");
            }
            return Response.ok().entity(archobj).build();
          } else {
            return Response.status(404).entity(M.of("msg","Version not found")).build();
          }
        } else {
          // create an initial version in the version history if it does not exist yet
          if(archobj.getVersion() == null) {
            VersionUtils.createNewVersion(hs, user, archobj, "initial version");
            hs.update(archobj);
            updateIndex(archobj);
          }
          if(archobj.updateFrom(m)) {
            VersionUtils.createNewVersion(hs, user, archobj, comment);
            hs.update(archobj);
            updateIndex(archobj);
          } else if (m.containsKey("public")) {
            boolean published = ObjUtils.asBoolean(m.get("public"));
            if (published) {
              archobj.setPubts(new Timestamp(Instant.now().toEpochMilli()));
            } else {
              archobj.setPubts(null);
            }
            hs.update(archobj);
            updateIndex(archobj);
          }
        }
        return Response.ok().entity(archobj).build();
      } finally {
        archobj.postUpdate(m);
      }
    });
  }

  private void updateIndex(ArchObj archobj) {
    if(archobj != null) {
      if (archobj.isPublished()) {
        try {
          new UpdateArchObjJob(archobj).run(searchindexwriter.getWriter());
          searchindexwriter.getWriter().commit();
        } catch(Exception e) {
          throw new RuntimeException("update search index failed", e);
        }
      } else {
        try {
          new DeleteDocumentJob(archobj.getId(), archobj.getType().toString()).run(searchindexwriter.getWriter());
          searchindexwriter.getWriter().commit();
        } catch(Exception e) {
          throw new RuntimeException("delete search index failed", e);
        }

      }
    }
  }
}

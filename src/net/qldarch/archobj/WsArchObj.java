package net.qldarch.archobj;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import org.hibernate.query.Query;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import net.qldarch.WsBase;
import net.qldarch.db.Db;
import net.qldarch.db.Rsc;
import net.qldarch.db.Sql;
import net.qldarch.gson.JsonSkipField;
import net.qldarch.gson.serialize.CollectionRemoveNullsSerializer;
import net.qldarch.gson.serialize.JsonSerializer;
import net.qldarch.hibernate.HS;
import net.qldarch.interview.Interview;
import net.qldarch.interview.InterviewUtteranceSerializer;
import net.qldarch.interview.Utterance;
import net.qldarch.jaxrs.ContentType;
import net.qldarch.media.Media;
import net.qldarch.security.User;
//import net.qldarch.relationship.TranscriptRelationshipSetup;
import net.qldarch.util.M;

@Path("/archobj")
public class WsArchObj extends WsBase<ArchObj> {

  @Inject
  private HS hs;

  @Inject
  private Db db;
  
  @Inject
  @Nullable
  private User user;

  @GET
  @Path("/{id}")
  @Produces(ContentType.JSON)
  @JsonSkipField(type=Media.class, field="depicts")
  @JsonSerializer(type=Utterance.class, serializer=InterviewUtteranceSerializer.class)
  @JsonSerializer(path="$.precededby", serializer=SimpleArchObjSerializer.class)
  @JsonSerializer(path="$.succeededby", serializer=SimpleArchObjSerializer.class)
  @JsonSerializer(path="$.interviews", serializer=CollectionRemoveNullsSerializer.class)
  @JsonSerializer(path="$.interviews.*", serializer=IdArchObjSerializer.class)
  @JsonSerializer(path="$.interviewer.*", serializer=SimpleArchObjSerializer.class)
  @JsonSerializer(path="$.interviewee.*", serializer=SimpleArchObjSerializer.class)
  public Response get(@PathParam("id") Long id) throws Exception {
    final ArchObj archobj = hs.get(ArchObj.class, id);
    if(archobj != null) {
      if(archobj.isDeleted()) {
        return Response.status(404).entity(M.of("msg","Archive object deleted")).build();
      }
      if(!archobj.canRead(user)) {
        return Response.status(404).entity(M.of("msg","No access to unpublished object")).build();
      }
      archobj.setRelationships(
          db.executeQuery(new Sql(this).prepare(), M.of("id", archobj.getId()), Rsc::fetchAll));
      archobj.setAssociatedMedia(
          db.executeQuery("select am.media, archobj.label, archobj.id  as depicts,"
              + " media.type, media.mimetype"
              + " from associatedmedia am join media on am.media = media.id"
              + " join archobj on media.depicts = archobj.id where am.associated = :id"
              + " and archobj.deleted is null and media.deleted is null",
              M.of("id", archobj.getId()), Rsc::fetchAll));
      archobj.setup();
      // if(archobj instanceof Interview) {
      //   transcriptSetup.setup((Interview)archobj);
      // }
      return Response.ok().entity(archobj).build();
    } else {
      return Response.status(404).entity(M.of("msg", "Archive object not found")).build();
    }
  }

  @GET
  @Path("/byname/{label}")
  @Produces(ContentType.JSON)
  @JsonSkipField(type=Media.class, field="depicts")
  @JsonSerializer(type=Utterance.class, serializer=InterviewUtteranceSerializer.class)
  @JsonSerializer(path="$.precededby", serializer=SimpleArchObjSerializer.class)
  @JsonSerializer(path="$.succeededby", serializer=SimpleArchObjSerializer.class)
  @JsonSerializer(path="$.interviews", serializer=CollectionRemoveNullsSerializer.class)
  @JsonSerializer(path="$.interviews.*", serializer=IdArchObjSerializer.class)
  @JsonSerializer(path="$.interviewer.*", serializer=SimpleArchObjSerializer.class)
  @JsonSerializer(path="$.interviewee.*", serializer=SimpleArchObjSerializer.class)
  public Response getByLabel(@PathParam("label") String label) throws Exception {
    
    String sql_exact = "SELECT id,label,type FROM archobj WHERE label='"+label+"'LIMIT 1";
    List<Map<String, Object>> results_exact =  db.executeQuery(sql_exact, Rsc::fetchAll);
    if (!results_exact.isEmpty()) {
      return Response.ok().entity(results_exact).build();
    } else {
      String sql_similar = "SELECT id,label,type FROM archobj WHERE type IN ('firm','person') AND SIMILARITY(label,'"+label+"') > 0.4";
      List<Map<String, Object>> results_similar =  db.executeQuery(sql_similar, Rsc::fetchAll);
      if (!results_similar.isEmpty()) {
        return Response.ok().entity(results_similar).build();
      } else {
        return Response.status(401).entity(M.of("msg", "No records found")).build();
      }

    }
  }
}

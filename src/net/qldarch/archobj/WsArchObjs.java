package net.qldarch.archobj;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;
import org.hibernate.query.Query;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

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

@Path("/archobjs")
public class WsArchObjs {

  @Inject
  private HS hs;

  @Inject
  private Db db;
  
  @Inject
  @Nullable
  private User user;

  // @GET
  // @Path("/entities")
  // @Produces(ContentType.JSON)
  // @JsonSkipField(type=Media.class, field="depicts")
  // public Response getEntities() throws Exception {
  //   if (user == null || !(user.isAdmin() || user.isEditor())) {
  //     return Response.status(403).entity(M.of("msg", "Unauthorised user")).build();
  //   }
  //   String sql = "SELECT a.id as id, label, type, pubts, architect " +
  //                "FROM archobj a " +
  //                "LEFT JOIN appuser u ON a.owner = u.id " +
  //                "LEFT JOIN person p ON a.id = p.id " +
  //                "WHERE type NOT IN ('article', 'interview') AND deleted IS NULL AND trim(label) <> '' ORDER BY label ASC;";
  //   List<Map<String, Object>> results = db.executeQuery(sql, Rsc::fetchAll);
  //   return Response.ok().entity(results).build();
  // }

  //@Inject
  //private TranscriptRelationshipSetup transcriptSetup;
  @GET
  @Path("/")
  @Produces(ContentType.JSON)
  @JsonSkipField(type=Media.class, field="depicts")
  public Response get(
    @QueryParam("type") List<String> types,
    @QueryParam("exctype") List<String> exctypes,
    @QueryParam("architect") Boolean architect,
    @QueryParam("owner") Long owner,
    @QueryParam("deleted") Boolean deleted,
    @QueryParam("published") Boolean published,
    @DefaultValue("label") @QueryParam("sort") String sort,
    @DefaultValue("0") @QueryParam("offset") int offset,
    @DefaultValue("1000") @QueryParam("size") int size
  ) throws Exception {
    if (user == null || !(user.isAdmin() || user.isEditor())) {
      return Response.status(403).entity(M.of("msg", "Unauthorised user")).build();
    }
    sort = sort.toLowerCase();
    switch (sort) {
      case "id": case "label": case "type": 
        break;
      default:
        sort = "label";
      break; 
    }
    String sql = "SELECT a.id as id, label, type, owner, u.displayname as ownername, deleted, pubts, architect " +
                 "FROM archobj a " +
                 "LEFT JOIN appuser u ON a.owner = u.id " +
                 "LEFT JOIN person p ON a.id = p.id " +
                 "%s ORDER BY " + sort + " ASC LIMIT :size OFFSET :offset;";
    ArrayList<String> filters = new ArrayList<String>();
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("offset", offset);
    params.put("size", size);
    if (types != null && types.size() > 0) {
      filters.add("type = ANY (:types)");
      params.put("types", types);
    } else if (exctypes != null && exctypes.size() > 0) {
      filters.add("NOT (type = ANY (:exctypes))");
      params.put("exctypes", exctypes);
    }
    if (owner != null) {
      filters.add("owner = :owner");
      params.put("owner", owner);
    }
    if (deleted != null) {
      filters.add("deleted is " + (deleted ? "not" : "") + " null");
    }
    if (published != null) {
      filters.add("pubts is " + (published ? "not" : "") + " null");
    }
    if (architect != null) {
      filters.add("architect = " + (architect ? "true" : "false"));
    }
    String cond = String.join(" AND ", filters);
    if (cond.length() > 0) cond = "WHERE " + cond;

    List<Map<String, Object>> results = db.executeQuery(String.format(sql, cond), params, Rsc::fetchAll);
    return Response.ok().entity(results).build();
/*
    final Stream<ArchObj> results = hs.getStream(session -> {
      CriteriaBuilder cb = session.getCriteriaBuilder();
      CriteriaQuery<ArchObj> cq = cb.createQuery(ArchObj.class);
      Root<ArchObj> root = cq.from(ArchObj.class);
      cq = cq.select(root);
      if (owner != null) {
        cq = cq.where(cb.equal(root.get("owner"), owner));
      }
      Query<ArchObj> q = session.createQuery(cq).setFirstResult(offset).setMaxResults(size);
      return q.stream();
    });
    */
  }

  // @GET
  // @Path("/owned")
  // @Produces(ContentType.JSON)
  // public List<Map<String, Object>> get(@QueryParam("type") String type) {
  //   String sql = "SELECT a.id as id, label, type, deleted, pubts, architect FROM archobj a " +
  //                "LEFT JOIN person p ON a.id = p.id " +
  //                "WHERE owner = :id %s ORDER BY id ASC";
  //   try {
  //     if(user != null) {
  //       if (type != null) {
  //         return db.executeQuery(String.format(sql, "AND type = :type"), M.of("id", user.getId(), "type", type), Rsc::fetchAll);
  //       } else {
  //         return db.executeQuery(String.format(sql, ""), M.of("id", user.getId()), Rsc::fetchAll);
  //       }
  //     } else {
  //       //log.debug("media by owner failed as user is {}", user);
  //     }
  //   } catch(Exception e) {
  //     //log.debug("media by owner failed for user {} ", user, e);
  //   }
  //   return null;
  // }
}

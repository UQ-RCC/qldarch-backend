package net.qldarch.relationship;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import net.qldarch.hibernate.HS;
import net.qldarch.jaxrs.ContentType;
import net.qldarch.security.User;
import net.qldarch.util.M;
import net.qldarch.util.ObjUtils;
import net.qldarch.util.UpdateUtils;

@Path("/articlerelationship")
public class WsCreateArticleRelationship {

  @Inject
  private HS hs;

  @Inject
  @Nullable
  private User user;

  @PUT
  @Consumes("application/x-www-form-urlencoded")
  @Produces(ContentType.JSON)
  public Response create(MultivaluedMap<String, Object> params) {
    if((user != null) && (user.isAdmin() || user.isEditor())) {
      final Map<String, Object> m = UpdateUtils.asMap(params);
      final RelationshipType type = RelationshipType.valueOf(ObjUtils.asString(m.get("type")));
      final RelationshipSource source = RelationshipSource.valueOf(ObjUtils.asString(m.get("source")));
      final long subject = ObjUtils.asLong(m.get("subject"));
      final long object = ObjUtils.asLong(m.get("object"));
      final long article = ObjUtils.asLong(m.get("article"));
      //final String page = ObjUtils.asString(m.get("page"));
      if(type == null) {
        return Response.status(400).entity(M.of("msg", "Missing or unknown type")).build();
      }
      if(source == null) {
        return Response.status(400).entity(M.of("msg", "Missing or unknown source")).build();
      }
      if(source != RelationshipSource.article) {
        return Response.status(400).entity(M.of("msg", "Non-article source")).build();
      }
      if(subject == 0L) {
        return Response.status(400).entity(M.of("msg", "Missing or unknown subject")).build();
      }
      if(object == 0L) {
        return Response.status(400).entity(M.of("msg", "Missing or unknown object")).build();
      }
      if(article == 0L) {
        return Response.status(400).entity(M.of("msg", "Missing or unknown article")).build();
      }
      // if(page == null) {
      //   return Response.status(400).entity(M.of("msg", "Missing or unknown page")).build();
      // }
      try {
        ArticleRelationship relationship = new ArticleRelationship();
        relationship.setType(type);
        relationship.setSource(source);
        relationship.setSubject(subject);
        relationship.setObject(object);
        relationship.setArticle(article);
        //relationship.setPage(page);
        relationship.setOwner(user.getId());
        if(m.containsKey("page")) {
          relationship.setPage(ObjUtils.asString(m.get("page")));
        }
        if(m.containsKey("note")) {
          relationship.setNote(ObjUtils.asString(m.get("note")));
        }
        if(m.containsKey("from")) {
          relationship.setFrom(ObjUtils.asInteger(m.get("from")));
        }
        if(m.containsKey("until")) {
          relationship.setUntil(ObjUtils.asInteger(m.get("until")));
        }
        relationship.setCreated(new Timestamp(Instant.now().toEpochMilli()));
        hs.save(relationship);
        return Response.ok().entity(relationship).build();
      } catch(Exception e) {
        throw new RuntimeException("failed to create article relationship object", e);
      }
    } else {
      return Response.status(403).entity(M.of("msg", "Unauthorised user")).build();
    }
  }

}

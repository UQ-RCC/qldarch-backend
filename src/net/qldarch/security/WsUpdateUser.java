package net.qldarch.security;

import java.util.Map;

import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import lombok.extern.slf4j.Slf4j;
import net.qldarch.hibernate.HS;
import net.qldarch.jaxrs.ContentType;
import net.qldarch.util.M;
import net.qldarch.util.ObjUtils;
import net.qldarch.util.UpdateUtils;

@Path("/account/update")
@Slf4j
public class WsUpdateUser {

  @Inject
  @Nullable
  private User user;

  @Inject
  private UserStore users;

  @Inject
  private HS hs;

  @POST
  @Path("/{id}")
  @Produces(ContentType.JSON)
  public Response update(@PathParam("id") Long id, MultivaluedMap<String, Object> params) {
    final User u = users.get(id);
    if(u != null) {
      if(user != null && user.isAdmin()) {
        final Map<String, Object> m = UpdateUtils.asMap(params);
        if(m.containsKey("contact")) {
          u.setContact(ObjUtils.asBoolean(m.get("contact")));
        }
        if(m.containsKey("displayName")) {
          u.setDisplayName(ObjUtils.asString(m.get("displayName")));
        }
        if(m.containsKey("email")) {
          u.setEmail(ObjUtils.asString(m.get("email")));
        }
        if(m.containsKey("role")) {
          u.setRole(ObjUtils.asString(m.get("role")));
        }
        if(m.containsKey("username")) {
          u.setUsername(ObjUtils.asString(m.get("username")));
        }
        hs.update(u);
        log.info("updating of appuser id {} successful", id);
        return Response.ok().entity(u).build();
      } else {
        return Response.status(403).entity(M.of("msg", "Unauthorised user")).build();
      }
    } else {
      log.debug("account with id {} does not exist", id);
      return Response.status(404).entity(M.of("msg", "User account not found")).build();
    }
  }
}

package net.qldarch.security;

import java.sql.Timestamp;

import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.qldarch.hibernate.HS;
import net.qldarch.jaxrs.ContentType;

@Path("/account/activate")
@Slf4j
public class WsActivateAccount {

  @AllArgsConstructor
  @Data
  private static class ActivationResponse {
    private boolean success;
    private Session session;

    public static ActivationResponse failed() {
      return new ActivationResponse(false, null);
    }
  }

  @Inject @Nullable
  private User user;

  @Inject
  private UserStore users;

  @Inject
  private HS hs;

  @Inject
  private SessionStore sessions;

  private Response failed() {
    return Response.ok().entity(ActivationResponse.failed()).build();
  }

  @GET
  @Produces(ContentType.JSON)
  public Response activate(@QueryParam("id") Long id, @QueryParam("code") String code) {
    final User u = users.get(id);
    if(u == null) {
      log.debug("account with id {} does not exist", id);
      return failed();
    } else if(!u.isSignInAllowed()) {
      log.debug("can not activate account {} as signin is disabled on this account", u.getUsername());
      return failed();
    } else if(u.getActivated() != null && !StringUtils.equals(u.getActivation(), code)) {
      log.debug("account activation code wrong {}, supplied code {}", u.getUsername(), code);
      return failed();
    } else if(u.getActivated() != null) {
      log.debug("account already activated, returning success for account {}", u.getUsername());
      return Response.ok(new ActivationResponse(true, null)).build();
      } else if(!StringUtils.equals(u.getActivation(), code)) {
        log.debug("account activation code wrong {}, supplied code {}", u.getUsername(), code);
        return failed();
    } else { 
      u.setActivated(new Timestamp(System.currentTimeMillis()));
      hs.update(u);
      log.info("activation of appuser id {} successful", id);
      if(user == null) {
        final Session session = sessions.newSession(u);
        final ActivationResponse resp = new ActivationResponse(true, session);
        NewCookie cookie = new NewCookie(new NewCookie(
            new Cookie("sessionid",session.getSessionId(), "/", null)));
        return Response.ok(resp).cookie(cookie).build();
      } else {
        return Response.ok(new ActivationResponse(true, null)).build();
      }
    }
  }

}

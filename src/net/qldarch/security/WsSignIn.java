package net.qldarch.security;

import jakarta.inject.Inject;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import lombok.extern.slf4j.Slf4j;
import net.qldarch.jaxrs.ContentType;

import net.qldarch.search.update.IndexUpdater;
import net.qldarch.search.update.UpdateAllJob;

@Path("/signin")
@Slf4j
public class WsSignIn {

  @Inject
  private SignIn signin;

  @Inject
  private IndexUpdater updater;

  @Inject
  private UpdateAllJob updateAllJob;

  @POST
  @Produces(ContentType.JSON)
  public Response signin(@FormParam("email") String usernameOrEmail,
      @FormParam("password") String password) {
    final SignInResponse s = signin.signin(usernameOrEmail, password);
    if(s.isSuccess()) {
      log.info("signin ok for '{}', session '{}'", usernameOrEmail, s.getSession().getSessionId());
      NewCookie cookie = new NewCookie(new NewCookie(
          new Cookie("sessionid",s.getSession().getSessionId(), "/", null)));
      if(s.getUser() != null && s.getUser().isAdmin()) {
        log.info("admin login detected, triggering search reindex");
        updater.addTasks(updateAllJob);
      }
      return Response.ok(s).cookie(cookie).build();
    } else {
      log.info("signin failed for '{}'", usernameOrEmail);
      return Response.ok(s).build();
    }
  }

}

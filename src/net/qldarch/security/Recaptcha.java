package net.qldarch.security;

import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.Response;

import net.qldarch.configuration.Cfg;
import net.qldarch.guice.Bind;

@Bind
public class Recaptcha {

  @Inject
  @Cfg("recaptcha.url")
  private String recaptchaUrl;

  @Inject
  @Cfg("recaptcha.secret")
  private String recaptchaSecret;

  public Response verifyRecaptcha(String gRecaptchaResponse) {
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(recaptchaUrl);
    Form form = new Form();
    form.param("secret", recaptchaSecret).param("response", gRecaptchaResponse);
    Entity<Form> entity = Entity.form(form);
    Response response = target.request().post(entity);
    return response;
  }
}

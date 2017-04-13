package net.qldarch.message;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import lombok.extern.slf4j.Slf4j;
import net.qldarch.jaxrs.ContentType;

import org.json.JSONException;
import org.json.JSONObject;

@Path("/message/contact")
@Slf4j
public class WsMessageContact {

  @Inject
  private MessageContact msgcontact;

  @POST
  @Produces(ContentType.JSON)
  public Response post(@FormParam("content") String content, @FormParam("firstname") String firstname,
      @FormParam("lastname") String lastname, @FormParam("from") String from,
      @FormParam("newsletter") boolean newsletter, @FormParam("g-recaptcha-response") String gRecaptchaResponse) {
    log.info("Message: {}, from: {} {}<{}>", content, firstname, lastname, from);
    try {
      Response verRecaptcha = verifyRecaptcha(gRecaptchaResponse);
      int status = verRecaptcha.getStatus();
      if(status != 200) {
        log.error("failed : http error code : {}", status);
        verRecaptcha.close();
        return Response
            .status(status)
            .entity(
                new MessageContactResponse(false, "verifiying recaptcha failed : http error code : "
                    + String.valueOf(status))).build();
      } else if(verRecaptcha.hasEntity()) {
        try {
          String strRecaptcha = verRecaptcha.readEntity(String.class);
          log.info("json response: {}", strRecaptcha);
          JSONObject jsonRecaptcha = new JSONObject(strRecaptcha);
          if(jsonRecaptcha.getBoolean("success")) {
            MessageContactResponse response = msgcontact
                .send(content, firstname + " " + lastname, from, newsletter);
            verRecaptcha.close();
            if(response != null && response.isSuccess()) {
              return Response.ok(response).build();
            } else {
              return Response.status(Status.BAD_REQUEST).entity(response).build();
            }
          } else {
            verRecaptcha.close();
            return Response.status(Status.BAD_REQUEST)
                .entity(new MessageContactResponse(false, "verifiying recaptcha unsuccessful")).build();
          }
        } catch(JSONException e) {
          verRecaptcha.close();
          return Response
              .status(Status.BAD_REQUEST)
              .entity(
                  new MessageContactResponse(false, "failed to parse verified recaptcha response to json: "
                      + e.toString())).build();
        }
      } else {
        verRecaptcha.close();
        return Response.status(Status.BAD_REQUEST)
            .entity(new MessageContactResponse(false, "verifiying recaptcha failed")).build();
      }
    } catch(Exception e) {
      return Response.status(Status.BAD_REQUEST).entity(new MessageContactResponse(false, e.toString())).build();
    }
  }

  private Response verifyRecaptcha(String gRecaptchaResponse) {
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target("https://www.google.com/recaptcha/api/siteverify");
    Form form = new Form();
    form.param("secret", "6Lc7wQkUAAAAALNXj5aKw9ljKrhtwG0nhFUA5yzV").param("response", gRecaptchaResponse);// qldarch.net@gmail.com
    Entity<Form> entity = Entity.form(form);
    Response response = target.request().post(entity);
    return response;
  }
}
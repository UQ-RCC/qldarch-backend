package net.qldarch.jaxrs;

import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotAllowedExceptionMapper implements ExceptionMapper<NotAllowedException> {
  @Override
  public Response toResponse(NotAllowedException e) {
    return Response.status(405).build();
  }
}

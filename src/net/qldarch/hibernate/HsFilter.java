package net.qldarch.hibernate;

import java.io.IOException;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import net.qldarch.db.Db;
import net.qldarch.guice.Bind;

@Bind
@Singleton
public class HsFilter implements Filter {

  @Inject
  private HS hs;

  @Inject
  private Db db;

  @Override
  public void init(FilterConfig arg0) throws ServletException {}

  @Override
  public void destroy() {}

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    hs.execute(session -> {
      session.doWork(con -> {
        db.join(con, () -> {
          try {
            chain.doFilter(request, response);
          } catch(Exception e) {
            throw new RuntimeException(e);
          }
        });
      });
      return "";
    });
  }

}

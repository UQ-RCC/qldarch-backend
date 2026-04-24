package net.qldarch.security;

import jakarta.annotation.Nullable;
import jakarta.inject.Inject;

import com.google.inject.Provider;

import net.qldarch.guice.Bind;

@Bind(provides=User.class)
public class UserProvider implements Provider<User> {

  @Inject
  @Nullable
  private Session session;

  @Inject 
  private SessionStore store;

  @Override
  public User get() {
    return store.get(session);
  }

}

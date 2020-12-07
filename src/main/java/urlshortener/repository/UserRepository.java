package urlshortener.repository;

import urlshortener.domain.User;

import java.util.List;

public interface UserRepository {
  User save(User u);
  Long count();
  List<User> list(Long limit, Long offset);
  long getId(String username);
  User login(User u);
  User getUser(String username);
  boolean exists(String userId);

}

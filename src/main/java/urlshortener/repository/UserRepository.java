package urlshortener.repository;

import urlshortener.domain.User;

import java.util.List;

public interface UserRepository {
  boolean save(User u);
  Long count();

  List<User> list(Long limit, Long offset);

  User getUser(String username);

  List<User> getUsers();

  boolean exists(String userId);

  List<User> getUsers();

  boolean deleteById(int userId);
}

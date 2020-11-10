package urlshortener.repository;

import urlshortener.domain.User;

import java.util.List;

public interface UserRepository {

  User save(User u);

  Long count();

  List<User> list(Long limit, Long offset);

}
package urlshortener.service;

import org.springframework.stereotype.Service;
import urlshortener.domain.Role;
import urlshortener.domain.User;
import urlshortener.repository.UserRepository;

import java.util.List;

@Service
public class UserService {
/*
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User save(String username, String password) {
    User u = UserBuilder.newInstance()
        .username(username)
        .password(password)
        .roleId(Role.ROLE_USER)
        .build();

    return userRepository.save(u);
  }

  public User login(String username, String password) {
    User u = UserBuilder.newInstance()
            .username(username)
            .password(password)
            .build();

    return userRepository.login(u);
  }

  public User getUser(String username) {
    return userRepository.getUser(username);
  }

    public boolean exists(String userId) {
      return userRepository.exists(userId);
    }

    public List<User> getUsers() {
      return userRepository.getUsers();
    }

  public boolean deleteUser(int userId) {
    return userRepository.deleteById(userId);
  }

  public List<User> getUsers() {
    return userRepository.getUsers();
  }*/
}

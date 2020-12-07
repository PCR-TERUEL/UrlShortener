package urlshortener.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import urlshortener.domain.Role;
import urlshortener.domain.User;
import urlshortener.repository.UserRepository;

@Service
public class UserService {



  @Autowired
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public boolean save(String username, String password) {
    User u = UserBuilder.newInstance()
        .id("-1")
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
}

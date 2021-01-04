package urlshortener.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
import urlshortener.domain.Role;
import urlshortener.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Service
public class SecureUserService implements UserDetailsService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Hello, Spring Security! let me check: " + username);
        urlshortener.domain.User user = userRepository.getUser(username);

        if (user != null) {
            System.out.println("Received user details from database: " + user.getUsername() + ":" + user.getPassword() + ":" + user.getRoleId());
            return User.builder()
                    .username(user.getUsername())
                    .password(bCryptPasswordEncoder.encode(user.getPassword()))
                    .disabled(false)
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .roles(getRoleName(user))
                    .build();
        }

        throw new UsernameNotFoundException("User or password invalid");

    }

    private String getRoleName(urlshortener.domain.User u) {
        if (u.getRoleId() == Role.ROLE_ADMIN) {
            return "ADMIN";
        } else {
            return "USER";
        }
    }

    public boolean save(String username, String password) {
        urlshortener.domain.User u = SecureUserBuilder.newInstance()
                .id("-1")
                .username(username)
                .password(password)
                .roleId(Role.ROLE_USER)
                .build();

        return userRepository.save(u);
    }

    public urlshortener.domain.User getUser(String username) {
        return userRepository.getUser(username);
    }

    public List<urlshortener.domain.User> getUsers() {
        return userRepository.getUsers();
    }

    public boolean deleteUser(int id) {
        return userRepository.deleteById(id);
    }
}
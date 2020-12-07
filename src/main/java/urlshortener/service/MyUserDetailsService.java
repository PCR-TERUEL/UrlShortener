package urlshortener.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
import urlshortener.domain.Role;


@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Hello, Spring Security!");
        urlshortener.domain.User user = userService.getUser(username);
        System.out.println(user.getUsername() + " " + user.getPassword());

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

    private String getRoleName( urlshortener.domain.User u) {
        if (u.getRoleId() == Role.ROLE_ADMIN) {
            return "ADMIN";
        } else {
            return "USER";
        }

    }

}
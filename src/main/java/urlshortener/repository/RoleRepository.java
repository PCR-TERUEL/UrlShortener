package urlshortener.repository;

import urlshortener.domain.Role;
import urlshortener.domain.User;

import java.util.List;

public interface RoleRepository {

    Role getRole(String role);
}

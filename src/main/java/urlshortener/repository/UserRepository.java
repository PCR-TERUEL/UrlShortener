package urlshortener.repository;

import urlshortener.domain.User;

import java.util.List;

public interface UserRepository {
  /**
   * Save an user on the repository
   *
   * @param u
   * @return saved?
   */
  boolean save(User u);

  /**
   * Get number of total users
   *
   * @return number of users
   */
  Long count();

  /**
   * Get a list of users in a range
   *
   * @param limit
   * @param offset
   * @return List of users
   */
  List<User> list(Long limit, Long offset);

  /**
   * Retrive User object from given username
   *
   * @param username
   * @return User or null
   */
  User getUser(String username);

  /**
   * Get a list of all users
   *
   * @return a list of all users
   */
  List<User> getUsers();

  /**
   * Check if user exists
   *
   * @param userId
   * @return exists?
   */
  boolean exists(String userId);

  /**
   * Delete a user
   *
   * @param userId
   * @return deleted?
   */
  boolean deleteById(int userId);
}

package urlshortener.repository;

import java.util.List;
import urlshortener.domain.ShortURL;

public interface ShortURLRepository {

  /**
   * Get the ShortURL of id
   *
   * @param id
   * @return ShortURL
   */
  ShortURL findByKey(String id);

  /**
   * Get the list of ShortURL's that redirects
   * to given target
   *
   * @param target
   * @return list of ShortURL's
   */
  List<ShortURL> findByTarget(String target);


  /**
   * Save a ShortURL on the repository
   *
   * @param su
   * @return ShortURL
   */
  ShortURL save(ShortURL su);

  ShortURL mark(ShortURL urlSafe, boolean safeness);

  /**
   * Update parameters of a ShortURL
   *
   * @param su
   */
  void update(ShortURL su);

  /**
   * Delete a ShortURL from the repository
   *
   * @param id
   */
  void delete(String id);

  /**
   * Check if a URL is expired
   *
   * @param id
   * @return expired?
   */
  boolean isExpired(String id);

  /**
   * Get total number of ShortURL's
   *
   * @return number
   */
  Long count();

  /**
   * Get a list of ShortURL's in a range
   *
   * @param limit
   * @param offset
   * @return List of urls
   */
  List<ShortURL> list(Long limit, Long offset);

  /**
   * Get a list of ShortURL's of an user
   *
   * @param userId
   * @return list of ShortUrls
   */
  List<ShortURL> findByUser(String userId);

}

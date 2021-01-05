package urlshortener.repository;

import java.util.List;
import urlshortener.domain.Click;

public interface ClickRepository {

  /**
   * Get all clicks of a given hash
   *
   * @param hash
   * @return list of Clicks
   */
  List<Click> findByHash(String hash);

  /**
   * Get n of clicks of a given hash
   *
   * @param hash
   * @return n of clicks
   */

  Long clicksByHash(String hash);


  /**
   * Save a click
   *
   * @param cl
   * @return Click
   */
  Click save(Click cl);

  /**
   * Delete a click
   *
   * @param id
   */
  void delete(Long id);

  /**
   * Get number of clicks
   *
   * @return n clicks
   */
  Long count();


  /**
   * Delete all clicks
   *
   */
  void deleteAll();

  /**
   * Get a list of clicks (range)
   *
   * @param limit
   * @param offset
   * @return list of clicks
   */
  List<Click> list(Long limit, Long offset);
}

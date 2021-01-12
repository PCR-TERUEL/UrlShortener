package urlshortener.repository;

import java.util.List;
import urlshortener.domain.Click;

public interface ClickRepository {


  /**
   * Save a click
   *
   * @param cl
   * @return Click
   */
  Click save(Click cl);

}

package urlshortener.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import urlshortener.domain.Click;
import urlshortener.repository.ClickRepository;

@Service
public class ClickService {

  private static final Logger log = LoggerFactory
      .getLogger(ClickService.class);

  private final ClickRepository clickRepository;
  public ClickService(ClickRepository clickRepository) {
    this.clickRepository = clickRepository;
  }


  /**
   * Create a Click and tell the repository to save it
   *
   * @param hash
   * @param ip
   */
  public void saveClick(String hash, String ip) {
    Click cl = ClickBuilder.newInstance().hash(hash).createdNow().ip(ip).build();
    cl = clickRepository.save(cl);
    System.out.println(cl != null ? "[" + hash + "] saved with id [" + cl.getId() + "]" :
            "[" + hash + "] was not saved");
    log.info(cl != null ? "[" + hash + "] saved with id [" + cl.getId() + "]" :
        "[" + hash + "] was not saved");
  }

}

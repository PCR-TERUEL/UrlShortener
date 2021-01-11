package urlshortener.service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;
import urlshortener.domain.Metric;
import urlshortener.domain.ShortURL;
import urlshortener.repository.ShortURLRepository;
import urlshortener.web.UrlShortenerController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Service
public class ShortURLService {

  private final ShortURLRepository shortURLRepository;


  public ShortURLService(ShortURLRepository shortURLRepository) {
    this.shortURLRepository = shortURLRepository;
  }

  /**
   * Get a ShortURL from id
   *
   * @param id
   * @return ShortURL
   */
  public ShortURL findByKey(String id) {
    return shortURLRepository.findByKey(id);
  }

  /**
   * Retrive a list of ShortURL's of an user
   *
   * @param userId
   * @return List of urls
   * @throws URISyntaxException
   */
  public List<ShortURL> findByUser(String userId) throws URISyntaxException {
    List<ShortURL> shortURLS = shortURLRepository.findByUser(userId);

    for (ShortURL shortURL : shortURLS) {
      shortURL.setUri(new URI(UrlShortenerController.HOST + "/r/" + shortURL.getHash()));
    }
     return shortURLRepository.findByUser(userId);
  }

  public JSONObject toJson(List<ShortURL> shortList) {
    JSONObject jObject = new JSONObject();

    try
    {
      JSONArray jArray = new JSONArray();
      for (ShortURL su : shortList)
      {
        JSONObject shortJSON = new JSONObject();
        shortJSON.put( "uri", "http://" + UrlShortenerController.HOST + "/r/" + su.getHash());
        shortJSON.put("target", su.getTarget());
        shortJSON.put("clicks", su.getClicks());
        shortJSON.put("valid", su.getSafe());
        jArray.add(shortJSON);
      }
      jObject.put("urlList", jArray);
      return jObject;
    } catch (Exception e) {
      return null;
    }

  }

  public JSONObject metricToJSON(List<Metric> list) {
    JSONObject jObject = new JSONObject();

    try
    {
      JSONArray jArray = new JSONArray();
      for (Metric metric : list)
      {
        JSONObject shortJSON = new JSONObject();
        shortJSON.put( "uri", "http://" + UrlShortenerController.HOST + "/r/" + metric.getShortedUrl());
        shortJSON.put("target", metric.getUrl());
        shortJSON.put("clicks", metric.getClicks());
        shortJSON.put("valid", metric.isValid());
        jArray.add(shortJSON);
      }
      jObject.put("urlList", jArray);
      return jObject;
    } catch (Exception e) {
      return null;
    }
  }

  public boolean isExpired(String id){
    return shortURLRepository.isExpired(id);
  }

  public void delete(String id){
    shortURLRepository.delete(id);
  }

  /**
   * Save a ShortURL and tell the repository to save it
   *
   * @param url
   * @param sponsor
   * @param owner
   * @param ip
   * @param numMonth
   * @return saved?
   */

  public ShortURL save(String url, String sponsor, String owner, String ip, int numMonth) {

    ShortURL su = ShortURLBuilder.newInstance()
        .target(url, owner)
        .uri((String hash) -> linkTo(methodOn(UrlShortenerController.class).redirectTo(hash, null)).toUri())
        .sponsor(sponsor)
        .createdNow()
        .addDateExpiration(numMonth)
        .owner(owner)
        .temporaryRedirect()
        .treatAsSafe()
        .ip(ip)
        .unknownCountry()
        .build();

    return shortURLRepository.save(su);
  }

  /**
   * Check if the ShortURL is validated
   *
   * @param id
   * @return
   */
  public boolean isValidated(String id) {
//    System.out.println("---------------------" + findByKey(id).isValidated());
    return findByKey(id) != null && findByKey(id).isValidated();
  }
  public boolean validate(String url, boolean value){
    List<ShortURL> urls = shortURLRepository.findByTarget(url);
    if(urls.size() == 0)
      return false;
    System.out.println(urls.get(0).getHash() + value);
    return shortURLRepository.mark(urls.get(0), value) != null;
  }
}

package com.cloud.nsk.microservicegatewayzuul.aggregation;

import com.cloud.nsk.microservicegatewayzuul.user.User;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

/**
 * @author nsk
 * 2018/11/14 8:09
 */
@Service
@ConfigurationProperties(prefix = "application-service")
public class AggregationService {

    @Autowired
    private RestTemplate restTemplate;

    private String userUrl;

    private String movieUrl;

    public String getUserUrl() {
        return userUrl;
    }

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }

    public String getMovieUrl() {
        return movieUrl;
    }

    public void setMovieUrl(String movieUrl) {
        this.movieUrl = movieUrl;
    }

    @HystrixCommand(fallbackMethod = "fallback")
    public Observable<User> getUserById(Long id) {
        return Observable.create(observer -> {
            final String plainCreds = "admin:password2";
            final byte[] plainCredsBytes = plainCreds.getBytes();
            final byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
            final String base64Creds = new String(base64CredsBytes);
            final HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + base64Creds);
            final HttpEntity<String> request = new HttpEntity<String>(headers);
            final ResponseEntity<User> response = restTemplate.exchange(this.userUrl + id, HttpMethod.GET
                    , request, User.class);
            User user = response.getBody();
            observer.onNext(user);
            observer.onCompleted();
        });
    }

    @HystrixCommand(fallbackMethod = "fallback")
    public Observable<User> getMovieUserById(Long id) {
        return Observable.create(observer -> {
            User user = restTemplate.getForObject(this.movieUrl, User.class, id);
            observer.onNext(user);
            observer.onCompleted();
        });
    }

    public User fallback(Long id) {
        User user = new User();
        user.setId(-1L);
        return user;
    }
}

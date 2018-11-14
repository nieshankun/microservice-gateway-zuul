package com.cloud.nsk.microservicegatewayzuul.aggregation;

import com.cloud.nsk.microservicegatewayzuul.user.User;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import rx.Observable;
import rx.Observer;

import java.util.HashMap;

/**
 * @author nsk
 * 2018/11/14 8:26
 */
@RestController
public class AggregationController {

    public static final Logger LOGGER = LoggerFactory.getLogger(AggregationController.class);

    @Autowired
    private AggregationService aggregationService;

    @GetMapping("/aggregate/{id}")
    public DeferredResult<HashMap<String, User>> aggregate(@PathVariable Long id) {
        Observable<HashMap<String, User>> result = this.aggregateObservable(id);
        return this.toDeferredResult(result);
    }

    private Observable<HashMap<String, User>> aggregateObservable(Long id) {
        return Observable.zip(
                this.aggregationService.getUserById(id),
                this.aggregationService.getMovieUserById(id),
                (user, movieUser) -> {
                    HashMap<String,User> map = Maps.newHashMap();
                    map.put("user",user);
                    map.put("movieUser",movieUser);
                    return map;
                }
        );
    }

    public DeferredResult<HashMap<String, User>> toDeferredResult(Observable<HashMap<String, User>> details){
        DeferredResult<HashMap<String, User>> result = new DeferredResult<>();
        details.subscribe(new Observer<HashMap<String, User>>() {
            @Override
            public void onCompleted() {
                LOGGER.info("Completed...");
            }

            @Override
            public void onError(Throwable e) {
                LOGGER.error("A error is produced: ",e);
            }

            @Override
            public void onNext(HashMap<String, User> stringUserHashMap) {
                result.setResult(stringUserHashMap);
            }
        });
        return result;
    }

}

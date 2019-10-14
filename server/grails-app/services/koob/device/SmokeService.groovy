package koob.device

import grails.util.Holders
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.FullNettyClientHttpResponse
import io.reactivex.Flowable
import koob.http.SmokeClient
import org.springframework.beans.factory.annotation.Autowired

class SmokeService {

    @Autowired
    SmokeClient smokeClient

    void on(){
        if (Holders.config.smoke.enable)
            smokeClientCallBack(smokeClient.on(Holders.config.smoke.uri))
    }

    void off(){
        if (Holders.config.smoke.enable)
            smokeClientCallBack(smokeClient.off(Holders.config.smoke.uri))
    }

    void smokeClientCallBack(Flowable<HttpResponse<String>> httpResponse) {

        httpResponse.subscribe({ FullNettyClientHttpResponse it ->
            log.debug it.body?.get()
            log.debug ' === !!!smokeClientCallBack Success: httpCallBackResult fully populated !!! ==='
        }, { exception ->
            log.debug 'smokeClientCallBack httpResponse.onError : Consumer error (async listener): ' + exception.toString()
            exception.printStackTrace()
        }, { it ->
            log.debug "smokeClientCallBack Success httpResponse.onComplete >> Consumer completed"
        })
    }
}

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
            smokeClientCallBack(smokeClient.on())
    }

    void off(){
        if (Holders.config.smoke.enable)
            smokeClientCallBack(smokeClient.off())
    }

    void smokeClientCallBack(Flowable<HttpResponse<String>> httpResponse) {

        httpResponse.subscribe({ FullNettyClientHttpResponse it ->
            println it.body?.get()
            println ' === !!!smokeClientCallBack Success: httpCallBackResult fully populated !!! ==='
        }, { exception ->
            println 'smokeClientCallBack httpResponse.onError : Consumer error (async listener): ' + exception.toString()
            exception.printStackTrace()
        }, { it ->
            println "smokeClientCallBack Success httpResponse.onComplete >> Consumer completed"
        })
    }
}

package koob.device

import grails.util.Holders
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.FullNettyClientHttpResponse
import io.reactivex.Flowable
import koob.domain.visualization.HueEffect
import koob.http.LightingClient
import koob.utils.JsonService
import org.springframework.beans.factory.annotation.Autowired

class LightingService {

    @Autowired
    LightingClient lightingClient

    @Autowired
    JsonService jsonService

    void setState(String node, HueEffect hueEffect){
        if (Holders.config.hue.enable)
            httpCallback(lightingClient.setState(Holders.config.hue.user, node, jsonService.toJsonFromDomainTemplate(hueEffect)))
    }

    void httpCallback(Flowable<HttpResponse<String>> httpResponse) {
        httpResponse.subscribe({ FullNettyClientHttpResponse it ->
            println it.body?.get()
            println ' === !!!lightClientCallback Success: httpCallBackResult fully populated !!! ==='
        }, { exception ->
            println 'lightClientCallback httpResponse.onError : Consumer error (async listener): ' + exception.toString()
            exception.printStackTrace()
        }, { it ->
            println "lightClientCallback Success httpResponse.onComplete >> Consumer completed"
        })
    }


}

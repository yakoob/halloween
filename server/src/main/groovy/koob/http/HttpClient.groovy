package koob.http

import grails.util.Holders
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Put
import io.micronaut.http.client.annotation.Client
import io.reactivex.Flowable

interface LightingOperations {
    Flowable<HttpResponse<String>> setState(String apiSecret, String node, String json)
}

@Client("http://hue.yakoobahmad.com/api")
interface LightingClient extends LightingOperations {
    @Override
    @Put(uri="/{apiSecret}/lights/{node}/state", processes = "application/json")
    Flowable<HttpResponse<String>> setState(
            String apiSecret,
            String node,
            @Body String json
    )
}

@Client(id="SmokeClient")
interface SmokeClient {

    @Get(uri="http://{uri}/servo/5/60", processes = "application/json")
    Flowable<HttpResponse<String>> on(
            String uri
    )

    @Get(uri="http://{uri}/servo/5/90", processes = "application/json")
    Flowable<HttpResponse<String>> off(
            String uri
    )

}



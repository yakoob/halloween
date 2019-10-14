package koob.http

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Put
import io.micronaut.http.client.annotation.Client
import io.reactivex.Flowable

interface LightingOperations {
    Flowable<HttpResponse<String>> setState(String user, String node, String json)
}

@Client("{Holders.config.hue.api}")
interface LightingClient extends LightingOperations {
    @Override
    @Put(uri="/{user}/lights/{node}/state", processes = "application/json")
    Flowable<HttpResponse<String>> setState(
            String user,
            String node,
            @Body String json
    )
}

@Client("{Holders.config.smoke.api}")
interface SmokeClient {

    @Get(uri="/servo/5/60", processes = "application/json")
    Flowable<HttpResponse<String>> on()

    @Get(uri="/servo/5/90", processes = "application/json")
    Flowable<HttpResponse<String>> off()

}



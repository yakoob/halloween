package koob.http

import grails.util.Holders
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Put
import io.micronaut.http.client.annotation.Client
import io.reactivex.Flowable

interface LightingOperations {
    Flowable<HttpResponse<String>> setState(String user, String node, String json)
}

@Client("http://192.168.20.153/api/")
interface LightingClient extends LightingOperations {
    @Override
    @Put(uri="/{user}/lights/{node}/state", processes = "application/json")
    Flowable<HttpResponse<String>> setState(
            String user,
            String node,
            @Body String json
    )
}





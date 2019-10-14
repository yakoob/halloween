package koob.actor

import koob.http.LightingClient
import org.springframework.beans.factory.annotation.Autowired

class LightingService {

    @Autowired
    LightingClient lightingClient

    def getClient() {
        return lightingClient
    }
}

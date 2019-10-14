package koob.actor

import koob.http.SmokeClient
import org.springframework.beans.factory.annotation.Autowired

class SmokeService {

    @Autowired
    SmokeClient smokeClient

    def getClient() {
        return smokeClient
    }
}

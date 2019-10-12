package koob.actor

import grails.gorm.transactions.Transactional
import koob.http.LightingClient
import org.springframework.beans.factory.annotation.Autowired

@Transactional
class LightingService {

    @Autowired
    LightingClient lightingClient

    def getClient() {
        return lightingClient
    }
}

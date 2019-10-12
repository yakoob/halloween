package koob.config

import grails.util.Holders
import org.springframework.beans.factory.annotation.Value

trait GlobalConfig {

    // @Value('${mode.christmas}')
    boolean christmasEnabled = Holders.config.mode.christmas

    // @Value('${mode.hallweeen}')
    boolean halloweenEnabled = Holders.config.mode.hallweeen

    Boolean getHalloweenEnabled(){

        if (this.halloweenEnabled instanceof Boolean)
            return this.halloweenEnabled

        else if (this.halloweenEnabled instanceof String)
            return this.halloweenEnabled == 'true'

        return false

    }

    Boolean getChristmasEnabled(){

        if (this.christmasEnabled instanceof Boolean)
            return this.christmasEnabled

        else if (this.christmasEnabled instanceof String)
            return this.christmasEnabled == 'true'

        return false

    }

}
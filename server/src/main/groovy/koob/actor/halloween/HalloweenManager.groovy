package koob.actor.halloween

import akka.actor.ActorRef
import akka.actor.Props
import koob.actor.BaseActor
import koob.config.GlobalConfig
import grails.util.Holders
import groovy.util.logging.Log

@Log
class HalloweenManager extends BaseActor implements GlobalConfig {

    private static ActorRef lighting = null
    private static ActorRef smoke = null

    private static ActorRef projectorPumpkins = null
    private static ActorRef projectorHologram = null

    HalloweenManager() {

        if (halloweenEnabled){
            smoke = context.system().actorOf(Props.create(Smoke.class), "SmokeMachine")
            lighting = context.system().actorOf(Props.create(Lighting.class), "Lighting")

            projectorPumpkins = context.system().actorOf(Props.create(ProjectorPumpkins.class), "ProjectorPumpkins")
            projectorHologram = context.system().actorOf(Props.create(ProjectorHologram.class), "ProjectorHologram")

            println "Halloween manager started"

        } else {
            println "CAN NOT START - halloween not enabled"
        }

    }

    @Override
    void onReceive(Object message) throws Exception {
        // println "halloween man: " + message.dump()
    }

    static void tell(message, actor = ActorRef.noSender()){
        lighting.tell(message, actor)
        smoke.tell(message, actor)
        projectorPumpkins.tell(message, actor)
        projectorHologram.tell(message, actor)
    }

    static void tellLighting(message, actor = ActorRef.noSender()){
        lighting.tell(message, actor)
    }

    static void tellSmoke(message, actor = ActorRef.noSender()){
        smoke.tell(message, actor)
    }

    static void tellProjectors(message, actor = ActorRef.noSender()){
        projectorPumpkins.tell(message, actor)
        projectorHologram.tell(message, actor)
    }

    static void tellProjectorHolograms(message, actor = ActorRef.noSender()){
        projectorHologram.tell(message, actor)
    }

    static void tellProjectorPumpkins(message, actor = ActorRef.noSender()){
        projectorPumpkins.tell(message, actor)
    }


}

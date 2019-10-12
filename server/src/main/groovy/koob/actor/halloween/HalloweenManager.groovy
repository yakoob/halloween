package koob.actor.halloween

import akka.actor.ActorRef
import akka.actor.Props
import koob.actor.BaseActor
import koob.config.GlobalConfig
import grails.util.Holders
import groovy.util.logging.Log

@Log
class HalloweenManager extends BaseActor implements GlobalConfig {

    def akkaService = Holders.applicationContext.getBean("akkaService")

    private static ActorRef projectorPumpkins = null
    private static ActorRef projectorHologram = null

    private static ActorRef lighting = null

    HalloweenManager() {

        if (halloweenEnabled){
            // smokeMachine = context.system().actorOf(Props.create(koob.koob.actor.halloween.Smoke.class), "SmokeMachine")
            projectorPumpkins = context.system().actorOf(Props.create(ProjectorPumpkins.class), "ProjectorPumpkins")
            projectorHologram = context.system().actorOf(Props.create(ProjectorHologram.class), "ProjectorHologram")

            // projectorSam = context.system().actorOf(Props.create(Projector2.class), "ProjectorSam")
            lighting = context.system().actorOf(Props.create(Lighting.class), "Lighting")
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
        projectorPumpkins.tell(message, actor)
        projectorHologram.tell(message, actor)
        lighting.tell(message, actor)
    }

    static void tellLighting(message, actor = ActorRef.noSender()){
        lighting.tell(message, actor)
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

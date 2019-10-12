package koob.actor.halloween

import akka.actor.ActorRef
import akka.actor.Props
import koob.actor.BaseActor
import koob.device.light.Hue
import koob.event.SoundDetectionCalculationComplete
import groovy.util.logging.Log

@Log
class Lighting extends BaseActor {

    ActorRef huePortable


    Lighting(){

        Hue.withNewSession {

            def _huePortable = Hue.portable
            huePortable = context.system().actorOf(Props.create(Light.class, _huePortable), _huePortable.description)

            /*
            def p1 = Hue.findByDescription("Pumpkin_left")
            pumpkin1 = context.system().actorOf(Props.create(Light.class, p1), p1.description)
             */
        }

    }

    @Override
    void onReceive(Object message) throws Exception {

        // println "lighting " + message.dump()

        if (message instanceof SoundDetectionCalculationComplete){

            // println "lighting : SoundDetectionCalculationComplete: " + message.dump()

            huePortable?.tell(message, self)


            // light2?.tell(message, self)


        }

    }

}

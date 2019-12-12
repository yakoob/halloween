package koob.actor.christmas

import akka.actor.ActorRef
import akka.actor.Props
import groovy.util.logging.Log
import koob.actor.BaseActor

@Log
class ChristmasManager extends BaseActor {

    private static ActorRef projector = null

    ChristmasManager() {
        log.info "setup christmas projector"
        projector = context.system().actorOf(Props.create(ProjectorHologram.class), "ChristmasProjector")
    }

    @Override
    void onReceive(Object message) throws Exception {

        projector.tell(message, self)

    }

    static void tell(message, actor = ActorRef.noSender()){
        projector.tell(message, actor)
    }

}

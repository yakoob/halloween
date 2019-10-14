package koob.actor.christmas

import akka.actor.ActorRef
import akka.actor.Props
import koob.actor.BaseActor
import grails.util.Holders

class Manager extends BaseActor {

    def akkaService = Holders.applicationContext.getBean("akkaService")

    private ActorRef projector = null

    Manager() {
        log.info "setup christmas projector"
        projector = context.system().actorOf(Props.create(Projector.class), "ChristmasProjector")
    }

    @Override
    void onReceive(Object message) throws Exception {

        projector.tell(message, self)

    }

}

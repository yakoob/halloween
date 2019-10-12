package koob.actor

import akka.actor.UntypedActor
import grails.util.Holders
import groovy.util.logging.Log

@Log
class BaseActor  extends UntypedActor {

    def akkaService = Holders.applicationContext.getBean("akkaService")

    @Override
    void onReceive(Object message) throws Exception{
        log.severe("Impl for onReceive() needed...")
    }

    // this is until I figure out how to get a logger attached to Runnable interface
    public void loggingProxy(l){
        println(l.toString())
    }

}
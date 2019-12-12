package koob.actor

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Address
import akka.actor.Props
import akka.cluster.Cluster
import akka.cluster.ClusterEvent
import com.typesafe.config.ConfigFactory
import grails.gorm.transactions.Transactional
import koob.actor.christmas.ChristmasManager
import koob.actor.device.SoundDetection
import koob.actor.halloween.HalloweenManager
import koob.actor.networking.ClusterListener
import koob.config.GlobalConfig
import grails.gsp.PageRenderer

import groovy.util.logging.Log

import javax.annotation.PreDestroy

@Log
@Transactional
class AkkaService implements GlobalConfig {

    private static ActorSystem system
    private static final ActorRef ACTOR_NO_SENDER = ActorRef.noSender()

    ActorRef clusterListener
    ActorRef halloweenManager
    ActorRef christmasManger
    ActorRef soundDetection
    ActorRef twitter

    PageRenderer groovyPageRenderer

    def serverService

    void init() {

        log.info "Akka service running with app mode: christmas $christmasEnabled | halloween: $halloweenEnabled"

        // create koob.actor system
        if (halloweenEnabled && christmasEnabled)
            system = ActorSystem.create("HalloweenAndChristmas")
        else if (halloweenEnabled)
            system = ActorSystem.create("Halloween")
        else if (christmasEnabled)
            system = ActorSystem.create("Christmas")
        else
            system = ActorSystem.create("AppModeNotConfigured")

        log.info "Initialized Akka ActorSystem $system"
        actorSetup()
        def cluster = Cluster.get(system)
        ConfigFactory.parseString("akka.remote.netty.tcp.hostname=${serverService.thisServer.ipAddress}").withFallback(ConfigFactory.load())
        // create list of akka cluster seed nodes to join to
        List seeds = new LinkedList<Address>()
        // leader always goes first in list
        seeds.add(new Address("akka.tcp", system.name().toString(), serverService.thisServer.ipAddress, 2552))
        cluster.joinSeedNodes(akka.japi.Util.immutableSeq(seeds))
        cluster.subscribe(clusterListener, ClusterEvent.MemberEvent.class, ClusterEvent.UnreachableMember.class, ClusterEvent.LeaderChanged.class)

    }

    ActorSystem getSystem() {
        return system
    }

    void actorSetup() {

        clusterListener = actorOf(ClusterListener, "ClusterListener")

        if (christmasEnabled){
            christmasManger = actorOf(ChristmasManager, "ChristmasManger")
        }

        if (halloweenEnabled){

            log.info "about to create halloweenManager"
            halloweenManager = actorOf(HalloweenManager, "HalloweenManager")

            log.info "about to create sound detection"
            soundDetection = actorOf(SoundDetection, "SoundDetection")
            // twitter = actorOf(koob.koob.actor.social.Twitter, "Twitter")

        }
    }

    ActorRef actorNoSender() {
        return ACTOR_NO_SENDER
    }

    Props props(Class clazz) {
        assert clazz != null
        Props props = Props.create(clazz)
        return props
    }

    ActorRef actorOf(Props props) {
        assert props != null
        assert system != null

        ActorRef actor = system.actorOf(props)
        return actor
    }

    ActorRef actorOf(Props props, String name) {
        assert props != null
        assert name != null

        assert system != null

        ActorRef actor = system.actorOf(props, name)
        return actor
    }

    ActorRef actorOf(Class clazz) {
        assert clazz != null

        Props props = props(clazz)
        assert props != null
        assert system != null

        ActorRef actor = system.actorOf(props)
        return actor
    }

    ActorRef actorOf(Class clazz, String name) {
        assert clazz != null
        assert name != null

        Props props = props(clazz)
        assert props != null
        assert system != null

        ActorRef actor = system.actorOf(props, name)
        return actor
    }

    @PreDestroy
    void destroy() {
        system?.shutdown()
        system = null
        log.warning("destroying Akka ActorSystem: done.")
    }

}

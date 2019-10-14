package koob.actor.halloween

import akka.actor.ActorRef
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.FullNettyClientHttpResponse
import io.reactivex.Flowable
import koob.command.Command
import koob.command.halloween.BlowSmoke
import koob.command.halloween.StopSmoke
import koob.fsm.Guard
import koob.fsm.state.Any
import koob.fsm.state.Off
import koob.fsm.state.On
import koob.fsm.state.State
import grails.util.Holders
import groovy.util.logging.Slf4j
import scala.concurrent.duration.Duration

import java.util.concurrent.TimeUnit

@Slf4j
class Smoke extends koob.actor.device.Smoke {

    def smokeService = Holders.applicationContext.getBean("smokeService")

    public Smoke() {
        println "Smoke actor initialized"
        startStateMachine(Off)
        configureFsmDsl()
    }

    @Override
    void onReceive(Object message) throws Exception {
        log.info message.toString()
        super.onReceive(message)
        if (message instanceof Command)
            fsm.fire(message)
    }

    @Override
    void configureFsmDsl() {

        super.configureFsmDsl()

        fsm.record().onCommands([StopSmoke]).fromState(Any).goToState(Off).transition = { Command command ->
            toggleSmokeMachine(new Off())
        }

        fsm.record().onCommands([BlowSmoke]).fromState(Off).goToState(On).transition = { Command command ->

            log.info "blow smoke"

            if (smokeCoolOffTimer) {
                return new Guard(reason: "can not turn On smoke machine so often", payload: command)
            }

            smokeCoolOffTimer()

            toggleSmokeMachine(new On())

            /**
             * turn smoke off after 2 seconds
             */
            context.system().scheduler().scheduleOnce(Duration.create(3, TimeUnit.SECONDS),
                new Runnable() {
                    @Override
                    public void run() {
                        self.tell(StopSmoke.newInstance(), ActorRef.noSender())
                    }
                }, context.system().dispatcher()
            )


        }

    }

    private void toggleSmokeMachine(State state){
        if (Holders.config.smoke.enable) {
            koob.device.Smoke.withNewSession {
                if (state instanceof On) {
                    println "tell smoke machine client on()"
                    smokeClientCallBack(smokeService.client.on())
                }
                if (state instanceof Off) {
                    println "tell smoke machine client off()"
                    smokeClientCallBack(smokeService.client.off())
                }
            }
        }
    }

    void smokeClientCallBack(Flowable<HttpResponse<String>> httpResponse) {

        httpResponse.subscribe({ FullNettyClientHttpResponse it ->
            println it.body?.get()
            println ' === !!!smokeClientCallBack Success: httpCallBackResult fully populated !!! ==='
        }, { exception ->
            println 'smokeClientCallBack httpResponse.onError : Consumer error (async listener): ' + exception.toString()
            exception.printStackTrace()
        }, { it ->
            println "smokeClientCallBack Success httpResponse.onComplete >> Consumer completed"
        })
    }

}

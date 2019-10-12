package koob.actor.halloween

import akka.actor.ActorRef
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

    def httpClientService = Holders.applicationContext.getBean("httpClientService")

    public Smoke() {
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
             * turn smoke off after 2 secons
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

        koob.device.Smoke.withNewSession {

            if (state instanceof On)
                httpClientService.get("http://192.168.20.217/arduino/servo/5/60")
            if (state instanceof Off)
                httpClientService.get("http://192.168.20.217/arduino/servo/5/90")

            /*
            def smoke = koob.device.Smoke.findByNameAndState(
                koob.device.Smoke.Name.HALLOWEEN_REAR,
                koob.device.Smoke.getSmokeState(state)
            )

            if (smoke?.jsonTemplatePath) {
                // send to mqtt so arduino can process
                mqttClientService.publish(
                        "/halloween/smoke",
                        jsonService.toJsonFromDomainTemplate(smoke)
                )
            }
            */

        }

    }
}

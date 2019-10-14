package koob.actor.halloween

import akka.actor.ActorRef
import koob.command.Command
import koob.command.device.SmokeOn
import koob.command.device.SmokeOff
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
        log.info "Smoke actor initialized"
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

        fsm.record().onCommands([SmokeOff]).fromState(Any).goToState(Off).transition = { Command command ->
            toggleSmokeMachine(new Off())
        }

        fsm.record().onCommands([SmokeOn]).fromState(Off).goToState(On).transition = { Command command ->

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
                        self.tell(new SmokeOff(), ActorRef.noSender())
                    }
                }, context.system().dispatcher()
            )


        }

    }

    private void toggleSmokeMachine(State state){

        koob.device.Smoke.withNewSession {
            if (state instanceof On) {
                log.info "tell smoke machine client on()"
                smokeService.on()
            }
            if (state instanceof Off) {
                log.info "tell smoke machine client off()"
                smokeService.off()
            }
        }

    }



}

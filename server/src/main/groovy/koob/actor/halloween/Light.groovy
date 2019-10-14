package koob.actor.halloween

import akka.actor.ActorRef
import groovy.util.logging.Log
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.FullNettyClientHttpResponse
import io.reactivex.Flowable
import koob.actor.BaseActor
import koob.command.Command
import koob.command.TurnOff
import koob.command.TurnOn
import koob.device.light.Hue
import koob.domain.visualization.HueEffect
import koob.event.SoundDetectionCalculationComplete
import koob.fsm.FSM
import koob.fsm.state.Off
import koob.fsm.state.On
import koob.http.LightingClient
import koob.visualization.Color
import koob.visualization.ColorHue
import grails.util.Holders
import org.springframework.beans.factory.annotation.Autowired

@Log
class Light extends BaseActor implements FSM {

    def jsonService = Holders.applicationContext.getBean("jsonService")
    def lightingService = Holders.applicationContext.getBean("lightingService")

    Hue light

    List<Color> colorList = []
    def httpClientService
    // def httpClientService = Holders.applicationContext.getBean("httpClientService")

    Light(Hue hue) {

        log.info "starting hue light: ${hue.node}"

        Hue.withNewSession {
            light = hue
            colorList << Color.findByDescription(Color.Name.PURPLE)
            colorList << Color.findByDescription(Color.Name.ORANGE)
            colorList << Color.findByDescription(Color.Name.BLUE)
            colorList << Color.findByDescription(Color.Name.GREEN)
            colorList << Color.findByDescription(Color.Name.PINK)
            colorList << Color.findByDescription(Color.Name.RED)
            colorList << Color.findByDescription(Color.Name.WHITE)
            colorList << Color.findByDescription(Color.Name.BLACK)
        }

        startStateMachine(Off)
        configureFsmDsl()

    }

    @Override
    void onReceive(Object message) throws Exception {

        // println "light: " + message

        if (message instanceof String && message == "SHOW_CURRENT_STATE") {

            fsm.showState()

        } else if (message instanceof Command) {

            fsm.fire(message)

        } else if (message instanceof SoundDetectionCalculationComplete) {

            // log.info "fsmCS:${this.fsm.currentState}"

            double avg = message.avg?.toDouble()
            double sum = message.sum?.toDouble()


            if (avg < 1) {

                if (this.fsm.currentState == On.name)
                    self.tell(new TurnOff(), ActorRef.noSender())

            } else {

                if (fsm.currentState == Off.name)
                    self.tell(new TurnOn(payload: message?.clone()), ActorRef.noSender())

                setLightColor(avg)

            }


        } else if (message instanceof String) {

            log.info message

        }

    }

    @Override
    void configureFsmDsl() {

        fsm.record().onCommands([TurnOff]).fromState(On).goToState(Off).transition = { Command command ->
            turnLightOff()
        }

        fsm.record().onCommands([TurnOn]).fromState(Off).goToState(On).transition = { Command command ->
            turnLightOn()
        }

    }

    void turnLightOn() {
        send(light.node, HueEffect.On())
    }

    void turnLightOff() {
        send(light.node, HueEffect.Off())
    }

    void setLightColor(level) {

        if (!level)
            return

        double val = level?.toDouble()

        def res = []

        BrightnessCatagory brightnessCatagory = getBrightness(val)

        if (brightnessCatagory == BrightnessCatagory.NONE) {
            // do nothing
        } else if (brightnessCatagory == BrightnessCatagory.SOME && fsm.currentState == On.name) {

            res << colorList.find{it.description==Color.Name.GREEN}

        } else if (brightnessCatagory == BrightnessCatagory.SOME_MORE && fsm.currentState == On.name) {

            res << colorList.find{it.description==Color.Name.BLUE}

        } else if (brightnessCatagory == BrightnessCatagory.HALF && fsm.currentState == On.name) {

            res << colorList.find{it.description==Color.Name.PURPLE}

        } else if (brightnessCatagory == BrightnessCatagory.MORE && fsm.currentState == On.name) {

            Hue.withNewSession {

                if (self.path().name().contains("Garage") || self.path().name().contains("left")) {
                    res << colorList.find{it.description==Color.Name.PURPLE}
                }

                if (self.path().name().contains("Door") || self.path().name().contains("center")) {
                    res << colorList.find{it.description==Color.Name.ORANGE}
                }

                if (self.path().name().contains("Pumpkin") || self.path().name().contains("right")) {
                    res << colorList.find{it.description==Color.Name.PURPLE}
                }

            }


        } else if (brightnessCatagory == BrightnessCatagory.FULL && fsm.currentState == On.name) {

            res << colorList.find{it.description==Color.Name.PURPLE}
            res << colorList.find{it.description==Color.Name.ORANGE}
            res << colorList.find{it.description==Color.Name.RED}

        }

        if (res.size()) {
            res.each { Color color ->
                if (color instanceof ColorHue) {
                    color.hue.bri = brightnessCatagory.value
                    send(light.node, color.hue)
                }
            }
        }


    }

    void send(String node, HueEffect hueEffect){
        if (Holders.config.hue.enable)
            lightClientCallback(lightingService.client.setState(Holders.config.hue.user, node, jsonService.toJsonFromDomainTemplate(hueEffect)))
    }

    void lightClientCallback(Flowable<HttpResponse<String>> httpResponse) {

        httpResponse.subscribe({ FullNettyClientHttpResponse it ->

            println it.body?.get()

            println ' === !!!lightClientCallback Success: httpCallBackResult fully populated !!! ==='


        }, { exception ->
            println 'lightClientCallback httpResponse.onError : Consumer error (async listener): ' + exception.toString()
            exception.printStackTrace()
            // todo: send delivery report
        }, { it ->
            println "lightClientCallback Success httpResponse.onComplete >> Consumer completed"
            // todo: send delivery report
        }
        )
    }

    public enum BrightnessCatagory {
        NONE(0),SOME(25),SOME_MORE(50),HALF(125),MORE(200),FULL(255)
        private final int id
        BrightnessCatagory(int id) { this.id = id }
        public int getValue() { return id }
    }

    private BrightnessCatagory getBrightness(val){

        if (val == 0) {
            return BrightnessCatagory.NONE
        } else if (val <= 10) {
            return BrightnessCatagory.SOME
        } else if (val <= 25) {
            return BrightnessCatagory.SOME_MORE
        } else if (val <= 50) {
            return BrightnessCatagory.HALF
        } else if (val <= 75) {
            return BrightnessCatagory.MORE
        } else {
            return BrightnessCatagory.FULL
        }

    }
}
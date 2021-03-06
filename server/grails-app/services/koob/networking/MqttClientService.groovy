package koob.networking

import akka.actor.ActorRef
import koob.actor.christmas.ChristmasManager
import koob.actor.halloween.HalloweenManager
import koob.command.video.Play
import koob.command.video.PlayHologram
import koob.config.GlobalConfig
import koob.event.HologramPlaybackComplete
import koob.event.HologramPlaybackStarted
import koob.event.MediaPlaybackComplete
import koob.event.MediaPlaybackStarted
import koob.event.MotionDetected
import koob.event.PumpkinsPlaybackComplete
import koob.event.PumpkinsPlaybackStarted
import koob.event.SoundDetected
import groovy.util.logging.Slf4j
import koob.media.ChristmasVideo
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.springframework.beans.factory.annotation.Value

@Slf4j
class MqttClientService implements MqttCallback, GlobalConfig {

    public MqttClient mqttClient

    @Value('${mqtt.client.connectOnStartUp}')
    boolean connectOnStartup

    @Value('${mqtt.client.ip}')
    String ip

    @Value('${mqtt.client.port}')
    String port

    def akkaService
    def serverService
    def mqttSerializerService

    void init() {

        if (connectOnStartup) {
            mqttClient = new MqttClient("tcp://${ip}:${port}", "${serverService.thisServer.name}", mqttPersistence)
            mqttClient.connect()
            mqttClient.setCallback(this)
            mqttClient.setTimeToWait(3000)
            mqttClient.subscribe("#") // subscript to all topics
            log.info "mqtt client connected"
        }
    }

    private MemoryPersistence getMqttPersistence(){
        return new MemoryPersistence()
    }

    @Override
    public void connectionLost(Throwable me) {

        log.warn("msg "+me?.getMessage())
        log.warn("loc "+me?.getLocalizedMessage())
        log.warn("cause "+me?.getCause())
        log.warn("excep "+me?.toString())
        me?.printStackTrace()

        sleep(5000)
        mqttClient = null
        init()
    }

    @Override
    public void messageArrived(String topic, MqttMessage m) {
        try {

            log.debug "MqttClient.messageArrived >> topic:$topic | message:${m.toString()}"

            def message = mqttSerializerService.serialize(topic, m?.toString())

            if (message instanceof SoundDetected) {
                akkaService.soundDetection?.tell(message, akkaService.actorNoSender())
            }

            else if (message instanceof PumpkinsPlaybackStarted){
                HalloweenManager.tellProjectorPumpkins(message)
            }

            else if (message instanceof PumpkinsPlaybackComplete){
                HalloweenManager.tellProjectorPumpkins(message)
            }

            else if (message instanceof HologramPlaybackComplete){
                if (halloweenEnabled)
                    HalloweenManager.tellProjectorHolograms(message)
                if (christmasEnabled)
                    ChristmasManager.tell(message)
            }

            else if (message instanceof HologramPlaybackStarted){
                if (halloweenEnabled)
                    HalloweenManager.tellProjectorHolograms(message)
                if (christmasEnabled)
                    ChristmasManager.tell(message)
            }

            else if (message instanceof PlayHologram) {
                if (halloweenEnabled){
                    HalloweenManager.tellProjectorHolograms(Play.waiting())
                    HalloweenManager.tellProjectorHolograms(message)
                }
                if (christmasEnabled)
                    ChristmasManager.tell(message)
            }

            /*
            else if (message instanceof MotionDetected ){
                akkaService.homeManager?.tell(message, akkaService.actorNoSender())
            }
            */

        } catch (e) {
            e?.printStackTrace()
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // log.info "mqtt deliveryComplete: ${token.toString()}"
    }

    def publish(String topic, String payload){
        log.debug ">>> publish topic: $topic | payload: $payload"

        if (mqttClient) {
            MqttMessage message=new MqttMessage()
            message.setPayload(payload.bytes)
            mqttClient.publish(topic, message)
        } else {
            log.error("can not publish message $payload because mqttClient not configured")
        }
    }

}

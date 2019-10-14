package koob.utils

import com.google.gson.Gson
import grails.gorm.transactions.Transactional
import koob.command.video.PlayHologram
import koob.event.HologramPlaybackComplete
import koob.event.HologramPlaybackStarted
import koob.event.MediaPlaybackComplete
import koob.event.MediaPlaybackStarted
import koob.event.MotionDetected
import koob.event.PumpkinsPlaybackComplete
import koob.event.PumpkinsPlaybackStarted
import koob.event.SoundDetected
import koob.media.ChristmasVideo
import koob.media.HalloweenVideo
import grails.converters.JSON

@Transactional
class MqttSerializerService {

    def serialize(String topic, String message) {
        def json
        if (topic.contains("HomeGenie/HomeAutomation.ZWave/12/event")){
            def JsonObject = JSON.parse(message)
            if (JsonObject.Name == "Sensor.Tamper"){
                def res = new MotionDetected()
                return res
            }
            return
        }

        if (topic.contains("Aurduino/HomeAutomation.Audio/102/event")){
            def JsonObject = JSON.parse(message)
            def res = new SoundDetected()
            res.level = JsonObject.Value
            return res
        }

        if (topic.contains("halloween/actor/pumpkins")){

            json = JSON.parse(message)

            log.info " "
            log.info 'MQTT serializer pumpkins'
            log.info json.toString()
            log.info " "

            HalloweenVideo _media = new Gson().fromJson(message, HalloweenVideo.class)
            def res

            if (json.event == "playbackStarted"){
                res = new PumpkinsPlaybackStarted(topic: topic, media: _media)
                return res
            }

            if (json.event == "playbackComplete"){
                res = new PumpkinsPlaybackComplete(topic: topic, media: _media, next:json.next as boolean)
                return res
            }

            if (!json.event && json.command == "Play" && _media.hologram)
                return new PlayHologram(media: _media)

        } else if (topic.contains("halloween/actor/hologram")){

            json = JSON.parse(message)

            log.info " "
            log.info 'mqqt serializer hologram'
            log.info json.toString()
            log.info " "

            HalloweenVideo _media = new Gson().fromJson(message, HalloweenVideo.class)

            def res

            if (json.event == "playbackStarted"){
                res = new HologramPlaybackStarted(topic: topic, media: _media)
                return res
            }

            if (json.event == "playbackComplete"){
                res = new HologramPlaybackComplete(topic: topic, media: _media, next:json.next as boolean)
                return res
            }



        }

        /*
        if (topic.contains("ActorSystem/Halloween/Projector")){

            json = JSON.parse(message)
            Gson gson = new Gson()

            if (json.command == "songComplete"){

                def complete
                if (json.next)
                    complete = new MediaPlaybackComplete(topic: topic, next:true)
                else
                    complete = new MediaPlaybackComplete(topic: topic, next:false)
                return complete
            }

            if (json.event == "playbackStarted"){
                return new MediaPlaybackStarted(media:new Gson().fromJson(message, HalloweenVideo.class), topic: topic)
            }
        }

         */


        if (topic.contains("ActorSystem/Christmas/Projector")){

            json = JSON.parse(message)
            Gson gson = new Gson()

            if (json.command == "songComplete"){
                return new MediaPlaybackComplete()
            }

            if (json.event == "playbackStarted"){
                return new MediaPlaybackStarted(media:new Gson().fromJson(message, ChristmasVideo.class))
            }
        }

        if (topic.contains("ActorSystem/Christmas/Projector/Event")){
            log.info "!!! todo: !!!! implement christmas projector events"
            log.info message
            return
        }
    }

}

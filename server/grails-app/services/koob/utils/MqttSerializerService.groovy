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

            log.debug " "
            log.debug 'MQTT serializer pumpkins'
            log.debug json.toString()
            log.debug " "

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

            log.debug " "
            log.debug 'mqqt serializer hologram'
            log.debug json.toString()
            log.debug " "

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

        if (topic.contains("christmas/actor/hologram")){

            json = JSON.parse(message)
            Gson gson = new Gson()
            def res

            ChristmasVideo _media = gson.fromJson(message, ChristmasVideo.class)

            if (json.command == "playbackComplete"){
                return new HologramPlaybackComplete()
            }

            if (json.event == "playbackStarted"){
                res = new HologramPlaybackStarted(topic: topic, media: _media)
                return res
            }

        } else if (topic.contains("christmas/actor")){

            json = JSON.parse(message)

            /*
            if (json.lights == "relaxed"){
                return new MediaPlaybackComplete()
            }
             */
        }

    }

}

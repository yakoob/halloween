package koob

import koob.command.device.Shutdown
import koob.actor.christmas.ChristmasManager
import koob.actor.halloween.HalloweenManager
import koob.command.video.Mute
import koob.command.video.Play
import koob.command.video.UnMute
import koob.config.GlobalConfig
import koob.media.ChristmasVideo
import koob.media.HalloweenVideo

class TestController implements GlobalConfig {

    def akkaService

    def index() {
        if (halloweenEnabled)
            render view: '/test', model: [jokes: HalloweenVideo.findAllByType(HalloweenVideo.Type.JOKE), videos:HalloweenVideo.findAllByType(HalloweenVideo.Type.PUMPKINS), holograms:HalloweenVideo.findAllByType(HalloweenVideo.Type.HOLOGRAM)]
        if (christmasEnabled)
            render view: '/test', model: [holograms: ChristmasVideo.findAll()]
        return
    }

    def play(){

        String name = params.video
        log.info "controller play() $name"

        def v

        if (halloweenEnabled) {
            v = HalloweenVideo.findByName(HalloweenVideo.Name.valueOf(name.toUpperCase()))
            if (v.hologram)
                HalloweenManager.tellProjectorHolograms(new Play(media: v))
            else if (v.pumpkin)
                HalloweenManager.tellProjectorPumpkins(new Play(media: v))

        }

        if (christmasEnabled) {
            v = ChristmasVideo.findByName(ChristmasVideo.Name.valueOf(name.toUpperCase()))
            ChristmasManager.tell(new Play(media: v))
        }

        log.info "controller video: $v"

        redirect(action: "index")

        return

    }

    def mute(){

        if ( christmasEnabled )
            ChristmasManager.tell(new Mute())

        if ( halloweenEnabled ){
            HalloweenManager.tellProjectorPumpkins(new Mute())
            HalloweenManager.tellProjectorHolograms(new Mute())
        }

        redirect(action: "index")

        return

    }

    def shutdown(){

        if ( christmasEnabled )
            ChristmasManager.tell(new Shutdown())

        if ( halloweenEnabled ){
            HalloweenManager.tellProjectorPumpkins(new Shutdown())
            HalloweenManager.tellProjectorHolograms(new Shutdown())
        }

        redirect(action: "index")

        return

    }

    def unMute(){

        if ( christmasEnabled )
            ChristmasManager.tell(new UnMute())

        if ( halloweenEnabled ){
            HalloweenManager.tellProjectorPumpkins(new UnMute())
            HalloweenManager.tellProjectorHolograms(new UnMute())
        }

        redirect(action: "index")

        return

    }

}

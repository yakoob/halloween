package koob


import koob.actor.halloween.HalloweenManager
import koob.command.video.Play
import koob.media.HalloweenVideo

class TestController {

    def akkaService

    def index() {
        render view: '/test', model: [jokes: HalloweenVideo.findAllByType(HalloweenVideo.Type.JOKE), videos:HalloweenVideo.findAllByType(HalloweenVideo.Type.PUMPKINS), holograms:HalloweenVideo.findAllByType(HalloweenVideo.Type.HOLOGRAM)]
        return
    }

    def play(){
        String name = params.video
        log.info "controller play() $name"

        HalloweenVideo v = HalloweenVideo.findByName(HalloweenVideo.Name.valueOf(name.toUpperCase()))
        log.info "controller video: $v"
        if (v.hologram)
            HalloweenManager.tellProjectorHolograms(new Play(media: v))
        else if (v.pumpkin)
            HalloweenManager.tellProjectorPumpkins(new Play(media: v))

        redirect(action: "index")
        return
    }

}

package koob.command.video

import groovy.util.logging.Slf4j
import koob.command.Command
import koob.command.CommandableMedia
import koob.media.HalloweenVideo

@Slf4j
class Play extends Command implements CommandableMedia {

    static Play woods(){
       HalloweenVideo.withNewSession {
           HalloweenVideo w = HalloweenVideo.findByName(HalloweenVideo.Name.WOODS)
           return new Play(media: w)
       }
    }
    static Play waiting(){
        HalloweenVideo.withNewSession {
            HalloweenVideo w = HalloweenVideo.findByName(HalloweenVideo.Name.WAITING)
            return new Play(media: w)
        }
    }

   static Play video(HalloweenVideo _video){
       HalloweenVideo.withNewSession {
           return new Play(media: _video)
       }
   }

    @Override
    public String toString(){
        return "${media?.command} >> ${media?.name}"
    }

}


class PlayHologram extends Play {

    @Override
    static PlayHologram waiting(){
        HalloweenVideo.withNewSession {
            HalloweenVideo w = HalloweenVideo.findByName(HalloweenVideo.Name.WAITING)
            return new PlayHologram(media: w)
        }
    }

}
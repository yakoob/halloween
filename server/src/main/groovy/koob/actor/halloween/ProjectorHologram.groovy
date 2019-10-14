package koob.actor.halloween

import akka.actor.ActorRef
import akka.actor.Cancellable
import koob.actor.BaseActor
import koob.command.Command
import koob.command.CommandableMedia
import koob.command.video.Pause
import koob.command.video.Play
import koob.command.video.PlayHologram
import koob.command.video.Random
import koob.command.video.Resume
import koob.event.Event
import koob.event.HologramPlaybackComplete
import koob.event.MediaPlaybackComplete
import koob.event.PumpkinsPlaybackComplete
import koob.fsm.FSM
import koob.fsm.state.Any
import koob.fsm.state.Off
import koob.fsm.state.video.Loading
import koob.fsm.state.video.Paused
import koob.fsm.state.video.Playing
import koob.media.HalloweenVideo
import grails.util.Holders
import groovy.util.logging.Slf4j
import scala.concurrent.duration.Duration

import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit

@Slf4j
class ProjectorHologram extends BaseActor implements FSM {

    def jsonService = Holders.applicationContext.getBean("jsonService")
    def mqttClientService = Holders.applicationContext.getBean("mqttClientService")

    HalloweenVideo currentVideo
    HalloweenVideo previousVideo

    Cancellable randomVideoTimer

    Boolean canPlay = true

    CopyOnWriteArrayList<HalloweenVideo> playlistQueue = new CopyOnWriteArrayList<HalloweenVideo>()

    ProjectorHologram(){
        startStateMachine(Off)
        configureFsmDsl()
        playWaiting()
    }

    private boolean canPlay(){
        canPlay && ( ! currentVideo || currentVideo.isWaiting() )
    }

    @Override
    void onReceive(Object message) throws Exception {

        if (message instanceof Command)

            fsm.fire(message)

        else if ( message instanceof Event ) {

            if ( message instanceof MediaPlaybackComplete && ! currentVideo.isWaiting()){

                if (message instanceof HologramPlaybackComplete) {
                    playWaiting()
                }

            }

        }
        else
            log.info "ProjectorHologram.onReceive not handled for message: ${message.toString()}"

    }

    void playRandomVideo(){

        if (playlistQueue.size() +1 >= HalloweenVideo.hologramCount){
            playlistQueue.clear()
            if (previousVideo)
                playlistQueue.addIfAbsent(previousVideo)
        }

        def videos = HalloweenVideo.allHolograms

        videos.removeAll(playlistQueue)

        if(videos?.size()){

            Collections.shuffle(videos)

            HalloweenVideo selectedVideo = videos?.first()

            if (! playlistQueue.contains(selectedVideo) )
                playlistQueue.add(selectedVideo)

            log.info 'playlistQueue : ' + playlistQueue.toListString()

            log.info "selectedVideo is ${selectedVideo.name}"

            HalloweenManager.tellProjectorHolograms(new PlayHologram(media: selectedVideo))

        } else {
            log.warn "no videos found!!!"
        }

    }

    void playWaiting(){
        HalloweenManager.tellProjectorHolograms(new Play(media: HalloweenVideo.waiting))
    }

    @Override
    void configureFsmDsl() {

        fsm.record().onCommands([Play]).fromState(Any).goToState(Playing).transition = { Command command ->
            remoteDispatch(command)
        }

        fsm.record().onCommands([Off, Pause]).fromState(Any).goToState(Paused).transition = { Command command ->
            remoteDispatch(command)
        }

        fsm.record().onCommands([Resume]).fromState(Paused).goToState(Playing).transition = { Command command ->
            remoteDispatch(command)
        }

        fsm.record().onCommands([Random]).fromState(Any).goToState(Loading).transition = { Command command ->
            log.info "Projector Hologram >> fsm.record().onCommands([Random])"
            if ( canPlay() )
                playRandomVideo()
        }

        fsm.record().onCommands([PlayHologram]).fromState(Any).goToState(Playing).transition = { Command command ->
            if ( canPlay() )
                remoteDispatch(command)
        }

    }

    private void remoteDispatch(Command command){

        if (command instanceof CommandableMedia){

            previousVideo = currentVideo

            currentVideo = command?.media

            if (currentVideo?.jsonTemplatePath) {
                // send to mqtt so android can process
                mqttClientService.publish(
                        "halloween/projector/hologram",
                        jsonService.toJsonFromDomainTemplate(currentVideo)
                )

                startCanPlayTimer()
            }
        }

    }

    def startCanPlayTimer(){

        canPlay = false

        randomVideoTimer = context.system().scheduler().schedule(Duration.Zero(), Duration.create(5, TimeUnit.MINUTES),
                new Runnable() {
                    @Override
                    public void run() {
                        canPlay = true
                    }
                }, context.system().dispatcher())
    }


}


package koob.actor.halloween

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
import koob.event.MediaPlaybackComplete
import koob.event.MediaPlaybackStarted
import koob.event.PumpkinsPlaybackComplete
import koob.event.PumpkinsPlaybackStarted
import koob.fsm.FSM
import koob.fsm.Guard
import koob.fsm.state.Any
import koob.fsm.state.Off
import koob.fsm.state.video.Loading
import koob.fsm.state.video.Paused
import koob.fsm.state.video.Playing
import koob.media.HalloweenVideo
import grails.util.Holders
import groovy.util.logging.Slf4j
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit
import java.util.concurrent.CopyOnWriteArrayList

@Slf4j
class ProjectorPumpkins extends BaseActor implements FSM {

    def jsonService = Holders.applicationContext.getBean("jsonService")
    def mqttClientService = Holders.applicationContext.getBean("mqttClientService")

    HalloweenVideo currentVideo
    HalloweenVideo previousVideo

    Integer videoCounter = 0

    CopyOnWriteArrayList<HalloweenVideo> playlistQueue = new CopyOnWriteArrayList<HalloweenVideo>()

    Cancellable playMediaTimer

    ProjectorPumpkins(){

        println "Projector initialized with : " + self?.path()?.name()

        startStateMachine(Off)

        configureFsmDsl()

        playRandomVideo()

    }

    @Override
    void onReceive(Object message) throws Exception {

        println "projectorPumpkins $message"

        if (message instanceof Command)

            fsm.fire(message)

        else if ( message instanceof Event ) {

            if ( message.media instanceof HalloweenVideo) {

                HalloweenVideo _media = message.media

                if ( message instanceof MediaPlaybackComplete ){

                    println "self.tell(new Random(), self)"
                    self.tell(new Random(), self)

                } else if (message instanceof PumpkinsPlaybackStarted) {
                    if ( _media.theWoods )
                        HalloweenManager.tellProjectorHolograms(new Random())
                }
            }


        }
        else
            println "ProjectorPumpkins.onReceive not handled for message: ${message.toString()}"

    }

    private startTimerRandomMedia(Integer waitForSeconds=20){
        playMediaTimer?.cancel()
        playMediaTimer = context.system().scheduler().scheduleOnce(Duration.create(waitForSeconds, TimeUnit.SECONDS),
                new Runnable() {
                    @Override
                    public void run() {
                        if ( currentVideo.theWoods )
                            self.tell(new Random(), self)
                    }
                }, context.system().dispatcher())
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

            if (!currentVideo)
                return new Guard(reason: "ineligible video for command:Resume")

            remoteDispatch(command)

        }

        fsm.record().onCommands([Random]).fromState(Any).goToState(Loading).transition = { Command command ->
            println "Projector Pumpkins >> fsm.record().onCommands([Random])"
            playRandomVideo()
        }

    }

    private void remoteDispatch(Command command){

        if (command instanceof CommandableMedia && command.media instanceof HalloweenVideo) {

            this.previousVideo = this.currentVideo

            this.currentVideo = command?.media

            if (currentVideo?.theWoods) {
                startTimerRandomMedia(120)
            }

            if (currentVideo?.jsonTemplatePath) {
                // send to mqtt so android can process
                mqttClientService.publish(
                        "halloween/projector/pumpkins",
                        jsonService.toJsonFromDomainTemplate(currentVideo)
                )
            }
        }

    }
    private void playRandomVideo(){

        videoCounter = videoCounter + 1

        boolean playWoods = videoCounter % 4 == 0

        boolean playJoke = videoCounter % 2 == 0

        if (playlistQueue.size() +1 >= HalloweenVideo.pumpkinsCount){
            playlistQueue.clear()
            if (previousVideo)
                playlistQueue.addIfAbsent(previousVideo)
        }

        def videos

        videos = HalloweenVideo.allPumpkins

        videos.removeAll(playlistQueue)

        if (playWoods) {

            HalloweenManager.tellProjectorPumpkins(Play.video(HalloweenVideo.woods))

        } else if (playJoke){

            def jokes = HalloweenVideo.allJokes
            Collections.shuffle(jokes)
            HalloweenManager.tellProjectorPumpkins(Play.video(jokes?.first()))
            HalloweenManager.tellProjectorHolograms(PlayHologram.waiting())

        } else {

            if(videos?.size()){

                Collections.shuffle(videos)

                HalloweenVideo selectedVideo = videos?.first()

                if ( ! playlistQueue.contains(selectedVideo) )
                    playlistQueue.add(selectedVideo)

                log.debug "selectedVideo is ${selectedVideo.name}"

                HalloweenManager.tellProjectorPumpkins(Play.video(selectedVideo))

            } else {

                playlistQueue?.clear()
                playRandomVideo()

                log.warn "no videos found!!!"
            }
        }


    }

}


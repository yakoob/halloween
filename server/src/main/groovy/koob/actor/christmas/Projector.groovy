package koob.actor.christmas

import akka.actor.ActorRef
import akka.actor.Cancellable
import koob.actor.BaseActor
import koob.command.Command
import koob.command.CommandableMedia
import koob.command.video.Pause
import koob.command.video.Play
import koob.command.video.Resume
import koob.event.MediaPlaybackComplete
import koob.event.MediaPlaybackStarted
import koob.event.MotionDetected
import koob.fsm.FSM
import koob.fsm.Guard
import koob.fsm.state.Any
import koob.fsm.state.Off
import koob.fsm.state.video.Paused
import koob.fsm.state.video.Playing
import koob.media.ChristmasVideo
import grails.util.Holders
import groovy.util.logging.Slf4j
import scala.concurrent.duration.Duration

import java.util.concurrent.TimeUnit

@Slf4j
class Projector extends BaseActor implements FSM {

    def jsonService = Holders.applicationContext.getBean("jsonService")
    def mqttClientService = Holders.applicationContext.getBean("mqttClientService")

    ChristmasVideo currentVideo
    ChristmasVideo previousVideo
    ChristmasVideo idleVideo

    Cancellable randomVideoTimer


    Projector(){

        startStateMachine(Off)

        configureFsmDsl()

        startRandomVideoTimer()

    }

    @Override
    void onReceive(Object message) throws Exception {

        if (message instanceof String && message == "SHOW_CURRENT_STATE"){

            fsm.showState()
            log.info "currentVideo:${currentVideo?.name} | previousVideo: ${previousVideo?.name}"

        } else if (message instanceof Command) {

            fsm.fire(message)

        } else if (message instanceof MediaPlaybackComplete) {

            self.tell(new Play(media: idleVideo), ActorRef.noSender())

        } else if (message instanceof MediaPlaybackStarted) {
            if (message.media instanceof ChristmasVideo) {
                this.currentVideo = message.media
            }
        } else if (message instanceof MotionDetected){

            randomVideoTimer?.cancel()
            startRandomVideoTimer()

        }

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

    }

    private void remoteDispatch(Command command){

        if (command instanceof CommandableMedia) {

            this.previousVideo = this.currentVideo
            this.currentVideo = command?.media

            if (currentVideo?.jsonTemplatePath) {
                // send to mqtt so android can process
                mqttClientService.publish(
                        "christmas/video",
                        jsonService.toJsonFromDomainTemplate(currentVideo)
                )
            }
        }

    }

    private void startRandomVideoTimer(){

        randomVideoTimer = context.system().scheduler().schedule(Duration.Zero(), Duration.create(3, TimeUnit.MINUTES),
                new Runnable() {
                    @Override
                    public void run() {

                        try{

                            log.info "playing random video"

                            ChristmasVideo.withNewSession {

                                def videos = ChristmasVideo.findAll()

                                // log.info "christmas videos: " + videos.toListString()

                                if(videos?.size()){

                                    videos.removeAll([previousVideo])

                                    Collections.shuffle(videos)

                                    ChristmasVideo selectedVideo = videos?.first()

                                    log.info "TELL PROJECTOR TO PLAY NEW VIDEO: ${selectedVideo.name}"

                                    self.tell(new Play(media: selectedVideo), ActorRef.noSender())

                                } else {
                                    log.warn "no videos found!!!"
                                }
                            }
                        } catch(e){
                            e.printStackTrace()
                        }


                    }
                }, context.system().dispatcher())

    }

}


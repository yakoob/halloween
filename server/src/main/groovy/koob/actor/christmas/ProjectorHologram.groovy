package koob.actor.christmas

import koob.command.device.Shutdown
import akka.actor.ActorRef
import akka.actor.Cancellable
import grails.converters.JSON
import koob.actor.BaseActor
import koob.command.Command
import koob.command.CommandableMedia
import koob.command.video.Mute
import koob.command.video.Pause
import koob.command.video.Play
import koob.command.video.Resume
import koob.command.video.UnMute
import koob.config.GlobalConfig
import koob.event.HologramPlaybackComplete
import koob.event.HologramPlaybackStarted
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
class ProjectorHologram extends BaseActor implements FSM, GlobalConfig {

    def jsonService = Holders.applicationContext.getBean("jsonService")
    def mqttClientService = Holders.applicationContext.getBean("mqttClientService")

    ChristmasVideo currentVideo = ChristmasVideo.deckTheHalls
    ChristmasVideo previousVideo = ChristmasVideo.deckTheHalls

    Cancellable randomVideoTimer


    ProjectorHologram(){

        startStateMachine(Off)

        configureFsmDsl()

        startRandomVideoTimer()

    }

    @Override
    void onReceive(Object message) throws Exception {

        if (message instanceof String && message == "SHOW_CURRENT_STATE"){

            fsm.showState()
            log.info "currentVideo:${currentVideo?.name} | previousVideo: ${previousVideo?.name}"

        } else if (message instanceof Mute ){

            remoteDispatch(new Mute())

        } else if (message instanceof UnMute ){

            remoteDispatch(new UnMute())

        } else if (message instanceof Shutdown ){

            remoteDispatch(new Shutdown())

        } else if (message instanceof Command) {

            fsm.fire(message)

        } else if (message instanceof HologramPlaybackComplete) {

            startRandomVideoTimer()

        } else if (message instanceof HologramPlaybackStarted) {
            if (message.media instanceof ChristmasVideo) {
                this.currentVideo = message.media
            }
            unmute()
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

            println command.dump()

            this.previousVideo = this.currentVideo
            this.currentVideo = command?.media

            if (command instanceof Mute) {
                mqttClientService.publish(
                        "christmas/projector/hologram",
                        (['command':'Mute'] as JSON).toString(false)
                )
            }
            else if (command instanceof UnMute) {
                mqttClientService.publish(
                        "christmas/projector/hologram",
                        (['command':'UnMute'] as JSON).toString(false)
                )
            }
            else if (command instanceof Shutdown) {
                mqttClientService.publish(
                        "christmas/projector/hologram",
                        (['command':'Shutdown'] as JSON).toString(false)
                )
            }
            else if (currentVideo?.jsonTemplatePath) {
                mqttClientService.publish(
                        "christmas/projector/hologram",
                        jsonService.toJsonFromDomainTemplate(currentVideo)
                )
            }
        }

    }

    private void unmute(){

        context.system().scheduler().scheduleOnce(Duration.create(5, TimeUnit.SECONDS),
                new Runnable() {
                    @Override
                    public void run() {
                        println 'unmute'
                        ChristmasManager.tell(new UnMute())
                    }
                }, context.system().dispatcher()
        )
    }

    private void mute(){

        context.system().scheduler().scheduleOnce(Duration.create(5, TimeUnit.SECONDS),
                new Runnable() {
                    @Override
                    public void run() {
                        println 'mute'
                        ChristmasManager.tell(new Mute())
                    }
                }, context.system().dispatcher()
        )
    }

    private void startRandomVideoTimer(){

        randomVideoTimer?.cancel()

        randomVideoTimer = context.system().scheduler().scheduleOnce(Duration.create(5, TimeUnit.SECONDS),
                new Runnable() {
                    @Override
                    public void run() {

                        try{

                            log.info "playing random video"

                            ChristmasVideo.withNewSession {

                                def videos = ChristmasVideo.findAll()

                                // log.info "christmas videos: " + videos.toListString()

                                if(videos?.size()){

                                    videos.remove([previousVideo])

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


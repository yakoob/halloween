package koob.actor.social

import akka.actor.ActorRef
import akka.actor.Cancellable
import koob.actor.BaseActor
import koob.command.video.Play
import koob.media.HalloweenVideo
import grails.util.Holders
import groovy.transform.AutoClone
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j
import scala.concurrent.duration.Duration

import java.util.concurrent.TimeUnit

@Slf4j
class Twitter extends BaseActor {

    Cancellable gatherMentionsTimer

    LinkedList<Tweet> tweets = new LinkedList<Tweet>()

    long lastId = 784879638040293377


    def twitterService = Holders.applicationContext.getBean("twitterService")

    Twitter(){
        startGatheringTweets()
    }

    @Override
    void onReceive(Object message) throws Exception {

        if (message instanceof String){

            if (message == "NEXT"){

                // log.info "time till next twitter poll: ${gatherMentionsTimer}"

                log.info("Next: current statck: " + tweets?.toListString())

                if (tweets.size()){


                    Tweet t = tweets.sort{it.id}.first()

                    String text = t?.text
                    text = text?.replaceAll(" ","")
                    text = text?.replaceAll("@KoobsPumpkins","")

                    HalloweenVideo.withNewSession {

                        if (text == "GGG"){
                            akkaService.halloweenManager.tell(new Play(media: HalloweenVideo.findByName(Video.Name.GRIM_GRINNING_GHOST)), ActorRef.noSender())
                        } else if (text == "WT"){
                            akkaService.halloweenManager.tell(new Play(media: HalloweenVideo.findByName(Video.Name.WHATS_THIS)), ActorRef.noSender())
                        } else if (text == "OBP"){
                            akkaService.halloweenManager.tell(new Play(media: HalloweenVideo.findByName(Video.Name.OOGIE_BOOGIE_PUMPKINS)), ActorRef.noSender())
                        } else if (text == "KSC"){
                            akkaService.halloweenManager.tell(new Play(media: HalloweenVideo.findByName(Video.Name.KIDNAP_SANDY_CLAWS)), ActorRef.noSender())
                        } else if (text == "TIH"){
                            akkaService.halloweenManager.tell(new Play(media: HalloweenVideo.findByName(Video.Name.THIS_IS_HALLOWEEN)), ActorRef.noSender())
                        } else if (text == "MM"){
                            akkaService.halloweenManager.tell(new Play(media: Video.findByName(Video.Name.MONSTER_MASH)), ActorRef.noSender())
                        }

                        tweets.remove(t)

                    }


                }




            }

        }

    }

    private void startGatheringTweets(){

        gatherMentionsTimer = context.system().scheduler().schedule(Duration.Zero(), Duration.create(1, TimeUnit.MINUTES),
                new Runnable() {
                    @Override
                    public void run() {

                        def apiMentions = twitterService.mentions(lastId)

                        if (apiMentions?.size())
                            lastId = apiMentions.sort{it.id}?.last()?.id

                        log.info "gatherMentionsTimer running: API found:${apiMentions?.size()} tweets (${apiMentions?.toListString()}) with lastId: $lastId"

                        if (apiMentions?.size()){

                            tweets.addAll(apiMentions)

                        }

                    }
                }, context.system().dispatcher()
        )

    }


}

@TupleConstructor
@AutoClone
@ToString(includeNames = true)
class Tweet {
    long id
    String text
}
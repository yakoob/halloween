package koob.actor.device

import akka.actor.ActorRef
import groovy.util.logging.Log
import groovy.util.logging.Slf4j
import koob.actor.BaseActor
import koob.actor.halloween.HalloweenManager
import koob.event.SoundDetected
import koob.event.SoundDetectionCalculationComplete
import grails.util.Holders
import scala.concurrent.duration.Duration

import java.util.concurrent.TimeUnit

@Slf4j
class SoundDetection extends BaseActor {

    private soundDetections = []
    Integer soundDetectionsLastSum = 0

    // @Value('${automation.arduino.sound.detectionBuffer.size}')
    private Integer detectionBufferSize = 5

    // @Value('${automation.arduino.sound.detectionAvgDeviation.none}')
    private Integer detectionAvgDeviationNone = 5

    // @Value('${automation.arduino.sound.detectionAvgDeviation.lite}')
    private Integer detectionAvgDeviationLite = 10

    SoundDetection(){
        // test()
    }

    @Override
    void onReceive(Object message) throws Exception{

        if (message instanceof SoundDetected) {


            Integer val = message?.level?.toInteger()

            /**
             * map audio sensor values to phillips hue bridge values
             */
            if (val<=detectionAvgDeviationNone)         // no sound... 3 or less accounts for the sound of the projector fan: mic mounted here...
                val = 0
            else if(val<=detectionAvgDeviationLite)     // very light sound
                val = 1
            else if (val>1 && val<100)
                val = val
            else                // lots of sound
                val = 100

            soundDetections.add(val)

            def sum
            def avg

            if (soundDetections?.size()>=detectionBufferSize+1) {
                sum = soundDetections?.sum()
                avg = (soundDetections?.sum()/detectionBufferSize)
                log.debug "==================================================="
                log.debug "Sound(sum: $sum | avg: $avg)"
                log.debug "==================================================="
                soundDetections.clear()
                soundDetectionsLastSum = sum

                // set the usable value to the average of last 10 (250 milliseconds) of sound detection
                HalloweenManager.tellLighting(new SoundDetectionCalculationComplete(avg: avg, sum: sum), self)

            } else {

                // log.info "${soundDetections.toListString()}"
                // return // do not bother continuing this sub-routine if sound detection buffer isn't full

            }



        }

    }

    private void test(){

        context.system().scheduler().schedule(Duration.Zero(), Duration.create(500, TimeUnit.MILLISECONDS),
                new Runnable() {
                    @Override
                    public void run() {
                        Random random = new Random()
                        int randomNumber = random.nextInt(100-0) + 0
                        self.tell(new SoundDetected(level: randomNumber), ActorRef.noSender())
                    }
                }, context.system().dispatcher())


    }
}
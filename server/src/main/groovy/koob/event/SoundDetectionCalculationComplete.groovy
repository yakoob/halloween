package koob.event

import groovy.transform.TupleConstructor

@TupleConstructor
class SoundDetectionCalculationComplete extends Event {

    String sum
    String avg

}

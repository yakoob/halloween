package koob.event

import groovy.transform.AutoClone
import groovy.transform.ToString
import koob.domain.media.Media
import koob.media.HalloweenVideo

@AutoClone
@ToString(includeSuper = true)
class MediaPlaybackComplete extends Event {
    Media media
    Boolean next = false
}

@AutoClone
class PumpkinsPlaybackComplete extends MediaPlaybackComplete {
}

@AutoClone
class HologramPlaybackComplete extends MediaPlaybackComplete {
}
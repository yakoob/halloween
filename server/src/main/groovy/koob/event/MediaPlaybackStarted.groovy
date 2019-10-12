package koob.event

import groovy.transform.AutoClone
import koob.domain.media.Media
import groovy.transform.ToString

@AutoClone
@ToString(includeSuper = true)
class MediaPlaybackStarted extends Event {
    Media media
}

class PumpkinsPlaybackStarted extends MediaPlaybackStarted {}
class HologramPlaybackStarted extends MediaPlaybackStarted {}



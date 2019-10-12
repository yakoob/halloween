package koob.media

import koob.domain.media.Media
import koob.domain.utils.JsonSerializable
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(includes = "name")
class HalloweenVideo implements JsonSerializable, Media {
    enum Name {
        WAITING,
        WOODS,
        ADDAMS_FAMILY,
        BLUE_MOON,
        DAY_O,
        DEAD_MANS_PARTY,
        GHOST_HOST,
        GHOST_BUSTERS,
        GRIM_GRINNING_GHOST,
        JUMP_IN_THE_LINE,
        MONSTER_MASH,
        NIGHTMARE_ON_MY_STREET,
        RED_RIDING_HOOD,
        SEXY_AND_I_KNOW_IT,
        SOMEBODYS_WATCHING_ME,
        THEYRE_COMING,
        THIS_IS_HALLOWEEN,
        THRILLER,
        TIME_WARP,
        WEREWOLVES_IN_LONDON,
        YO_HO_HO,
        ZOMBIE_JAMBORE,
        BOHEMIAN_RHAPSODY,

        JOKE_COBWEBS, JOKE_DEADICATION, JOKE_EATING_CLOWN, JOKE_HOLLYWOOD_GHOST, JOKE_KNOCK_KNOCK, JOKE_LOOK_BOTH_WAYS, JOKE_MUMMY, JOKE_NOTHING_BUT_BEST, JOKE_PUMPKIN_PATCH, JOKE_STAIRING_AT, JOKE_TASTEFULL, JOKE_TOO_MUCH_CANDY, JOKE_UNDER_SKIN, JOKE_ZOMBIE_EYES,

        SAM_NOCOSTUME,SAM_SYMPHONY,SAM_SCARE1,SAM_SCARE2,SAM_SCARE3,SAM_SCARE4,
        BONEYARD_BAND,BONEYARD_PUMPKIN,
        OMINOUS_OCULI_SWARM,DRAGON_FULL,DRAGON_EYE,
        SPECTRAL_SURFACES,SPECTRAL_SCARE1,SPECTRAL_SCARE2,SPECTRAL_SCARE3,
        MONSTERS_BAND,MONSTERS_DANCE,MONSTERS_SCAREY,MONSTERS_SILY,
        JESTER_BALLOON,JESTER_CLOWNING,JESTER_JOYRIDE,JESTER_MECHANICAL,JESTER_NOWYOUSEEME,JESTER_SCARES,
        RAPTORS_1,RAPTORS_2,RAPTORS_3,RAPTORS_4,
        TREX_1,TREX_2,TREX_3,TREX_4,
        MOON,ZOMBIE_TWIST,WALL_BANGERS,YOU_AXED,BOO
    }
    Name name
    String command
    String event

    enum Type {PUMPKINS,HOLOGRAM,JOKE,DISABLED}
    Type type

    @Override
    public String getJsonTemplatePath(){
        return "/halloween/_video"
    }

    @Override
    String toString(){
        return "${this.class.simpleName} >> id: $id | $name | $command"
    }

    static constraints = {
        name nullable: true
        command nullable: true
        event nullable: true
        id bindable: true
    }

    boolean isWaiting(){
        name == Name.WAITING
    }

    boolean isTheWoods(){
        name == Name.WOODS
    }

    boolean isHologram(){
        type == Type.HOLOGRAM
    }

    static HalloweenVideo getWoods(){
        withNewSession {
            return HalloweenVideo.findByName(Name.WOODS)
        }
    }

    boolean isPumpkin(){
        type == Type.PUMPKINS || type == Type.JOKE
    }

    static List<HalloweenVideo> getAllPumpkins(){
        withNewSession {
            return HalloweenVideo.findAllByType(Type.PUMPKINS)
        }
    }

    static List<HalloweenVideo> getAllJokes(){
        withNewSession {
            return HalloweenVideo.findAllByType(Type.JOKE)
        }
    }

    static int getPumpkinsCount(){
        withNewSession {
            return HalloweenVideo.countByTypeInList([Type.PUMPKINS, Type.JOKE])
        }

    }

    static HalloweenVideo getWaiting(){
        withNewSession {
            return HalloweenVideo.findByName(Name.WAITING)
        }
    }

    static int getHologramCount(){
        withNewSession {
            return HalloweenVideo.countByTypeAndNameInList(Type.HOLOGRAM, [Name.SAM_NOCOSTUME, Name.SAM_SYMPHONY, Name.BONEYARD_BAND, Name.BONEYARD_PUMPKIN, Name.MONSTERS_BAND, Name.JESTER_MECHANICAL, Name.JESTER_BALLOON, Name.JESTER_SCARES, Name.SPECTRAL_SCARE3])
        }
    }

    static List<HalloweenVideo> getAllHolograms(){
        withNewSession {
            HalloweenVideo.findAllByTypeAndNameInList(HalloweenVideo.Type.HOLOGRAM, [Name.SAM_NOCOSTUME, Name.SAM_SYMPHONY, Name.BONEYARD_BAND, Name.BONEYARD_PUMPKIN, Name.MONSTERS_BAND, Name.JESTER_MECHANICAL, Name.JESTER_BALLOON, Name.JESTER_SCARES, Name.SPECTRAL_SCARE3])
        }
    }
}

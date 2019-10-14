package koob
import koob.config.GlobalConfig
import koob.device.light.Light
import koob.device.Smoke
import koob.device.light.Hue
import koob.media.ChristmasVideo
import koob.media.HalloweenVideo
import koob.visualization.Color
import koob.visualization.ColorHue

class BootStrap implements GlobalConfig {

    def akkaService
    def mqttClientService
    def mqttBrokerService

    def init = { servletContext ->
        configureModels()
        akkaService.init()
        mqttBrokerService.start()
        mqttClientService.init()
    }

    def destroy = {
        akkaService?.destroy()
        mqttBrokerService?.stop()
    }

    private void configureModels(){
        HalloweenVideo.withNewSession { configureDataHalloween() }
        // ChristmasVideo.withNewSession { configureDataChristmas() }
    }

    private void configureDataHalloween(){

        // new Smoke(name: Smoke.Name.HALLOWEEN_REAR, position: 20, state: Smoke.State.OFF).save(failOnError:true, flush:true)
        // new Smoke(name: Smoke.Name.HALLOWEEN_REAR, position: 60, state: Smoke.State.ON).save(failOnError:true, flush:true)
        HalloweenVideo.withNewTransaction {

            new HalloweenVideo(id: 1, name: HalloweenVideo.Name.WOODS, type: HalloweenVideo.Type.PUMPKINS).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 2, name: HalloweenVideo.Name.ADDAMS_FAMILY, type: HalloweenVideo.Type.PUMPKINS).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 3, name: HalloweenVideo.Name.BLUE_MOON, type: HalloweenVideo.Type.PUMPKINS).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 4, name: HalloweenVideo.Name.DAY_O, type: HalloweenVideo.Type.PUMPKINS).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 5, name: HalloweenVideo.Name.DEAD_MANS_PARTY, type: HalloweenVideo.Type.PUMPKINS).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 6, name: HalloweenVideo.Name.GHOST_HOST, type: HalloweenVideo.Type.PUMPKINS).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 7, name: HalloweenVideo.Name.GHOST_BUSTERS, type: HalloweenVideo.Type.PUMPKINS).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 8, name: HalloweenVideo.Name.GRIM_GRINNING_GHOST, type: HalloweenVideo.Type.PUMPKINS).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 9, name: HalloweenVideo.Name.JUMP_IN_THE_LINE, type: HalloweenVideo.Type.PUMPKINS).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 10, name: HalloweenVideo.Name.MONSTER_MASH, type: HalloweenVideo.Type.PUMPKINS).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 11, name: HalloweenVideo.Name.NIGHTMARE_ON_MY_STREET, type: HalloweenVideo.Type.PUMPKINS).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 12, name: HalloweenVideo.Name.RED_RIDING_HOOD, type: HalloweenVideo.Type.PUMPKINS).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 13, name: HalloweenVideo.Name.SEXY_AND_I_KNOW_IT, type: HalloweenVideo.Type.PUMPKINS).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 14, name: HalloweenVideo.Name.SOMEBODYS_WATCHING_ME, type: HalloweenVideo.Type.PUMPKINS).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 15, name: HalloweenVideo.Name.THEYRE_COMING, type: HalloweenVideo.Type.PUMPKINS).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 16, name: HalloweenVideo.Name.THIS_IS_HALLOWEEN, type: HalloweenVideo.Type.PUMPKINS).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 17, name: HalloweenVideo.Name.THRILLER, type: HalloweenVideo.Type.PUMPKINS).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 18, name: HalloweenVideo.Name.TIME_WARP, type: HalloweenVideo.Type.PUMPKINS).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 19, name: HalloweenVideo.Name.WEREWOLVES_IN_LONDON, type: HalloweenVideo.Type.PUMPKINS).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 20, name: HalloweenVideo.Name.YO_HO_HO, type: HalloweenVideo.Type.PUMPKINS).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 21, name: HalloweenVideo.Name.ZOMBIE_JAMBORE, type: HalloweenVideo.Type.PUMPKINS).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 22, name: HalloweenVideo.Name.BOHEMIAN_RHAPSODY, type: HalloweenVideo.Type.PUMPKINS).save(failOnError:true, flush:true)

            new HalloweenVideo(id: 23, name: HalloweenVideo.Name.JOKE_COBWEBS, type: HalloweenVideo.Type.JOKE).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 24, name: HalloweenVideo.Name.JOKE_DEADICATION, type: HalloweenVideo.Type.JOKE).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 25, name: HalloweenVideo.Name.JOKE_EATING_CLOWN, type: HalloweenVideo.Type.JOKE).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 26, name: HalloweenVideo.Name.JOKE_HOLLYWOOD_GHOST, type: HalloweenVideo.Type.JOKE).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 27, name: HalloweenVideo.Name.JOKE_KNOCK_KNOCK, type: HalloweenVideo.Type.JOKE).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 28, name: HalloweenVideo.Name.JOKE_LOOK_BOTH_WAYS, type: HalloweenVideo.Type.JOKE).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 29, name: HalloweenVideo.Name.JOKE_MUMMY, type: HalloweenVideo.Type.JOKE).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 30, name: HalloweenVideo.Name.JOKE_NOTHING_BUT_BEST, type: HalloweenVideo.Type.JOKE).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 31, name: HalloweenVideo.Name.JOKE_PUMPKIN_PATCH, type: HalloweenVideo.Type.JOKE).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 32, name: HalloweenVideo.Name.JOKE_STAIRING_AT, type: HalloweenVideo.Type.JOKE).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 33, name: HalloweenVideo.Name.JOKE_TASTEFULL, type: HalloweenVideo.Type.JOKE).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 34, name: HalloweenVideo.Name.JOKE_TOO_MUCH_CANDY, type: HalloweenVideo.Type.JOKE).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 35, name: HalloweenVideo.Name.JOKE_UNDER_SKIN, type: HalloweenVideo.Type.JOKE).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 36, name: HalloweenVideo.Name.JOKE_ZOMBIE_EYES, type: HalloweenVideo.Type.JOKE).save(failOnError:true, flush:true)

            new HalloweenVideo(id: 37, name: HalloweenVideo.Name.WAITING, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 38, name: HalloweenVideo.Name.SAM_NOCOSTUME, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 39, name: HalloweenVideo.Name.SAM_SYMPHONY, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 40, name: HalloweenVideo.Name.SAM_SCARE1, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 41, name: HalloweenVideo.Name.SAM_SCARE2, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 42, name: HalloweenVideo.Name.SAM_SCARE3, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 43, name: HalloweenVideo.Name.SAM_SCARE4, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 44, name: HalloweenVideo.Name.BONEYARD_BAND, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 45, name: HalloweenVideo.Name.BONEYARD_PUMPKIN, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 46, name: HalloweenVideo.Name.DRAGON_EYE, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 47, name: HalloweenVideo.Name.DRAGON_FULL, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 48, name: HalloweenVideo.Name.OMINOUS_OCULI_SWARM, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)

            new HalloweenVideo(id: 49, name: HalloweenVideo.Name.SPECTRAL_SURFACES, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 50, name: HalloweenVideo.Name.SPECTRAL_SCARE1, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 51, name: HalloweenVideo.Name.SPECTRAL_SCARE2, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 52, name: HalloweenVideo.Name.SPECTRAL_SCARE3, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)

            new HalloweenVideo(id: 53, name: HalloweenVideo.Name.MONSTERS_BAND, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 54, name: HalloweenVideo.Name.MONSTERS_DANCE, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 55, name: HalloweenVideo.Name.MONSTERS_SCAREY, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 56, name: HalloweenVideo.Name.MONSTERS_SILY, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)

            new HalloweenVideo(id: 57, name: HalloweenVideo.Name.JESTER_BALLOON, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 58, name: HalloweenVideo.Name.JESTER_CLOWNING, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 59, name: HalloweenVideo.Name.JESTER_JOYRIDE, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 60, name: HalloweenVideo.Name.JESTER_MECHANICAL, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 61, name: HalloweenVideo.Name.JESTER_NOWYOUSEEME, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 62, name: HalloweenVideo.Name.JESTER_SCARES, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)

            new HalloweenVideo(id: 63, name: HalloweenVideo.Name.MOON, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 64, name: HalloweenVideo.Name.ZOMBIE_TWIST, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 65, name: HalloweenVideo.Name.YOU_AXED, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 66, name: HalloweenVideo.Name.WALL_BANGERS, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)
            new HalloweenVideo(id: 67, name: HalloweenVideo.Name.BOO, type: HalloweenVideo.Type.HOLOGRAM).save(failOnError:true, flush:true)

            new ColorHue(description: Color.Name.PURPLE, red: "0.7117647058823499", green: "0.9724025974025973", blue: "0.9042207792207793").save(failOnError:true, flush:true)
            new ColorHue(description: Color.Name.BLUE, red: "0.6562091503267974", green: "0.9529220779220778", blue: "0.9334415584415585").save(failOnError:true, flush:true)
            new ColorHue(description: Color.Name.GREEN, red: "0.27320261437908283", green: "0.9724025974025973", blue: "1").save(failOnError:true, flush:true)
            new ColorHue(description: Color.Name.PINK, red: "0.8215686274509816", green: "1", blue: "1").save(failOnError:true, flush:true)
            new ColorHue(description: Color.Name.ORANGE, red: "0.09542483660130567", green: "1", blue: "1").save(failOnError:true, flush:true)
            new ColorHue(description: Color.Name.RED, red: "0.9797385620915028", green: "1", blue: "1").save(failOnError:true, flush:true)
            new ColorHue(description: Color.Name.BLACK, red: "0", green: "0", blue: "0").save(failOnError:true, flush:true)
            new ColorHue(description: Color.Name.WHITE, red: "0", green: "0", blue: "1").save(failOnError:true, flush:true)

            // new Hue(description: "Pumpkin_left", node: 4, state: Light.State.OFF, color: ColorHue.findByDescription(Color.Name.ORANGE)).save(failOnError:true, flush:true)
            // new Hue(description: "Pumpkin_center", node: 7, state: Light.State.OFF, color: ColorHue.findByDescription(Color.Name.ORANGE)).save(failOnError:true, flush:true)
            new Hue(description: "Portable", node: "8", state: Light.State.OFF, color: ColorHue.findByDescription(Color.Name.ORANGE)).save(failOnError:true, flush:true)
            /*
            new Hue(description: "Front_Door", node: 16, state: Light.State.OFF, color: ColorHue.findByDescription(Color.Name.ORANGE)).save(failOnError:true, flush:true)
            new Hue(description: "Garage_1", node: 9, state: Light.State.OFF, color: ColorHue.findByDescription(Color.Name.ORANGE)).save(failOnError:true, flush:true)
            new Hue(description: "Garage_2", node: 10, state: Light.State.OFF, color: ColorHue.findByDescription(Color.Name.ORANGE)).save(failOnError:true, flush:true)
            new Hue(description: "Garage_3", node: 11, state: Light.State.OFF, color: ColorHue.findByDescription(Color.Name.ORANGE)).save(failOnError:true, flush:true)
            new Hue(description: "Door_1", node: 12, state: Light.State.OFF, color: ColorHue.findByDescription(Color.Name.ORANGE)).save(failOnError:true, flush:true)
            new Hue(description: "Door_2", node: 13, state: Light.State.OFF, color: ColorHue.findByDescription(Color.Name.ORANGE)).save(failOnError:true, flush:true)
            new Hue(description: "Door_3", node: 14, state: Light.State.OFF, color: ColorHue.findByDescription(Color.Name.ORANGE)).save(failOnError:true, flush:true)
            new Hue(description: "Door_4", node: 15, state: Light.State.OFF, color: ColorHue.findByDescription(Color.Name.ORANGE)).save(failOnError:true, flush:true)
            */
        }

    }

    private void configureDataChristmas(){

        new ChristmasVideo(name: ChristmasVideo.Name.DECK_THE_HALLS).save(failOnError:true, flush:true)
        new ChristmasVideo(name: ChristmasVideo.Name.GREAT_GIFT_WRAP).save(failOnError:true, flush:true)
        new ChristmasVideo(name: ChristmasVideo.Name.MARCH_WOODEN_SOLDIER).save(failOnError:true, flush:true)
        new ChristmasVideo(name: ChristmasVideo.Name.PACKING_SANTA_SLEIGH).save(failOnError:true, flush:true)
        new ChristmasVideo(name: ChristmasVideo.Name.TOY_TINKERING).save(failOnError:true, flush:true)

    }
}

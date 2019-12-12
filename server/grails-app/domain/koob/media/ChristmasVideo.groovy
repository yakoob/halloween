package koob.media

import koob.domain.media.Media
import koob.domain.utils.JsonSerializable

class ChristmasVideo implements JsonSerializable, Media {

    enum Name {DECK_THE_HALLS, GREAT_GIFT_WRAP, MARCH_WOODEN_SOLDIER, PACKING_SANTA_SLEIGH, TOY_TINKERING, YULETIDE, SNOWMAN_SKATE, SNOWMAN_TREE}

    Name name
    String command
    String event

    @Override
    public String getJsonTemplatePath(){
        return "/christmas/_video"
    }

    @Override
    String toString(){
        return "${this.class.simpleName} >> id: $id | $name | $command"
    }

    static constraints = {
        name nullable: true
        command nullable: true
        event nullable: true
    }

    static ChristmasVideo getDeckTheHalls(){
        withNewSession {
            return ChristmasVideo.findByName(Name.DECK_THE_HALLS)
        }
    }
}
package koob.media

import koob.domain.media.Media
import koob.domain.utils.JsonSerializable

class ChristmasVideo implements JsonSerializable, Media {

    enum Name {DECK_THE_HALLS, GREAT_GIFT_WRAP, MARCH_WOODEN_SOLDIER, PACKING_SANTA_SLEIGH, TOY_TINKERING}

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
}
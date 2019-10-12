package koob.device

import koob.domain.utils.JsonSerializable
import koob.fsm.state.Off
import koob.fsm.state.On

class Smoke implements JsonSerializable {

    enum Name {HALLOWEEN_REAR}
    enum State {OFF,ON}

    Name name
    State state
    int position

    void setPosition(int pos){
        this.position = pos
    }

    static Smoke.State getSmokeState(koob.fsm.state.State s){
        if (s instanceof On)
            return koob.device.Smoke.State.ON
        if (s instanceof Off)
            return koob.device.Smoke.State.OFF
    }

    @Override
    public String getJsonTemplatePath(){
        return "/device/smoke/_smoke"
    }

    @Override
    String toString(){
        return "${this.class.simpleName} >> id: $id | $name | $state: $position"
    }

    static constraints = {

    }
}

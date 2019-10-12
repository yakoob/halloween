package koob.fsm.state

import koob.fsm.FiniteStateMachine

trait State implements Serializable {

    public Boolean is(FiniteStateMachine fsm){
        if (this.class.name == fsm.currentState)
            return true
        return false
    }

}
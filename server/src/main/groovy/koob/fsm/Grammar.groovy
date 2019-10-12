package koob.fsm

import koob.fsm.state.State
import groovy.util.logging.Log

/**
 * used to define the FSM Transitional behavior
 */
@Log
class Grammar {

    FiniteStateMachine fsm

    String fromState
    String toState

    List<String> eventMatches = []

    Closure transition

    Grammar(a_fsm) {
        fsm = a_fsm
    }
    
    def onCommands(List<Class<State>> events) {
        events.each { e -> eventMatches << e.canonicalName }
        return this
    }

    def fromState(Class<State> fs) {
        fromState = fs.canonicalName
        return this
    }

    def goToState(Class<State> ts) {
        toState = ts.canonicalName
        fsm.registerTransition(this)
        return this
    }

    @Override
    public String toString() {
        "commands: ${eventMatches.toListString()} <-- from:$fromState"
    }
}

/**
 * used to stop a FSM transition from occurring
 */
class Guard {
    String reason
    def payload
}
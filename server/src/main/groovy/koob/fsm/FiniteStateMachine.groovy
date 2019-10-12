package koob.fsm

import koob.command.Command
import koob.event.Event
import koob.fsm.state.State
import groovy.util.logging.Slf4j

@Slf4j
class FiniteStateMachine {

    String parent
    String initialState
    String previousState
    String currentState

    List<Grammar> transitions = []

    /**
     * Defines a memento of state(s) with valid state transitions while brokering the runtime transition between states
     * @param startingState
     */
    FiniteStateMachine(Class<State> startingState){
        assert startingState, "You need to provide an initial state"
        initialState = startingState.canonicalName
        currentState = startingState.canonicalName
        parent = callerClassName
    }

    /**
     * creates an instance of grammar to be used for each FSM DSL declaration
     */
    Grammar record() {
        Grammar.newInstance(this)
    }

    /**
     * register the FSM dsl transitions
     * @param grammar
     */
    void registerTransition(Grammar grammar) {
        transitions << grammar
    }

    /**
     * Actor system onRecieve handler will process messages in an orderly fashion through the FSM via this method
     * if the transition closure returns a Guard.class the state machine will stay put.
     */
    def fire(e) {

        if (e instanceof Command) {

            def command = e

            def thisState = this.currentState

            if (e instanceof String)
                log.info(e)
            else
                e = e.class.canonicalName

            /**
             * look for qualifying transitions
             */
            def transitionMatches = transitions.findAll { Grammar it ->
                (e in it.eventMatches && thisState == it.fromState) ||
                        (e in it.eventMatches && it.fromState.contains("state.Any"))
            }

            if (transitionMatches.size()){

                transitionMatches.each { Grammar it ->

                    // log.info "[FSM:MATCH] $parent with command:($e) on currentState:(${currentState}) for grammar: (${it.toString()})"

                    def result = it.transition(command)

                    // check if transition closure was guarded for 1 or more domain specific reason
                    if (result instanceof Guard) {
                        log.info "FSM $parent: guarded transition for ${it.fromState} >> ${it.toState} becuase ${result.reason}"
                    }
                    // transition closure executed properly so change the finite state
                    else {

                        this.previousState = this.currentState
                        this.currentState = it.toState

                        // log.info "fsm setPreviousState: ${this.previousState}"
                        // log.info "fsm setCurrentState: ${this.currentState}"
                    }

                }

            }

            return currentState

        }

    }

    void reset() {
        currentState = initialState
    }

    public void showState(){
        log.info("[FSM:STATUS] for $parent is reporting its current state as: ${currentState.toString()} from previous state of: ${previousState} with initialState of: ${initialState}")
    }

    private String getCallerClassName(){
        Thread.currentThread().getStackTrace().find { it.className.contains("koob") && !it.className.contains("FSM") && !it.className.contains("koob.fsm.FiniteStateMachine") }?.className
    }

}

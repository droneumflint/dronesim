package stateMachine;

import java.util.List;
import java.util.Set;

public class FSM {
	List<Transition> transitions;
	State current;
	
	FSM(State start, List<Transition> transitions) {
		this.current = start;
		this.transitions = transitions;
	}
	
	void apply(Set<Event> events) {
		current = getNextState(events);
	}
	
	State getNextState(Set<Event> events){
		for(Transition transition : transitions) {
			boolean currentStateMatches = transition.from.equals(current);
			boolean conditionsMatch = transition.events.equals(events);
			if(currentStateMatches && conditionsMatch) {
				return transition.to;
			}
		}
		return null;
	}
}
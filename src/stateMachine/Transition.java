package stateMachine;

import java.util.Set;

public class Transition {
	
	State from;
	Set<Event> events;
	State to;
}
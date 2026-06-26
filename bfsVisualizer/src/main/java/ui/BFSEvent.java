package ui;

public class BFSEvent {
	EventStates state;
	String value;
	
	public BFSEvent(EventStates e, String val) {
		state = e;
		value = val;
	}
}

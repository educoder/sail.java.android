package org.encorelab.sail;

public abstract class EventResponder {
	protected String eventType;
	
	public abstract void triggered(Event ev);
	
	public String getEventType() {
		return eventType;
	}
}

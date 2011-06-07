package us.exultant.ahs.util.event;

/**
 * The equality operator on an Event is used to determine to what listener it is sent by
 * EventDisbatch -- thus, typically an Event will have some fields used to determine the
 * event's "type" (such as a String name that comes from a predefined pool or some such)
 * which IS included in the definition of equality, and may contain other fields that
 * contain data specific to just one instance of an event of that "type" which are NOT
 * included in the definition of equality.
 * 
 * @author hash
 * 
 */
public interface Event {
	
}

package us.exultant.ahs.core;

/**
 * <p>
 * Generic interface for listeners (whether for events or messages). Collaborates with the
 * Transport interface.
 * </p>
 * 
 * <p>
 * It is advised in most situations that classes should actually contain a nested class
 * that implements this Listener interface; this allows messages of different types to be
 * routed through one object without name collisions or bad encapsulation breaks (or
 * revealing more functions than necessary on the base class).
 * </p>
 * 
 * <p>
 * Listeners may be presumed to be reentrant or otherwise <b>thread-safe</b> whenever
 * applied in the threading module of the AHS library! If a Listener cannot be safely
 * entered by multiple threads concurrently, the {@link #hear(Object)} method should be
 * synchronized and this departure from theme should be loudly documented due to the
 * potential for this synchronization to have a large performance impact.
 * </p>
 * 
 * @author hash
 * 
 * @param <$M>
 *                the message/event type
 */
public interface Listener<$M> {
	/**
	 * Hear (and respond to) the given event/message.
	 * 
	 * @param $m
	 */
	public void hear($M $m);
}
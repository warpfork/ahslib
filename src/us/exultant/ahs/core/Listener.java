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

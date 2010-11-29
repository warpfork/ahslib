package ahs.util.event;

import ahs.util.*;

import java.util.*;

/**
 * <p>
 * This is designed so that (roughly) semantically whenever a user opens a new menu or
 * window in their interface, a new EventDispatch is added as the first composite to the
 * previously most recent EventDispatch; when the window is closed, that object is popped
 * off the list again. In this way, the most recent (a.k.a. 'top') window or menu will get
 * the first chance to react to events, and then events bubble out again to the other
 * dispatchers.
 * </p>
 * 
 * <p>
 * Listeners enrolled in an EventDispatch must be careful to be either reentrant or
 * synchronized -- they may receive events from any thread that EventDispatch does, and
 * there's no guarantee that multiple threads won't create events faster than a particular
 * listener can complete its processing of them.
 * </p>
 * 
 * @author hash
 * 
 * @param <$T>
 */
public class EventDispatch<$T extends Event> implements Listener<$T> {
	public EventDispatch() {
		$composites = Collections.synchronizedList(new ArrayList<EventDispatch<$T>>(1));
		$map = Collections.synchronizedMap(new HashMap<$T,Listener<$T>>());
	}
	
	/**
	 * <p>
	 * Dispatches the given Event to an appropriate Listener in a thread-safe fashion.
	 * </p>
	 * 
	 * <p>
	 * Other EventDispatch instances in the List returned by {@link #getComposites()}
	 * are first checked in order to see if they contain Listener for the Event; this
	 * proceeds recursively. If a Listener is found, the Event is immediately
	 * dispatched to that Listener an execution returns. If no Listener is found in
	 * the list of composites, then this EventDispatch is checked last.
	 * </p>
	 */
	public void hear($T $x) {
		accept($x);
	}
	
	/**
	 * <p>
	 * Internally checks child dispatchers for acceptance, and then tries itself.
	 * </p>
	 * 
	 * @return true if this dispatcher or one of its children had a listener for the
	 *         event (and thus the event was dispatched); false if not.
	 */
	protected boolean accept($T $x) {
		synchronized ($composites) {
			for (EventDispatch<$T> $composite : $composites)
				if ($composite.accept($x)) return true;
			Listener<$T> $spoon = $map.get($x);
			if ($spoon == null) return false;
			$spoon.hear($x);
			return true;
		}
	}
	
	private List<EventDispatch<$T>>	$composites;
	private Map<$T,Listener<$T>>	$map;
	
	/**
	 * <p>
	 * Use this method to modify the map used within this dispatch. The map is
	 * thread-safe, but if multiple operations are required in an atomic block,
	 * synchronize on the map.
	 * </p>
	 */
	public Listener<$T> put($T $eventPrototype, Listener<$T> $handler) {
		return $map.put($eventPrototype, $handler);
	}
	
	/**
	 * <p>
	 * Use this method to modify the map used within this dispatch. The map is
	 * thread-safe, but if multiple operations are required in an atomic block,
	 * synchronize on the map.
	 * </p>
	 */
	// not sure i need or want to expose quite this much
	public Map<$T,Listener<$T>> getMap() {
		return $map;
	}
	
	/**
	 * <p>
	 * Mutate this list to control the order in which other EventDispatch instances
	 * are checked for Listeners when events come in to this dispatcher.
	 * </p>
	 * 
	 * <p>
	 * If you have an event dispatching system working in multithreaded conditions and
	 * need to make more than one operation on this list atomically, just synchronize
	 * on the list. (The list is already a synchronized list interally, so if you only
	 * need to add or remove a single element, this is already atomic and does not
	 * demand additional synchronization.)
	 * </p>
	 */
	public List<EventDispatch<$T>> getComposites() {
		return $composites;
	}
	
	public static class Debug<$T extends Event> extends EventDispatch<$T> {
		/**
		 * <p>
		 * Same as for the superclass, except that if no Listener can be found for
		 * an Event anywhere in this dispatcher or any of its children, then a
		 * runtime exception is thrown.
		 * </p>
		 * 
		 * @throws NoListenerFoundException
		 */
		public void hear($T $x) {
			if (!accept($x)) { throw new NoListenerFoundException(); }
		}
		
		public static class NoListenerFoundException extends IllegalStateException {
			public NoListenerFoundException() { super(); }
			public NoListenerFoundException(String $arg0, Throwable $arg1) { super($arg0, $arg1); }
			public NoListenerFoundException(String $arg0) { super($arg0); }
			public NoListenerFoundException(Throwable $arg0) { super($arg0); }
		}
	}
}

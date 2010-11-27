package ahs.util.event;

import ahs.util.*;

import java.util.*;

public class EventDispatch<$T extends Event> implements Listener<$T> {
	public EventDispatch() {
		$composites = Collections.synchronizedList(new ArrayList<EventDispatch<$T>>(1));
		$map = Collections.synchronizedMap(new HashMap<$T,Listener<$T>>());
	}
	public EventDispatch(String $wef) {
		this();
		Listener<NamedEvent> $t = new Listener<NamedEvent>() {
			public void hear(NamedEvent $x) {
			}
		};
		$map.put(new NamedEvent($wef), $t);
	}
	
	public void hear($T $x) {
		accept($x);
	}
	
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

	public Listener<$T> put($T $eventPrototype, Listener<$T> $handler) {
		return $map.put($eventPrototype, $handler);
	}
	
	// not sure i need or want to expose quite this much
	public Map<$T,Listener<$T>> getMap() {
		return $map;
	}
	
	/**
	 * Mutate this list to control the order in which other EventDispatch instances
	 * are checked for Listeners when events come in to this dispatcher. Whenever this
	 * EventDispatch hears an event,
	 * 
	 * If you have an event dispatching system working in multithreaded conditions and
	 * need to make more than one operation on this list atomically, just synchronize
	 * on the list. (The list is already a synchronized list interally, so if you only
	 * need to add or remove a single element, this is already atomic and does not
	 * demand additional synchronization.)
	 */
	public List<EventDispatch<$T>> getComposites() {
		return $composites;
	}
}

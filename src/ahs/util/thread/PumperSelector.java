package ahs.util.thread;

import ahs.util.*;

import java.io.*;
import java.nio.channels.*;
import java.util.*;

/**
 * If using this with a ReadHead that is backed by NIO channels, you'll probably be
 * handing a reference to this pumper to the ReadHead; ReadHead don't typically expose the
 * details of their base enough for this class to do its job that way.
 * 
 * @author hash
 * 
 */
public class PumperSelector implements Pumper {
	public PumperSelector() {
		Selector $bees = null;
		try {
			$bees = Selector.open();
		} catch (IOException $e) {
			X.cry($e);
		}
		$selector = $bees;
	}
	
	private final Selector $selector; 
	
	/** {@inheritDoc} */
	public void run() {
		while (true) {
			try {
				$selector.select();	// blocks for events
			} catch (IOException $e) {
				X.cry($e);
			}
			
			// Get list of selection keys with pending events
			Iterator<SelectionKey> $itr = $selector.selectedKeys().iterator();
			
			// Process each key
			while ($itr.hasNext()) {
				SelectionKey $k = $itr.next();
				$itr.remove();
				
				((Pump)$k.attachment()).run(Integer.MAX_VALUE);	// the pump is told to get as much as it can, but with the expectation that it will return much sooner when the channel runs out of immediately available data.
			}
		}
	}
	
	public void register(SelectableChannel $ch, Pump $p) {
		if ($p == null) throw new NullPointerException("pump cannot be null");
		try {
			$ch.register($selector, $ch.validOps(), $p);
		} catch (ClosedChannelException $e) {
			throw new IllegalStateException($e);
		}
	}
	
	public void deregister(Pump $p) {
		for (Iterator<SelectionKey> $itr = $selector.keys().iterator(); $itr.hasNext();) {
			SelectionKey $k = $itr.next();
			if ($p == $k.attachment()) $itr.remove();
		}
	}
	
	public void deregister(SelectableChannel $ch) {
		for (Iterator<SelectionKey> $itr = $selector.keys().iterator(); $itr.hasNext();) {
			SelectionKey $k = $itr.next();
			if ($ch == $k.channel()) $itr.remove();
		}
	}
		
}

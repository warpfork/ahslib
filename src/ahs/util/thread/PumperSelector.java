package ahs.util.thread;

import ahs.io.*;
import ahs.io.Pipe;
import ahs.util.*;

import java.io.*;
import java.nio.channels.*;
import java.util.*;

/**
 * <p>
 * This class provides a comprehensive wrapper (more of an asylum, really) around
 * java.nio.channels.Selector that simplifies common operation and provides efficient,
 * thread-safe controls that the java Selector fails to provide. Using this class, it
 * becomes possible to register and deregister selectable channels from any thread at any
 * time safely.
 * </p>
 * 
 * <p>
 * If using this with a ReadHead that is backed by NIO channels, you'll probably be
 * handing a reference to this pumper to the ReadHead; ReadHead don't typically expose the
 * details of their base enough for this class to do its job if merely given the public
 * interface of the ReadHead.
 * </p>
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
		$pipe = new Pipe<Event>();
		$pipe.SRC.setListener(new Listener<ReadHead<Event>>() {
			public void hear(ReadHead<Event> $eh) {
				$selector.wakeup();
			}
		});
	}
	
	private final Selector		$selector;
	private final Pipe<Event>	$pipe;
	
	/**
	 * <p>
	 * Gets a "default" PumperSelector that is a singleton to the VM.
	 * </p>
	 * 
	 * <p>
	 * This method uses lazy instantiation which is NOT thread-safe... but it's fine
	 * as long as it's used once from a single thread first.
	 * </p>
	 * 
	 * @return the default single PumperSelector for this VM (already started in its
	 *         own daemon thread).
	 */
	public static PumperSelector getDefault() {
		if ($default == null) {
			$default = new PumperSelector();
			$default.start();
		}
		return $default;
	}
	
	private static PumperSelector	$default;
	
	/**
	 * Starts the pump in a brand new (daemon) thread of its own.
	 */
	public synchronized void start() {
		Thread $t = new Thread(this, "SelectorPumper");
		$t.setDaemon(true);
		$t.start();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The method cycles through two phases. In the first phase blocks on the
	 * selector, then runs the Pump associated with any channel that reports an event.
	 * The Pump is run within the selection thread and told to get as much data as it
	 * can -- it is expected that the Pump will return immediately when it runs out of
	 * available data so that the selection thread continues to operate in an
	 * nonblocking fashion overall. In the second phase, the internal event buffer
	 * used to synchronize registration and deregistration requests from other threads
	 * is checked, and any buffered events are processed.
	 * </p>
	 */
	public void run() {
		while (true) {
			try {
				// block for events
				$selector.select();
			} catch (IOException $e) {
				X.cry($e);
			}
			
			{ // Get list of selection keys with pending events
				Iterator<SelectionKey> $itr = $selector.selectedKeys().iterator();
				
				// Process each key
				while ($itr.hasNext()) {
					SelectionKey $k = $itr.next();
					$itr.remove();
					
					if (!$k.isValid()) continue;
					
					((Pump) $k.attachment()).run(Integer.MAX_VALUE); // the pump is told to get as much as it can, but with the expectation that it will return much sooner (namely, when the channel runs out of immediately available data).
				}
			}
			
			List<Event> $evts = $pipe.SRC.readAllNow();
			for (Event $evt : $evts) {
				if ($evt instanceof Event_Reg) {
					try {
						if ($evt.channel() instanceof ServerSocketChannel) $evt.channel().register($selector, SelectionKey.OP_ACCEPT, $evt.pump());
						else $evt.channel().register($selector, SelectionKey.OP_READ, $evt.pump());
					} catch (ClosedChannelException $e) {
						X.cry($e); //XXX: i'm not sure if this is okay... what if the remote connection closes the connection between the register call and when the event comes out of the pipe here?
					}
				} else if ($evt instanceof Event_Dereg) {
					if ($evt.channel() == null) for (Iterator<SelectionKey> $itr = $selector.keys().iterator(); $itr.hasNext();) {
						SelectionKey $k = $itr.next();
						if ($evt.$pump == $k.attachment()) $k.cancel();
					}
					else for (Iterator<SelectionKey> $itr = $selector.keys().iterator(); $itr.hasNext();) {
						SelectionKey $k = $itr.next();
						if ($evt.$thing == $k.channel()) $k.cancel();
					}
				}
			}
		}
	}
	
	public void register(SelectableChannel $ch, Pump $p) {
		if ($p == null) throw new NullPointerException("pump cannot be null");
		$pipe.SINK.write(new Event_Reg($ch, $p));
	}
	
	public void register(ServerSocketChannel $ch, Pump $p) {
		if ($p == null) throw new NullPointerException("pump cannot be null");
		$pipe.SINK.write(new Event_Reg($ch, $p));
	}
	
	public void deregister(SelectableChannel $ch) {
		$pipe.SINK.write(new Event_Dereg($ch));
	}
	
	public void deregister(Pump $p) {
		$pipe.SINK.write(new Event_Dereg($p));
	}
	
	private static abstract class Event {
		protected Event(SelectableChannel $thing, Pump $pump) {
			this.$thing = $thing;
			this.$pump = $pump;
		}
		
		private SelectableChannel	$thing;
		private Pump			$pump;
		
		public SelectableChannel channel() {
			return $thing;
		};
		
		public Pump pump() {
			return $pump;
		};
	}
	
	private static class Event_Reg extends Event {
		private Event_Reg(SelectableChannel $ch, Pump $p) {
			super($ch, $p);
		}
	}
	
	private static class Event_Dereg extends Event {
		private Event_Dereg(Pump $p) {
			super(null, $p);
		}
		
		private Event_Dereg(SelectableChannel $ch) {
			super($ch, null);
		}
	}
}

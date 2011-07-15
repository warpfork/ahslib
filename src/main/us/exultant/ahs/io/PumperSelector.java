package us.exultant.ahs.io;

import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;
import us.exultant.ahs.thread.*;
import us.exultant.ahs.thread.Pipe;
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
// worth reading before dealing with nio: http://rox-xmlrpc.sourceforge.net/niotut/
public class PumperSelector {
	/**
	 * <p>
	 * Constructs a new PumperSelector with an open {@link Selector} backing it. The
	 * new PumperSelector is ready to accept registrations, but must still be started
	 * before it will take any action.
	 * </p>
	 */
	public PumperSelector() {
		$pump = new PumpT();
		Selector $bees = null;
		try {
			$bees = Selector.open();
		} catch (IOException $e) {
			X.cry($e);	// there's approximately zero documentation of what could cause this, but I assume if it happens then your VM is seriously fucked. 
		}
		$selector = $bees;
		$pipe = new Pipe<Event>();
		$pipe.SRC.setListener(new Listener<ReadHead<Event>>() {
			public void hear(ReadHead<Event> $eh) {
				$selector.wakeup();
			}
		});
	}
	
	private final Pump		$pump;
	private final Selector		$selector;
	private final Pipe<Event>	$pipe;
	
	/**
	 * <p>
	 * Gets a "default" PumperSelector that is a singleton to the VM.
	 * </p>
	 * 
	 * <p>
	 * This method is performs lazy instantiation, is thread-safe, and the returned
	 * PumperSelector is already started in its own daemon thread when returned.
	 * </p>
	 * 
	 * @return the default single PumperSelector for this VM.
	 */
	public static PumperSelector getDefault() {
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder {
		public static final PumperSelector INSTANCE = new PumperSelector();
	}
	
	/**
	 * <p>
	 * Starts the pump in a brand new (daemon) thread of its own.
	 * </p>
	 * 
	 * <p>
	 * Thread safety: go nuts. No, really. It is possible to safely register channels
	 * before and after starting the selector, and it's even fine to register channels
	 * from other threads while in the middle of starting the selector.
	 * </p>
	 */
	public synchronized void start() {
		Thread $t = new Thread(new Pump.RunnableWrapper(getPump()), "SelectorPumper");
		$t.setDaemon(true);
		$t.start();
	}
	
	public Pump getPump() {
		return $pump;
	}
	
	public class PumpT implements Pump {
		public boolean isDone() {
			return !$selector.isOpen();
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
		public void run(final int $times) {
			for (int $i = 0; $i < $times; $i++) {
				// if you get a notification of new registration event right here,
				//  whether on the first look or the nth, you're still fine:
				//  the selector remembers that it's been wakeup'd since it was last in this following part of the loop,
				//   so that call will still return immediately, and everything is kosher.
				
				// poke the selector for events
				try {
					$selector.select();	// blocks until channel events or wakeups triggered by the event pipe's listener.
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
						
						int $ops = $k.readyOps();
						///X.saye("ops"+$ops+"; keys"+$selector.keys().size());
						Attache $a = (Attache) $k.attachment();
						// the pumps are told to get as much as it can, but with the expectation that it will return much sooner (namely, when the channel runs out of immediately available data).
						if ((($ops & SelectionKey.OP_READ) != 0) && $a.$reader != null)
							$a.$reader.run(Integer.MAX_VALUE);
						if ((($ops & SelectionKey.OP_WRITE) != 0) && $a.$writer != null)
							$a.$writer.run(Integer.MAX_VALUE);
						if ((($ops & SelectionKey.OP_ACCEPT) != 0) && $a.$accepter != null)
							$a.$accepter.run(Integer.MAX_VALUE);
					}
				}
				
				// check for new registration or deregistration events.
				List<Event> $evts = $pipe.SRC.readAllNow();
				for (Event $evt : $evts) {
					if ($evt instanceof Event_Reg) {
						///X.saye($evt+" "+$evt.$ops);
						try {
							SelectionKey $k = $evt.$chan.keyFor($selector);
							Attache $a;
							int $old_ops;
							if ($k == null) {
								$a = new Attache();
								$old_ops = 0;
							} else {
								$a = (Attache)$k.attachment();
								$old_ops = $k.interestOps();
							}
							$a.apply($evt);
							///X.saye($evt+" "+$a.$reader+" "+$a.$writer);
							$evt.$chan.register($selector, Primitives.addMask($old_ops,$evt.$ops), $a);
						} catch (ClosedChannelException $e) {
							$e.printStackTrace();
							// don't know what kind of a response this could properly deserve here, though.  ignore it and wait for someone else to ask about that channel again and notice the foo?
						}
					} else if ($evt instanceof Event_Dereg) {
						SelectionKey $k = getKey($evt);
						Attache $a = (Attache)$k.attachment();
						int $old_ops = $k.interestOps();
						$k.interestOps(Primitives.removeMask($old_ops,$evt.$ops));
					} else if ($evt instanceof Event_Cancel) {
						SelectionKey $k = getKey($evt);
						if ($k != null) $k.cancel();
					}
				}
			}
		}
	}
	
	private SelectionKey getKey(Event $evt) {
		if ($evt.$chan != null) return $evt.$chan.keyFor($selector);
		for (SelectionKey $k : $selector.keys()) {
			Attache $a = (Attache)$k.attachment();
			if ($a == null) continue;
			if ($a.contains($pump)) return $k;
		}
		return null;
	}
	
	private class Attache {
		public Pump $reader;
		public Pump $writer;
		public Pump $accepter;
		
		public void apply(Event $evt) {
			switch ($evt.$ops) {
				case SelectionKey.OP_READ:
					$reader = $evt.$pump; break;
				case SelectionKey.OP_WRITE:
					$writer = $evt.$pump; break;
				case SelectionKey.OP_ACCEPT:
					$accepter = $evt.$pump; break;
				default:
					throw new MajorBug("op type not supported");
			}
		}
		
		public boolean contains(Pump $p) {
			return ($reader == $p) || ($writer == $p) || ($accepter == $p);
		}
	}
	
	/**
	 * Registers the given pump to be triggered by the selecting thread when this
	 * channel is flagged as having readable data.
	 * 
	 * @param $ch a readable channel
	 * @param $p a pump to run when the channel has data
	 */
	public void registerRead(SelectableChannel $ch, Pump $p) {
		$pipe.SINK.write(new Event_Reg($ch, $p, SelectionKey.OP_READ));
	}
	
	/**
	 * Registers the given pump to be triggered by the selecting thread when this
	 * channel is flagged as ready to accept data writes.
	 * 
	 * @param $ch a writable channel
	 * @param $p a pump to run when the channel can accept data
	 */
	public void registerWrite(SelectableChannel $ch, Pump $p) {
		$pipe.SINK.write(new Event_Reg($ch, $p, SelectionKey.OP_WRITE));
	}
	
	/**
	 * Registers the given pump to be triggered by the selecting thread when this
	 * ServerSocketChannel is flagged as having new connections ready to accept.
	 * 
	 * @param $ch a ServerSocketChannel channel
	 * @param $p a pump to run when connections are ready to be accepted
	 */
	public void register(ServerSocketChannel $ch, Pump $p) {
		$pipe.SINK.write(new Event_Reg($ch, $p, SelectionKey.OP_ACCEPT));
	}
	
	public void deregisterRead(SelectableChannel $ch) {
		$pipe.SINK.write(new Event_Dereg($ch, SelectionKey.OP_READ));
	}
	
	public void deregisterWrite(SelectableChannel $ch) {
		$pipe.SINK.write(new Event_Dereg($ch, SelectionKey.OP_WRITE));
	}
	
	/**
	 * Using this form of the command is slower and will behave ambiguously if the
	 * same pump object is attached to several channels. Consider using
	 * {@link #deregisterRead(SelectableChannel)} instead.
	 */
	public void deregisterRead(Pump $p) {
		$pipe.SINK.write(new Event_Dereg($p, SelectionKey.OP_READ));
	}
	
	/**
	 * Using this form of the command is slower and will behave ambiguously if the
	 * same pump object is attached to several channels. Consider using
	 * {@link #deregisterWrite(SelectableChannel)} instead.
	 */
	public void deregisterWrite(Pump $p) {
		$pipe.SINK.write(new Event_Dereg($p, SelectionKey.OP_WRITE));
	}
	
	public void deregister(ServerSocketChannel $ch) {
		$pipe.SINK.write(new Event_Dereg($ch, SelectionKey.OP_ACCEPT));
	}
	
	public void cancel(SelectableChannel $ch) {
		$pipe.SINK.write(new Event_Cancel($ch));
	}
	
	private static abstract class Event {
		protected Event(SelectableChannel $thing, Pump $pump, int $ops) {
			this.$chan = $thing;
			this.$pump = $pump;
			this.$ops = $ops;
		}
		
		public final SelectableChannel	$chan;
		public final Pump		$pump;
		public final int		$ops;
	}
	
	private static class Event_Reg extends Event {
		private Event_Reg(SelectableChannel $ch, Pump $p, int $ops) {
			super($ch, $p, $ops);
			if ($p == null) throw new NullPointerException("pump cannot be null");
		}
	}
	
	private static class Event_Dereg extends Event {
		private Event_Dereg(Pump $p, int $ops) {
			super(null, $p, $ops);
		}
		
		private Event_Dereg(SelectableChannel $ch, int $ops) {
			super($ch, null, $ops);
		}
	}
	
	private static class Event_Cancel extends Event {
		private Event_Cancel(SelectableChannel $ch) {
			super($ch, null, 0);
		}
		private Event_Cancel(Pump $p) {
			super(null, $p, 0);
		}
	}
}

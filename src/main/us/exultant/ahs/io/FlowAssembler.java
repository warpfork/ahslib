package us.exultant.ahs.io;

import us.exultant.ahs.core.*;
import us.exultant.ahs.io.TranslatorByteBufferToChannel.Completor;
import us.exultant.ahs.thread.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public class FlowAssembler {
	public static Flow<ByteBuffer> wrap(SocketChannel $chan, PumperSelector $ps) {
		return new Flow.Basic<ByteBuffer>(
				makeNonblockingChannelReader($chan, $ps),
				makeNonblockingChannelWriter($chan, $ps)
		);
	}
	
	public static  WriteHead<ByteBuffer> makeNonblockingChannelWriter(SocketChannel $chan, PumperSelector $ps) {
		Fuu $fuu = new Fuu(new TranslatorByteBufferToChannel.Nonblocking($chan), $ps);
		return $fuu;
	}
	private static class Fuu extends WriteHeadAdapter<ByteBuffer> implements Pump {
		/**
		 * @param $tran must have been constructed over a SelectableChannel or we'll throw ClassCastException later on. 
		 */
		public Fuu(TranslatorByteBufferToChannel $tran, PumperSelector $p) {
			this.$trans = $tran;
			this.$ps = $p;
			$pipe.SRC.setListener(new Listener<ReadHead<ByteBuffer>>() {
				// this listener is to register write interest as necessary when the pipe becomes nonempty.
				public void hear(ReadHead<ByteBuffer> $esto) {
					synchronized ($trans.$base) {
						// and incidentally, do we have any way to tell if it was empty right before this?
						//   i mean, technically it's not a problem to just (re)register every time, but it's inefficient.
						//   we would need synchronization here that locks reads as well as writes in order to be able to tell if this was really the first new object making the queue nonempty
						$ps.registerWrite((SelectableChannel)$trans.$base, Fuu.this);
					}
				}
			});
		}
		
		// IOException from the wire itself cause closure of the base channel, then get referred to the normal handler.
		public synchronized void run(final int $times) {
			for (int $i = 0; $i < $times; $i++) {
				if ($last != null) {
					try {
						$last.write();
					} catch (IOException $e) {
						try {
							close();
						} catch (IOException $e1) { /* we've already got more than we know what to do with */ }
						handleException($e);
						break;
					}
					if ($last.isComplete()) {
						$last = null;	// and if we've got $times left, loop and start a new one.
						synchronized ($trans.$base) {
							// we want to have the selector stop poking us if we haven't got any more data to work on, so:
							//  as long the pipe's listener's interest-reg enqueue happens-after the disinterest-reg here, it's fine.
							//  thus we have the listener sync on the transport base, and so do we.
							if ($pipe.size() == 0) {
								$ps.deregisterWrite((SelectableChannel)$trans.$base);
								break;
							}
						}
					} else {
						break;	// if this one didn't have room to finish pushing out, there sure ain't room to start another yet.
					}
				} else {
					if (isDone()) break;
					
					ByteBuffer $a = $pipe.SRC.readNow();
					if ($a == null) break;
					
					try {
						$last = $trans.translate($a);
						if ($last == null) break;
						// normally i'd say a translator might be allowed to make partial progress (and i'm checking for it just in case), but i don't think that really makes any sense here.
					} catch (TranslationException $e) {
						handleException($e);
						break;
					}
					try {
						$last.write();
					} catch (IOException $e) {
						try {
							close();
						} catch (IOException $e1) { /* we've already got more than we know what to do with */ }
						handleException($e);
						break;
					}
				}
			}
		}
		
		private final TranslatorByteBufferToChannel	$trans;
		private final PumperSelector			$ps;
		private volatile Completor			$last;
		
		public boolean isDone() {
			return $pipe.SRC.isClosed() && !$pipe.SRC.hasNext();
		}
		
		public void close() throws IOException {
			$trans.$base.close();
			$ps.cancel((SelectableChannel)$trans.$base);
		}
	}
	
	public static ReadHead<ByteBuffer> makeNonblockingChannelReader(SocketChannel $chan, PumperSelector $ps) {
		Quu $fuu = new Quu($chan, new TranslatorChannelToByteBuffer.Nonblocking(), $ps);
		$ps.registerRead($chan, $fuu);
		return $fuu;
	}
	private static class Quu extends ReadHeadAdapter<ByteBuffer> implements Pump {
		/**
		 * @param $base
		 *                must also be a SelectableChannel or we'll throw
		 *                ClassCastException later on (unfortunately, the type
		 *                hierarchy of Java's NIO package doesn't allow me to
		 *                specify that clearly without making a decorator object
		 *                purely to conceal their mistakes (which I'm not willing
		 *                to do)).
		 */
		public Quu(ReadableByteChannel $base, TranslatorChannelToByteBuffer $trans, PumperSelector $p) {
			this.$trans = $trans;
			this.$base = $base;
			this.$ps = $p;
		}
		
		// IOException cause closure of the base channel, then get referred to the normal handler.
		public synchronized void run(final int $times) {
			for (int $i = 0; $i < $times; $i++) {
				if (isDone()) break;
				
				ByteBuffer $chunk;
				try {
					$chunk = $trans.translate($base);
					if ($chunk == null) break;
				} catch (IOException $e) {
					try {
						close();
					} catch (IOException $e1) { /* we've already got more than we know what to do with */ }
					handleException($e);
					break;
				}
				$pipe.SINK.write($chunk);
			}
		}

		private final ReadableByteChannel		$base;
		private final PumperSelector			$ps;
		private final TranslatorChannelToByteBuffer	$trans;
		
		public boolean isDone() {
			return !$base.isOpen();
		}
		
		public void close() throws IOException {
			$base.close();
			$ps.cancel((SelectableChannel)$base);
		}
	}
}

package ahs.io;

import ahs.util.*;
import ahs.util.thread.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public abstract class ReadHeadAdapter<$T> implements ReadHead<$T> {
	/**
	 * The ReadHead returned by this factory method has no attached pumping scheme,
	 * and must be pumped manually by some thread.
	 * 
	 * @param $rbc
	 * @param $ts
	 *                Translator (or stack thereof) to use in processing chunks.
	 */
	public static <$T> ReadHead<$T> make(ReadableByteChannel $rbc, Translator<Channelwise.InfallibleReadableByteChannel, $T> $ts) {
		return new Channelwise<$T>($rbc, (Channelwise.ChunkBuilder<$T>)$ts);
	}
	
	/**
	 * The ReadHead returned by this factory method already involves a pumping scheme
	 * (via the PumperSelector), and thus need not be pumped manually by any other
	 * threads (though there is no harm in doing so other than wasted time).
	 * 
	 * @param $base
	 *                should already be connected and in a non-blocking state.
	 * @param $ps
	 *                selector with which to register the pump for reading operations;
	 *                will be kept and relevant key removed automatically when the
	 *                channel is closed.
	 * @param $ts
	 *                Translator (or stack thereof) to use in processing chunks.
	 */
	public static <$T> ReadHead<$T> make(DatagramChannel $base, PumperSelector $ps, Translator<Channelwise.InfallibleReadableByteChannel, $T> $ts) {
		return new ChannelwiseSelecting<$T>($base, $ps, (Channelwise.ChunkBuilder<$T>)$ts);
	}
	
	/**
	 * The ReadHead returned by this factory method already involves a pumping scheme
	 * (via PumperSelector), and thus need not be pumped manually by any other
	 * threads (though there is no harm in doing so other than wasted time).
	 * 
	 * @param $base
	 *                should already be connected and in a non-blocking state.
	 * @param $ps
	 *                selector with which to register the pump for reading operations;
	 *                will be kept and relevant key removed automatically when the
	 *                channel is closed.
	 * @param $ts
	 *                Translator (or stack thereof) to use in processing chunks.
	 */
	public static <$T> ReadHead<$T> make(SocketChannel $base, PumperSelector $ps, Translator<Channelwise.InfallibleReadableByteChannel, $T> $ts) {
		return new ChannelwiseSelecting<$T>($base, $ps, (Channelwise.ChunkBuilder<$T>)$ts);
	}
	
	/**
	 * The ReadHead returned by this factory method already involves a pumping scheme
	 * (via {@link PumperSelector#getDefault()}), and thus need not be pumped manually
	 * by any other threads (though there is no harm in doing so other than wasted
	 * time).
	 * 
	 * @param $base
	 *                should already be connected and in a non-blocking state.
	 * @param $ts
	 *                Translator (or stack thereof) to use in processing chunks.
	 */
	public static <$T> ReadHead<$T> make(DatagramChannel $base, Translator<Channelwise.InfallibleReadableByteChannel, $T> $ts) {
		return new ChannelwiseSelecting<$T>($base, PumperSelector.getDefault(), (Channelwise.ChunkBuilder<$T>)$ts);
	}
	
	/**
	 * The ReadHead returned by this factory method already involves a pumping scheme
	 * (via {@link PumperSelector#getDefault()}), and thus need not be pumped manually
	 * by any other threads (though there is no harm in doing so other than wasted
	 * time).
	 * 
	 * @param $base
	 *                should already be connected and in a non-blocking state.
	 * @param $ts
	 *                Translator (or stack thereof) to use in processing chunks.
	 */
	public static <$T> ReadHead<$T> make(SocketChannel $base, Translator<Channelwise.InfallibleReadableByteChannel, $T> $ts) {
		return new ChannelwiseSelecting<$T>($base, PumperSelector.getDefault(), (Channelwise.ChunkBuilder<$T>)$ts);
	}
	
	
	
	
	
	
	
	protected ReadHeadAdapter() {
		$pipe = new Pipe<$T>();
	}
	
	protected final Pipe<$T>		$pipe;
	protected ExceptionHandler<IOException>	$eh;
	
	public abstract Pump getPump();
	
	public void setExceptionHandler(ExceptionHandler<IOException> $eh) {
		this.$eh = $eh;
	}
	
	/**
	 * It's advised that if the ByteChannel at the base of this adapter is connected
	 * to a selector, the listener should cancel the relevant selection key when the
	 * channel becomes closed.
	 */
	public void setListener(Listener<ReadHead<$T>> $el) {
		$pipe.SRC.setListener($el);
	}
	
	public $T read() {
		return $pipe.SRC.read();
	}
	
	public $T readNow() {
		return $pipe.SRC.readNow();
	}
	
	public boolean hasNext() {
		return $pipe.SRC.hasNext();
	}
	
	public List<$T> readAll() {
		return $pipe.SRC.readAll();
	}
	
	public List<$T> readAllNow() {
		return $pipe.SRC.readAllNow();
	}
	
	public boolean isClosed() {
		return $pipe.SRC.isClosed();
	}
	
	public abstract void close() throws IOException;
	
	/**
	 * <p>
	 * This function is for updating our state at this buffering level to fully closed
	 * (i.e., after the event has propagated through the underlying stream and
	 * buffering, when we will never again add data to the readable buffer). It will
	 * cause this ReadHead to promptly report its state as closed, and thus halt all
	 * future pumping.
	 * </p>
	 * 
	 * <p>
	 * This transparently handles (in this order):
	 * <ol>
	 * <li>interruption of any still-blocking reads.
	 * <li>return of the final readAll.
	 * <li>sending of an event to our listener to give it a chance to notice our
	 * closure.
	 * </ol>
	 * </p>
	 */
	protected void baseEof() {
		$pipe.SRC.close();
	}
	
	
	
	
	
	
	
	public static class Channelwise<$T> extends ReadHeadAdapter<$T> {
		public Channelwise(ReadableByteChannel $rbc, ChunkBuilder<$T> $ts) {
			this.$pump = new PumpT();
			this.$trans = $ts;
			this.$irbc = new InfallibleReadableByteChannel($rbc, new ExceptionHandler<IOException>() {
				public void hear(IOException $e) {
					ExceptionHandler<IOException> $dated_eh = $eh;
					$irbc.close();	// this could kinda loop, but the method shouldn't KEEP throwing exceptions.
					if ($dated_eh != null) $dated_eh.hear($e);
				}
			});
		}
		
		private final ChunkBuilder<$T>			$trans;
		private final PumpT				$pump;
		private final InfallibleReadableByteChannel	$irbc;
		
		public Pump getPump() {
			return $pump;
		}
		
		public void close() throws IOException {
			$irbc.close();
			// we don't close the pipe itself -- we wait for a subsequent round of pumping to empty the buffer of the Channel; it then closes the pipe by calling the baseEof function.
			
			// in subclasses that Know about PumperSelector, it's advisable to override this to cancel the key after closing the channel.
		}
		
		
		
		
		
		// acts by going all the way to the BOTTOM of the stack,
		//  then handing things up to the translation stack,
		//  then taking what the translation stack returned and putting into the buffer pipe if it's not null.
		private class PumpT implements Pump {
			public boolean isDone() {
				return isClosed();
			}
			
			public synchronized void run(final int $times) {
				for (int $i = 0; $i < $times; $i++) {
					if (isDone()) break;
					
					try {
						$T $chunk = $trans.translate($irbc);
						
						// if we have no chunk it's just a non-blocking dude who doesn't have enough bytes for a semantic chunk
						if ($chunk == null) {
							if (!$irbc.isOpen())
								baseEof();
							break;	// we don't want to spin on it any more right now (and we might be done with it permanently).
						}
						
						// we have a chunk; wrap it up and enqueue to the buffer
						// any readers currently blocking will immediately Notice the new data due to the pipe's internal semaphore doing its job
						//  and the listener will automatically be notified as well
						$pipe.SINK.write($chunk);
					} catch (TranslationException $e) {
						ExceptionHandler<IOException> $dated_eh = $eh;
						if ($dated_eh != null) $dated_eh.hear($e);
						break;
					}
				}
			}
		}
		
		
		
		// both the first entry in a TranslatorStack as well as the entire stack itself should be able to implement this, really.
		public static interface ChunkBuilder<$CHUNK> extends Translator<InfallibleReadableByteChannel,$CHUNK> {
			// this interface really only exists so i had someplace to put this javadoc.
			/**
			 * Read as much as currently possible from the ByteChannel. If
			 * pleased with the data obtained in this read (along with other
			 * data that may be buffered from preceeding reads), return a
			 * chunk to be passed up to the next layer of either translation
			 * or buffering; if not yet enough data to make a full chunk,
			 * return null; if weird data, throw exceptions.
			 * 
			 * @param $bc
			 *                a ReadableByteChannel in non-blocking mode that
			 *                reports its low-level exceptions elsewhere.
			 * @return a chunk if possible; null otherwise.
			 * @throws TranslationException
			 *                 in case of data not conforming to protocol, or
			 *                 if the channel became closed at a point not
			 *                 expected by the protocol
			 */
			public $CHUNK translate(InfallibleReadableByteChannel $bc) throws TranslationException;
		}
		
		

		/**
		 * Hides all exceptions from the client, rerouting them elsewhere.
		 * IOException thrown from the read method cause the method to return 0;
		 * if the ExceptionHandler doesn't do something in response to the
		 * exception when it gets it (like simply closing the channel), it's quite
		 * likely that the read method will keep getting pumped with no productive
		 * result.
		 */
		private static class InfallibleReadableByteChannel implements ReadableByteChannel {
			private InfallibleReadableByteChannel(ReadableByteChannel $bc, ExceptionHandler<IOException> $eh) {
				$bc = $bc;
				$eh = $eh;
			}
			
			private ExceptionHandler<IOException>	$eh;
			private ReadableByteChannel		$bc;
			
			public void close() {
				try {
					$bc.close();
				} catch (IOException $ioe) {
					$eh.hear($ioe);
				}
			}
			
			public boolean isOpen() {
				return $bc.isOpen();
			}
			
			public int read(ByteBuffer $dst) {
				try {
					return $bc.read($dst);
				} catch (IOException $ioe) {
					$eh.hear($ioe);
					return -1;
				}
			}
		}
		
		
		
		public static class BabbleTranslator implements ChunkBuilder<ByteBuffer> {
			public BabbleTranslator() {}
			
			private final ByteBuffer	$preint	= ByteBuffer.allocate(4);
			private int			$messlen = -1;
			private ByteBuffer		$mess;
			
			public ByteBuffer translate(InfallibleReadableByteChannel $base) throws TranslationException {
				if ($messlen <= 0) {
					// figure out what length of message we expect
					if ($base.read($preint) == -1) {
						if ($preint.remaining() != 4) throw new TranslationException("malformed babble -- partial message length header read before unexpected EOF");
						return null;	// this is the one place in the Babble protocol for a smooth shutdown to be legal... the pump should just notice the channel being closed before next time around.
					}
					if ($preint.remaining() > 0) return null; // don't have a size header yet.  keep waiting for more data.
					$messlen = Primitives.intFromByteArray($preint.array());
					$preint.rewind();
					if ($messlen < 1) throw new TranslationException("malformed babble -- message length header not positive");
					$mess = ByteBuffer.allocate($messlen);
				}
				// if procedure gets here, we either had messlen state from the last round or we have it now.
				
				// get the message (or at least part of it, if possible)
				if ($base.read($mess) == -1) throw new TranslationException("babble of unexpected length");
				
				if ($mess.remaining() > 0) return null; // we just don't have as much information as this chunk should contain yet.  keep waiting for more data.
				
				$messlen = -1;
				$preint.rewind();
				$mess.rewind();
				return $mess;
			}
		}
	}

	public static class ChannelwiseSelecting<$T> extends Channelwise<$T> {
		// i would LOVE to have made more general constructors, but it's impossible to find a higher level of abstraction that is both selectable and readable
		// and i can't even sling together a new hack interface to unify them because everything in the selectable hierarchy is abstract classes (never interfaces)! 
		
		/**
		 * @param $base
		 *                should already be connected and in a non-blocking state.
		 * @param $ps
		 *                selector with which to register the pump for reading
		 *                operations; will be kept and relevant key removed
		 *                automatically when the channel is closed.
		 * @param $ts
		 *                Translator (or stack thereof) to use in processing
		 *                chunks.
		 */
		public ChannelwiseSelecting(DatagramChannel $base, PumperSelector $ps, ChunkBuilder<$T> $ts) {
			super($base, $ts);
			this.$ps = $ps;
			$ps.register($base, getPump());
		}
		
		/**
		 * @param $base
		 *                should already be connected and in a non-blocking state.
		 * @param $ps
		 *                selector with which to register the pump for reading
		 *                operations; will be kept and relevant key removed
		 *                automatically when the channel is closed.
		 * @param $ts
		 *                Translator (or stack thereof) to use in processing
		 *                chunks.
		 */
		public ChannelwiseSelecting(SocketChannel $base, PumperSelector $ps, ChunkBuilder<$T> $ts) {
			super($base, $ts);
			this.$ps = $ps;
			$ps.register($base, getPump());
		}
		
		private final PumperSelector $ps;
		
		public void close() throws IOException {
			$ps.deregister(getPump());
			super.close();
		}
	}
}

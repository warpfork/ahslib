package us.exultant.ahs.io;

import us.exultant.ahs.core.*;
import us.exultant.ahs.thread.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

/**
 * <p>
 * An output system wraps together basic parts of communication channel usage into a
 * convenient interface that's uniform whether you need to access the local filesystem or
 * communicate across the network. It accepts chunks of binary data, frames them according
 * to your specification, and pushes them onto the medium you provide; all of this is done
 * nonblockingly and concurrently by the system scheduler and selector system, leaving you
 * with high performance and nothing to worry about. Feeding data into an output system is
 * as easy as handing it an instance of the standard {@link ReadHead} interface.
 * </p>
 * 
 * <p>
 * Generally speaking you're not going to find methods here that expose to you, say, a
 * socket implementation raw that you can act on yourself. An OutputSystem unites local
 * filesystems and sockets and just about darn anything into one abstraction, so while
 * concepts such as close can be seen as a global pattern and a reasonable thing for an
 * OutputSystem to describe across all scenarios... socket timeout options on the other
 * hand would clearly not fit here.
 * </p>
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * 
 */
public class OutputSystem {
	public static OutputSystem makeReader(ReadHead<ByteBuffer> $source, ReadableByteChannel $sink, ChannelWriter $translator) {
		return makeReader(WorkManager.getDefaultScheduler(), $source, $sink, $translator);
	}
	public static OutputSystem makeReader(WorkScheduler $scheduler, ReadHead<ByteBuffer> $source, ReadableByteChannel $sink, ChannelWriter $translator) {
		// behavior for filesystem or other crap that doesn't match the SelectableChannel interface
		return null;
	}
	
	public static <$T extends SelectableChannel & WritableByteChannel> OutputSystem makeWriteSystem(ReadHead<ByteBuffer> $source, $T $sink, ChannelWriter $translator) {
		return makeWriteSystem(WorkManager.getDefaultScheduler(), IOManager.getDefaultSelectionSignaller(), $source, $sink, $translator);
	}
	public static <$T extends SelectableChannel & WritableByteChannel> OutputSystem makeWriteSystem(final WorkScheduler $scheduler, final SelectionSignaller $selector, final ReadHead<ByteBuffer> $source, final $T $sink, final ChannelWriter $translator) {
		final WorkTarget<Void> $wt = new WriterChannelWorker<$T>($selector, $source, $sink, $translator);
		final WorkFuture<Void> $wf = $scheduler.schedule($wt, ScheduleParams.NOW);
		$source.setListener(new Listener<ReadHead<ByteBuffer>>() {
			// this listener is to register write interest as necessary when the pipe becomes nonempty.
			//TODO:AHS:IO: this will work of course, but it's not good.  better behavior is: try to write, then do this if it fails to get through.
			public void hear(ReadHead<ByteBuffer> $esto) {
				$selector.registerWrite($sink, WorkManager.<SelectableChannel>updater($wf));
			}
		});
		return null;
	}
	
	
	
	private static class WriterChannelWorker<T extends SelectableChannel & WritableByteChannel> implements WorkTarget<Void> {	// we'll have to have different implemenations of this class, one for selectable one for not.  hide this with factory methods of course.  but doing typecast checks while running would be poor.
		public WriterChannelWorker(SelectionSignaller $selector, ReadHead<ByteBuffer> $source, T $sink, ChannelWriter $translator) {
			this.$source = $source;
			this.$channel = $sink;
			this.$trans = $translator;
			this.$selector = $selector;
			this.$last = null;
		}
		
		private final ReadHead<ByteBuffer>	$source;
		private final T				$channel;
		private final ChannelWriter		$trans;
		private final SelectionSignaller	$selector;
		/**
		 * If the last run wasn't able to push all the bytes in its message chunk
		 * onto the wire, that buffer is here. Otherwise is null.
		 */
		private ByteBuffer			$last;
		/**
		 * Count of bytes actually written to wire (assuming the ChannelWritter
		 * reports to us accurately). This becomes foobar'd and an underestimate
		 * if there's an IOException during a write, of course.
		 */
		private long				$bytesWritten;
		
		public Void call() throws IOException {
			if ($last == null) {
				$last = $source.readNow();
				if ($last == null) return null;
			}
			doWrite();
			
			if ($last.remaining() == 0) {
				$last = null;
				if (!$source.hasNext())
					$selector.deregisterWrite($channel);
			}
			
			return null;
		}
		
		private void doWrite() throws IOException {
			try {
				$bytesWritten += $trans.write($channel, $last);
			} catch (IOException $e) {
				close();
				throw $e;
			}
		}
		
		public void close() throws IOException {
			try {
				$channel.close();
			} catch (IOException $e) {
				$selector.cancel($channel);
				throw $e;
			}
		}
		
		/**
		 * This method is quite strange. We'd like to be able to query the
		 * selection ky to see if we can write at all, except of course for
		 * performance reasons we don't even register write interest until we know
		 * we have to wait for a kernal buffer to open. Returning true based
		 * instead on presense of a chunk buffer to finish or the source having
		 * another chunk will get us killed by the
		 * immediately-reschedule-if-ready-after-run rule of WorkSchedulers
		 * because we'd never hit waiting mode when we did schedule ourselves with
		 * the selector. So this one is... delicate.
		 */// maybe this isn't so bad.  no one has any business asking this and getting a sensible answer if you're in the middle of running, and if last is set when you're not running then clearly we are blocking for a signal.  oh, but the signal still has to be what wakes us up, so the listener we register with the selectionsignaller would actually have to call us back to make us admit we're ready, THEN tell the scheduler to ask us?  sheesh.  indirect.  but not wrong.  quite reasonable, really.
		public boolean isReady() {
			//TODO:AHS:IO: wheeee
			return true;
		}
		
		public int getPriority() {
			return 0;
		}
		
		public boolean isDone() {
			return $source.isExhausted();
		}
	}
}

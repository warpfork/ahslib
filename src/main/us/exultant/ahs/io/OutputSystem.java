package us.exultant.ahs.io;

import us.exultant.ahs.core.*;
import us.exultant.ahs.thread.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

// point of interest: you will want some systems that allow you to know when you've flushed.  and they might still not want to be synchronous.
//  writehead gets in your way quite directly here, having no return type.
//    you could have versions that give you a writehead that takes a Tup2<ByteBuffer, WorkFutureLatched>, and that would work, but be all kinds of inelegance.
//  or you could force people to use a writehead that's actually blocking if they want that kind of flushing.  but that's... baaaaaad.
//  on the gripping hand, it's of course possible to add a return type to WriteHead.  Perhaps it would even always be WorkFutureLatched type.  but it would still usually return null because we don't want to waste time on those object allocations if we don't have to, and more often than not noone cares.
//    well, in DataPipe it's obviously a bit trivialized.  a network system can obviously have the network fail before the program is dying, and knowing how far along in the queue of messages it is could be relevant.
//      then again, if the system breaks it becomes Done and exceptionally, and one could just drain the pipe of things not yet done with.  so i guess it really is mostly a question of what would make someone want to wait for a write to complete before moving on with their lives.
//  i guess the thing that makes me unsure how much minding this is worth is that if you're on the network, you never know if the far side got your message unless they respond anyway.  and if you're on a local filesystem, doing it synchronously typically isn't really a source of major butthurt.

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
		final WriterChannelWorker<$T> $wt = new WriterChannelWorker<$T>($selector, $source, $sink, $translator);
		final WorkFuture<Void> $wf = $scheduler.schedule($wt, ScheduleParams.NOW);
		$source.setListener(new Listener<ReadHead<ByteBuffer>>() {
			// this listener is to register write interest as necessary when the pipe becomes nonempty.
			//TODO:AHS:IO: this will work of course, but it's not good.  better behavior is: try to write, then do this if it fails to get through.
			public void hear(ReadHead<ByteBuffer> $esto) {
				$selector.registerWrite($sink, $wt.new Updater($wf));
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
		private volatile ByteBuffer		$last;
		/**
		 * Used to tell if we're ready to run or not. This is turned on by the
		 * listener we give for write interest when we have a $last chunk that
		 * didn't get finished writing. You can only turn this off after finishing
		 * writing a chunk and thus unregistering write interest (registering
		 * write interest again shall only take place if you get partial on
		 * another chunk in a future call).
		 */
		private volatile boolean		$signal;
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
			
			for (int $i = 0; $i < 3; $i++) {
				doWrite();
				if ($last.remaining() == 0) break;
			}
			//fuck: to register write here, we... essentially have to have a pointer to our own workfuture.  which is... a bit tough.
			
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
		
		public boolean isReady() {
			return ($last == null) ? $source.hasNext() : $signal;
		}
		
		private final class Updater implements Listener<SelectableChannel> {
			public Updater(WorkFuture<?> $wf) {
				this.$wf = $wf;
			}
			
			private final WorkFuture<?>	$wf;
			
			public final void hear(SelectableChannel $x) {
				$signal = true;
				$wf.update();
			}
		}
		
		public int getPriority() {
			return 0;
		}
		
		public boolean isDone() {
			return $source.isExhausted() && ($last == null);
		}
	}
}

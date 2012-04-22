package us.exultant.ahs.io;

import us.exultant.ahs.core.*;
import us.exultant.ahs.thread.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public class OutputSystem {
//	private final WorkTarget<Void> $wt;
//	private final WorkFuture<Void> $wf;
	
	public OutputSystem makeReader(ReadHead<ByteBuffer> $source, ReadableByteChannel $sink, ChannelWriter $translator) {
		return makeReader(
				WorkManager.getDefaultScheduler(),
				$source,
				$sink,
				$translator
		);
	}
	
	public OutputSystem makeReader(WorkScheduler $scheduler, ReadHead<ByteBuffer> $source, ReadableByteChannel $sink, ChannelWriter $translator) {
		// behavior for filesystem or other crap that doesn't match the SelectableChannel interface
		return null;
	}
	
	public <$T extends SelectableChannel & WritableByteChannel> OutputSystem makeWriteSystem(ReadHead<ByteBuffer> $source, $T $sink, ChannelWriter $translator) {
		return makeWriteSystem(
				WorkManager.getDefaultScheduler(),
				IOManager.getDefaultSelectionSignaller(),
				$source,
				$sink,
				$translator
		);
				
	}
	
	public <$T extends SelectableChannel & WritableByteChannel> OutputSystem makeWriteSystem(WorkScheduler $scheduler, SelectionSignaller $selector, ReadHead<ByteBuffer> $source, $T $sink, ChannelWriter $translator) {
		WorkTarget<Void> $wt = new WriterChannelWorker<$T>($selector, $source, $sink, $translator);
		WorkFuture<Void> $wf = $scheduler.schedule($wt, ScheduleParams.NOW);
		$source.setListener(new Listener<ReadHead<ByteBuffer>>() {
			// this listener is to register write interest as necessary when the pipe becomes nonempty.
			//TODO:AHS:IO: this will work of course, but it's not good.  better behavior is: try to write, then do this if it fails to get through.
			public void hear(ReadHead<ByteBuffer> $esto) {
				$selector.registerWrite($sink, $wf.updater());	// except this obviously won't go on that interface because that would piss me off every single time i implemented it
			}
		});
		return null;
	}
	
	private static class WriterChannelWorker<$T extends SelectableChannel & WritableByteChannel> implements WorkTarget<Void> {	// we'll have to have different implemenations of this class, one for selectable one for not.  hide this with factory methods of course.  but doing typecast checks while running would be poor.
		//realization: don't be a dick and put everyone on training wheels. that means expose more than a readhead.  sure, you can have a close on the writehead make the worker eventually close the channel.  but what if that's not what you mean?  why if you really want to just sent FIN *right now*?  expose the fucking socket.  don't be a dick.
		
		public WriterChannelWorker(SelectionSignaller $selector, ReadHead<ByteBuffer> $source, $T $sink, ChannelWriter $translator) {
			this.$source = $source;
			this.$channel = $sink;
			this.$trans = $translator;
			this.$selector = $selector;
			this.$last = null;
		}
		
		private final ReadHead<ByteBuffer>			$source;
		private final $T					$channel;
		private final ChannelWriter				$trans;
		private final SelectionSignaller			$selector;
		/** If the last run wasn't able to push all the bytes in its message chunk onto the wire, that buffer is here.  Otherwise is null. */
		private ByteBuffer					$last;
		/** Count of bytes actually written to wire (assuming the ChannelWritter reports to us accurately).
		 *  This becomes foobar'd and an underestimate if there's an IOException during a write, of course. */
		private long						$bytesWritten;
		
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
		 */
		public boolean isReady() {
			//TODO:AHS:IO: wheeee
			return true;
		}
		
		public boolean isDone() {
			return $source.isExhausted();
		}
		
		// i don't really wanna pass priority all the way down through the constructors from the top factory methods... i think i might make it a property of the OutputSystem and let it be set later instead.
	}	
}

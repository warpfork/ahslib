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
//    and yes, tcp has acking, sure.  but how much does that really tell you?  it tells you that the message got as far as kernel buffers on the other end.  it doesn't tell you that the application has even recieved it or become aware of it, much less done anything with that data or "committed" anything.

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
		final ChannelWritingWorker<$T> $wt = new ChannelWritingWorker<$T>($selector, $source, $sink, $translator);
		final WorkFuture<Void> $wf = $scheduler.schedule($wt, ScheduleParams.NOW);
		$wt.install($wf);
		return new OutputSystem($scheduler, $wf, $translator);
	}
	
	
	
	// this class might end up holding most of the things that are currently in the args of the factory methods... the factory methods can then let you choose all your own like they do now, or have more stuff that's like makeMeATcp(port), and in either case you have the same tuple holder when you're done.
	
	/**
	 * @param $scheduler
	 * @param $future
	 * @param $translator
	 */
	public OutputSystem(WorkScheduler $scheduler, WorkFuture<Void> $future, ChannelWriter $translator) {
		this.$scheduler = $scheduler;
		this.$future = $future;
		this.$translator = $translator;
	}
	
	private final WorkScheduler	$scheduler;
	private final WorkFuture<Void>	$future;
	private final ChannelWriter	$translator;
	
	// things we could but won't allow you to get out of this:
	//   - the SelectionSignaller (actually i'd be open to this, but i'm not doing it until i find a reason to do so).
	//   - the sink, because its type can be a bit... whatever it wants to be.  Perhaps we'll make this a generic type eventually, but that's actually a detail of implementation i'd rather conceal more often than not.
	//   - the ReadHead the worker will pull work from, because why would you want that?  If anything, you'd want the WriteHead that matches it, which we never had to begin with (which in turn was a choice made because the couplings between WorkTargets are the application designer's job to begin with).
	
	public WorkScheduler getScheduler() {	// hard to know why you'd want this either.
		return this.$scheduler;
	}
	public WorkFuture<Void> getFuture() {
		return this.$future;
	}
	public ChannelWriter getTranslator() {	// hard to know why you'd want this either.
		return this.$translator;
	}
	
	
	
	
	
	private static class ChannelWritingWorker<Chan extends SelectableChannel & WritableByteChannel> implements WorkTarget<Void> {	// we'll have to have different implemenations of this class, one for selectable one for not.  hide this with factory methods of course.  but doing typecast checks while running would be poor.
		public ChannelWritingWorker(SelectionSignaller $selector, ReadHead<ByteBuffer> $source, Chan $sink, ChannelWriter $translator) {
			this.$source = $source;
			this.$channel = $sink;
			this.$trans = $translator;
			this.$selector = $selector;
			this.$last = null;
		}
		
		private void install(WorkFuture<Void> $selfFuture) {
			$selectedListener = new Updater($selfFuture);
			// it's possible that there was a call() before this install(), which may (improbably) have gotten stuck.  check for that.  incredibly improbably, this could also end up redundant, but that's fine.
			ByteBuffer $dated_last = $last;
			if ($dated_last != null && $dated_last.remaining() == 0)
				$selector.registerWrite($channel, $selectedListener);
		}
		
		private final ReadHead<ByteBuffer>		$source;
		private final Chan				$channel;
		private final ChannelWriter			$trans;
		private final SelectionSignaller		$selector;
		private volatile Listener<SelectableChannel>	$selectedListener;	// we cannot make this final no matter what, but we COULD remove the need for volatile at least if we implemented the precall/install pattern at a grand level.
		/**
		 * If the last run wasn't able to push all the bytes in its message chunk
		 * onto the wire, that buffer is here. Otherwise is null.
		 */
		private volatile ByteBuffer			$last;
		/**
		 * Used to tell if we're ready to run or not. This is turned on by the
		 * listener we give for write interest when we have a $last chunk that
		 * didn't get finished writing. You can only turn this off after finishing
		 * writing a chunk and thus unregistering write interest (registering
		 * write interest again shall only take place if you get partial on
		 * another chunk in a future call).
		 */
		private volatile boolean			$signal;
		/**
		 * Count of bytes actually written to wire (assuming the ChannelWriter
		 * reports to us accurately). This becomes foobar'd and an underestimate
		 * if there's an IOException during a write, of course.
		 */
		private long					$bytesWritten;
		
		public Void call() throws IOException {
			if ($last == null) {
				$last = $source.readNow();
				if ($last == null) return null;
			}
			
			for (int $i = 0; $i < 3; $i++) {
				doWrite();
				if ($last.remaining() == 0) break;
			}
			
			if ($last.remaining() == 0) {
				// clean finish, no one blocked or nothing
				$last = null;
				$selector.deregisterWrite($channel);	/* this request is queued, and the selector is capable of being in the middle of signally process that's going to leave our $signal set again even right after this next line where we unset it.  this is still fine.  that possibility is impossible to prevent, but the absolute worst it can ever cause is a spurious call of this WT, which quickly exits again and clears the $signal. */
				$signal = false;
			} else {
				// we didn't get to write the whole chunk.  we need to set up a callback to schedule us again when the selection system says it's ready to accept more writing.
				if ($selectedListener != null) $selector.registerWrite($channel, $selectedListener);
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
			public Updater(WorkFuture<?> $wf) { this.$wf = $wf; }
			private final WorkFuture<?> $wf;
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

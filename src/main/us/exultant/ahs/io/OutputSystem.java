package us.exultant.ahs.io;

import us.exultant.ahs.core.*;
import us.exultant.ahs.thread.*;
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
 * communicate across the network. It accepts arbitrary messages, frames them according to
 * your specification, and pushes them onto the medium you provide; all of this is done
 * nonblockingly and concurrently by the system scheduler and selector system, leaving you
 * with high performance and nothing to worry about. Feeding data into an output system is
 * as easy as handing it an instance of the standard {@link ReadHead} interface that the
 * system should pull messages from.
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
public class OutputSystem<$MSG> {
	public static <$MSG> OutputSystem<$MSG> setup(ReadHead<$MSG> $source, ReadableByteChannel $sink, ChannelWriter<$MSG> $translator) {
		return setup(WorkManager.getDefaultScheduler(), $source, $sink, $translator);
	}
	public static <$MSG> OutputSystem<$MSG> setup(
			final WorkScheduler $scheduler,
			final ReadHead<$MSG> $source,
			final ReadableByteChannel $sink,
			final ChannelWriter<$MSG> $translator
		) {
		// behavior for filesystem or other crap that doesn't match the SelectableChannel interface
		return null;
	}
	
	public static <$MSG, $CHAN extends SelectableChannel & WritableByteChannel> OutputSystem<$MSG> setup(ReadHead<$MSG> $source, $CHAN $sink, ChannelWriter<$MSG> $translator) {
		return setup(WorkManager.getDefaultScheduler(), IOManager.getDefaultSelectionSignaller(), $source, $sink, $translator);
	}
	public static <$MSG, $CHAN extends SelectableChannel & WritableByteChannel> OutputSystem<$MSG> setup(
			final WorkScheduler $scheduler,
			final SelectionSignaller $selector,
			final ReadHead<$MSG> $source,
			final $CHAN $sink,
			final ChannelWriter<$MSG> $translator
		) {
		if ($sink.isBlocking()) throw new IllegalArgumentException("channel must be in nonblocking mode");
		final OutputSystem_WorkerChannelSelectable<$MSG, $CHAN> $wt = new OutputSystem_WorkerChannelSelectable<$MSG, $CHAN>($selector, $source, $sink, $translator);
		final WorkFuture<Void> $wf = $scheduler.schedule($wt, ScheduleParams.NOW);
		$wt.install($wf);
		return new OutputSystem<$MSG>($scheduler, $wf, $translator);
	}
	
	
	
	// this class might end up holding most of the things that are currently in the args of the factory methods... the factory methods can then let you choose all your own like they do now, or have more stuff that's like makeMeATcp(port), and in either case you have the same tuple holder when you're done.
	
	/**
	 * @param $scheduler
	 * @param $future
	 * @param $translator
	 */
	private OutputSystem(WorkScheduler $scheduler, WorkFuture<Void> $future, ChannelWriter<$MSG> $translator) {
		this.$scheduler = $scheduler;
		this.$future = $future;
		this.$translator = $translator;
	}
	
	private final WorkScheduler		$scheduler;
	private final WorkFuture<Void>		$future;
	private final ChannelWriter<$MSG>	$translator;
	
	// things we could but won't allow you to get out of this:
	//   - the SelectionSignaller (actually i'd be open to this, but i'm not doing it until i find a reason to do so).  oh also that's not necessarily a thing at all for a lot of implementations.
	//   - the sink, because its type can be a bit... whatever it wants to be.  Perhaps we'll make this a generic type eventually, but that's actually a detail of implementation i'd rather conceal more often than not.
	//   - the ReadHead the worker will pull work from, because why would you want that?  If anything, you'd want the WriteHead that matches it, which we never had to begin with (which in turn was a choice made because the couplings between WorkTargets are the application designer's job to begin with).
	
	public WorkScheduler getScheduler() {	// hard to know why you'd want this either.
		return this.$scheduler;
	}
	public WorkFuture<Void> getFuture() {
		return this.$future;
	}
	public ChannelWriter<$MSG> getTranslator() {	// hard to know why you'd want this either.
		return this.$translator;
	}
}

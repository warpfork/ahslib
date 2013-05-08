package us.exultant.ahs.io;

import us.exultant.ahs.core.*;
import us.exultant.ahs.thread.*;
import java.io.*;
import java.nio.channels.*;

// point of interest: you will want some systems that allow you to know when you've flushed.  and they might still not want to be synchronous.
//  absolved by using Flow<Ackable<Whatever>>.  though we should actually... do... that.

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
		if ($sink.isBlocking()) {
			try {
				$sink.configureBlocking(false);
			} catch (IOException $e) { throw new IllegalArgumentException("channel must be in nonblocking mode; and failed to change mode.", $e); }
		}
		final OutputSystem_WorkerChannelSelectable<$MSG, $CHAN> $wt = new OutputSystem_WorkerChannelSelectable<$MSG, $CHAN>($selector, $source, $sink, $translator);
		final WorkFuture<Void> $wf = $scheduler.schedule($wt, ScheduleParams.NOW);
		$wt.install($wf);
		return new OutputSystem<$MSG>($scheduler, $wf, $sink, $translator);
	}



	private OutputSystem(WorkScheduler $scheduler, WorkFuture<Void> $future, Channel $channel, ChannelWriter<$MSG> $translator) {
		this.$scheduler = $scheduler;
		this.$channel = $channel;
		this.$future = $future;
		this.$translator = $translator;
		final WorkTargetChannelCloser $wt_closer = new WorkTargetChannelCloser(this);
		final WorkFuture<Void> $wf_closer = $scheduler.schedule($wt_closer, ScheduleParams.NOW);
		$future.addCompletionListener(new Listener<WorkFuture<?>>() {
			public void hear(WorkFuture<?> $x) {
				$wf_closer.update();
			}
		});
	}

	private final WorkFuture<Void>		$future;
	private final ChannelWriter<$MSG>	$translator;
	private final Channel			$channel;
	private final WorkScheduler		$scheduler;

	// things we could but won't allow you to get out of this:
	//   - the SelectionSignaller (actually i'd be open to this, but i'm not doing it until i find a reason to do so).  oh also that's not necessarily a thing at all for a lot of implementations.
	//   - the ReadHead the worker will pull work from, because why would you want that?  If anything, you'd want the WriteHead that matches it, which we never had to begin with (which in turn was a choice made because the couplings between WorkTargets are the application designer's job to begin with).

	public WorkFuture<Void> getFuture() {
		return this.$future;
	}

	public ChannelWriter<$MSG> getTranslator() {	// hard to know why you'd want this either.
		return this.$translator;
	}

	public Channel getChannel() {
		return $channel;
	}

	public WorkScheduler getScheduler() {	// hard to know why you'd want this either.
		return this.$scheduler;
	}
}

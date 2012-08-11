package us.exultant.ahs.io;

import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;
import us.exultant.ahs.thread.*;
import java.io.*;
import java.nio.channels.*;
import org.slf4j.*;

class OutputSystem_WorkerChannelSelectable<$MSG, Chan extends SelectableChannel & WritableByteChannel> implements WorkTarget<Void> {
	public OutputSystem_WorkerChannelSelectable(SelectionSignaller $selector, ReadHead<$MSG> $source, Chan $sink, ChannelWriter<$MSG> $translator) {
		this.$source = $source;
		this.$channel = $sink;
		this.$trans = $translator;
		this.$selector = $selector;
	}
	
	void install(final WorkFuture<Void> $selfFuture) {
		$selectedListener = new Updater($selfFuture);
		$source.setListener(new Listener<ReadHead<$MSG>>() {
			public void hear(ReadHead<$MSG> $x) {
				$selfFuture.update();
			}
		});
		// it's possible that there was a call() before this install(), which may (improbably) have gotten stuck.  check for that.  incredibly improbably, this could also end up redundant, but that's fine.
		if ($buffered) $selector.registerWrite($channel, $selectedListener);
	}

	public static final Loggar logger = new Loggar(LoggerFactory.getLogger(OutputSystem_WorkerChannelSelectable.class));
	
	private final ReadHead<$MSG>			$source;
	private final Chan				$channel;
	private final ChannelWriter<$MSG>		$trans;
	private final SelectionSignaller		$selector;
	private volatile Listener<SelectableChannel>	$selectedListener;	// we cannot make this final no matter what, but we COULD remove the need for volatile at least if we implemented the precall/install pattern at a grand level.
	/**
	 * True if the last run wasn't able to push all the bytes in its message chunk
	 * onto the wire, false otherwise.
	 */
	private volatile boolean			$buffered;
	/**
	 * Used to tell if we're ready to run or not. This is turned on by the
	 * listener we give for write interest when we have a $last chunk that
	 * didn't get finished writing. You can only turn this off after finishing
	 * writing a chunk and thus unregistering write interest (registering
	 * write interest again shall only take place if you get partial on
	 * another chunk in a future call).
	 */
	private volatile boolean			$signal;
	
	public Void call() throws IOException {
		assert logger.debug("write worker called; operating on channel {}", $channel);
		try {
			if (!$buffered) {
				$MSG $msg = $source.readNow();
				if ($msg == null) {
					assert logger.debug("there's no data to write; this was a spurious call, returning");
					return null;	// it might behoove us to make sure $signal is unset here.  it probably is, but a very slow queue for write interest deregistration could cause us to loop unpleasantly on an empty pipe.	// this may or may not have been intentionally deleted already?  fucked up something with stash, leaves me unsure.
				}
				assert logger.debug("starting write of new chunk");
				$buffered = !$trans.write($channel, $msg);
			}
			for (int $i = 0; $buffered && $i < 3; $i++) {
				assert logger.debug("continuing write of buffered chunk");
				$buffered = !$trans.write($channel, null);
			}
		} catch (TranslationException $e) {
			throw $e;
		} catch (IOException $e) {
			close();
			throw $e;
		}
		
		if (!$buffered) {
			// clean finish, no one blocked or nothing
			if ($signal) {  // we needn't bother the selectorsignaller with a deregister request if we were never registered.
				assert logger.debug("write worker ran with no remaining buffered data; signal was set, so deregistering write interest on channel:{}", $channel);
				$selector.deregisterWrite($channel);	/* this request is queued, and the selector is capable of being in the middle of signally process that's going to leave our $signal set again even right after this next line where we unset it.  this is still fine.  that possibility is impossible to prevent, but the absolute worst it can ever cause is a spurious call of this WT, which quickly exits again and clears the $signal. */
				$signal = false;
			} else {
				assert logger.debug("write worker ran with no remaining buffered data; signal was already unset, so no further action.");
			}
		} else {
			// we didn't get to write the whole chunk.  we need to set up a callback to schedule us again when the selection system says it's ready to accept more writing.
			assert logger.debug("write worker has remaining buffered data, so registering write interest on channel:{}", $channel);
			if ($selectedListener != null) $selector.registerWrite($channel, $selectedListener);
		}
		
		return null;
	}
	
	public void close() throws IOException {
		try {
			assert logger.debug("closing channel {}", $channel);
			$channel.close();
		} catch (IOException $e) {
			$selector.cancel($channel);
			throw $e;
		}
	}
	
	public boolean isReady() {
		assert logger.trace("write worker asked if ready");
		return ($buffered) ? $signal : $source.hasNext();
	}
	
	private final class Updater implements Listener<SelectableChannel> {
		public Updater(WorkFuture<?> $wf) { this.$wf = $wf; }
		private final WorkFuture<?> $wf;
		public final void hear(SelectableChannel $x) {
			assert logger.trace("write worker updater called");
			$signal = true;
			$wf.update();
		}
	}
	
	public int getPriority() {
		return 0;
	}
	
	public boolean isDone() {
		return $source.isExhausted() && !$buffered;
	}
}

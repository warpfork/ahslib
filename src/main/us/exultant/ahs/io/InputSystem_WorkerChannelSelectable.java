package us.exultant.ahs.io;

import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;
import us.exultant.ahs.thread.*;
import java.io.*;
import java.nio.channels.*;
import org.slf4j.*;

class InputSystem_WorkerChannelSelectable<$MSG, Chan extends SelectableChannel & ReadableByteChannel> implements WorkTarget<$MSG> {
	public InputSystem_WorkerChannelSelectable(SelectionSignaller $selector, WriteHead<$MSG> $sink, Chan $source, ChannelReader<$MSG> $framer) {
		this.$sink = $sink;
		this.$channel = $source;
		this.$framer = $framer;
		this.$selector = $selector;
	}
	
	void install(WorkFuture<$MSG> $selfFuture) {
		$selector.registerRead($channel, new Updater($selfFuture));
	}
	
	public static final Loggar logger = new Loggar(LoggerFactory.getLogger(InputSystem_WorkerChannelSelectable.class));
	
	private final WriteHead<$MSG>			$sink;
	private final Chan				$channel;
	private final ChannelReader<$MSG>		$framer;
	private final SelectionSignaller		$selector;
	
	/**
	 * Used to tell if we're ready to run or not. This is turned on by the listener we
	 * give for read interest, and can come on at any time since we leave that
	 * listener registered pretty much until we die. It's safe to turn off any time
	 * you do any attempt to read; if you don't drain all the bytes, it should just
	 * come back on again shortly since we never remove the listener.
	 */
	private volatile boolean			$signal;
	
	public $MSG call() throws IOException, TranslationException {
		try {
			assert logger.debug("read worker called; operating on channel {}", $channel);
			$signal = false;
			$MSG $msg = $framer.read($channel);
			if ($msg != null) {
				assert logger.debug("read message chunk from framer; handing it off.");
				$sink.write($msg);
			} else {
				assert logger.debug("no full message chunk available from framer.");
			}
			return $msg;
		} catch (TranslationException $e) {
			throw $e;
		} catch (IOException $e) {
			close();
			throw $e;
		}
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
		assert logger.trace("read worker asked if ready");
		return $signal && $sink.hasRoom();
	}
	
	private final class Updater implements Listener<SelectableChannel> {
		public Updater(WorkFuture<?> $wf) { this.$wf = $wf; }
		private final WorkFuture<?> $wf;
		public final void hear(SelectableChannel $x) {
			assert logger.trace("read worker updater called");
			$signal = true;
			$wf.update();
		}
	}
	
	public int getPriority() {
		return 0;
	}
	
	public boolean isDone() {
		return !$channel.isOpen();
	}
}

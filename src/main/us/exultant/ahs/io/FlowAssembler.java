package us.exultant.ahs.io;

import us.exultant.ahs.core.*;
import us.exultant.ahs.io.TranslatorByteBufferToChannel.Completor;
import us.exultant.ahs.thread.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public class FlowAssembler {
	public Flow<ByteBuffer> wrap(SocketChannel $chan, PumperSelector $ps) {
		return new Flow.Basic<ByteBuffer>(
				makeNonblockingChannelReader($chan, $ps),
				makeNonblockingChannelWriter($chan, $ps)
		);
	}
	
	public WriteHead<ByteBuffer> makeNonblockingChannelWriter(SocketChannel $chan, PumperSelector $ps) {
		Fuu $fuu = new Fuu(new TranslatorByteBufferToChannel.Nonblocking($chan), $ps);
		$ps.register($chan, $fuu);
		return $fuu;
	}
	private class Fuu extends WriteHeadAdapter<ByteBuffer> implements Pump {
		public Fuu(TranslatorByteBufferToChannel $trans, PumperSelector $p) {
			this.$trans = $trans;
			this.$ps = $p;
			$pipe.SRC.setListener(new Listener<ReadHead<ByteBuffer>>() {
				// this listener is to register write interest as necessary when the pipe becomes nonempty.
				public void hear(ReadHead<ByteBuffer> $esto) {
					//TODO:AHS:FINISH
					//$ps.
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
						
						// we want to have the selector stop poking us if we haven't got any more data to work on, so:
						// as long the pipe's listener's interest-reg enqueue happens-after the disinterest-reg here, it's fine.
						// how to do?!  either:
						//   - lock the writability of ps's event queue BEFORE doing the size check
						//   - lock the writability of our pipe BEFORE doing the size check
						// the former is probably capable of causing a wider set of unrelated services to block, so we're against that option.
						if ($pipe.size() == 0) {
							// or what if the listener syncs on something we do here too?
							// that might actually be a more elegant solution, since it's effectively option two but without the hack into the pipe.
							//TODO:AHS:FINISH
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
		}
	}
	
	public ReadHead<ByteBuffer> makeNonblockingChannelReader(SocketChannel $chan, PumperSelector $ps) {
		Quu $fuu = new Quu($chan, new TranslatorChannelToByteBuffer.Nonblocking());
		$ps.register($chan, $fuu);
		return $fuu;
	}
	private class Quu extends ReadHeadAdapter<ByteBuffer> implements Pump {
		public Quu(ReadableByteChannel $base, TranslatorChannelToByteBuffer $trans) {
			this.$trans = $trans;
			this.$base = $base;
		}
		
		// IOException cause closure of the base channel, then get referred to the normal handler.
		public synchronized void run(final int $times) {
			for (int $i = 0; $i < $times; $i++) {
				if (isDone()) break;
				
				ByteBuffer $chunk;
				try {
					$chunk = $trans.translate($base);
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
		private final TranslatorChannelToByteBuffer	$trans;
		
		public boolean isDone() {
			return !$base.isOpen();
		}
		
		public void close() throws IOException {
			$base.close();
		}
	}
}

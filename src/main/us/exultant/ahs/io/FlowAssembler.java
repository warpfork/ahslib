/*
 * Copyright 2010, 2011 Eric Myhre <http://exultant.us>
 * 
 * This file is part of AHSlib.
 *
 * AHSlib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License, or
 * (at the original copyright holder's option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package us.exultant.ahs.io;

import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;
import us.exultant.ahs.io.TranslatorByteBufferToChannel.Completor;
import us.exultant.ahs.thread.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public class FlowAssembler {
	/**
	 * Produces a {@link Flow} that pairs a {@link ReadHead} and a {@link WriteHead}
	 * that will communicate over the given channel using Babble frames in a
	 * nonblocking fashion powered by the given selector.
	 * 
	 * @param $chan
	 *                The channel to wrap with ReadHead and WriteHead. If this channel
	 *                is not already set to nonblocking mode, it will be set.
	 * @param $ps
	 *                The selector to register the channel with.
	 * @return a new {@link Flow} ready for use, or null if the channel was already
	 *         closed.
	 */
	public static Flow<ByteBuffer> wrap(SocketChannel $chan, PumperSelector $ps) {
		try {
			$chan.configureBlocking(false);
		} catch (ClosedChannelException $e) {
			return null;
		} catch (IOException $e) {
			X.cry($e);	/* If someone ever explains to me why this would happen, then I'll write logic to handle it properly. */
		}
		return new Flow.Basic<ByteBuffer>(
				makeNonblockingChannelReader($chan, $ps),
				makeNonblockingChannelWriter($chan, $ps)
		);
	}
	
	


	/**
	 * Associates a socket channel with a selector to perform nonblocking write using
	 * the standard "Babble" protocol (i.e., simple frames of binary data preceeded by
	 * a 32-bit signed int).
	 * 
	 * @param $chan
	 * @param $ps
	 * @return a WriteHead which pushes a frame of binary babble to the wire for every
	 *         ByteBuffer.
	 */
	public static WriteHead<ByteBuffer> makeNonblockingChannelWriter(SocketChannel $chan, PumperSelector $ps) {
		Fuu $fuu = new Fuu(new TranslatorByteBufferToChannel.Nonblocking($chan), $ps);
		return $fuu;
	}
	private static class Fuu extends WriteHeadAdapter<ByteBuffer> implements Pump {
		/**
		 * @param $tran must have been constructed over a SelectableChannel or we'll throw ClassCastException later on. 
		 */
		public Fuu(TranslatorByteBufferToChannel $tran, PumperSelector $p) {
			this.$trans = $tran;
			this.$ps = $p;
			$pipe.SRC.setListener(new Listener<ReadHead<ByteBuffer>>() {
				// this listener is to register write interest as necessary when the pipe becomes nonempty.
				public void hear(ReadHead<ByteBuffer> $esto) {
					synchronized ($trans.$base) {
						// and incidentally, do we have any way to tell if it was empty right before this?
						//   i mean, technically it's not a problem to just (re)register every time, but it's inefficient.
						//   we would need synchronization here that locks reads as well as writes in order to be able to tell if this was really the first new object making the queue nonempty
						$ps.registerWrite((SelectableChannel)$trans.$base, Fuu.this);
					}
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
						synchronized ($trans.$base) {
							// we want to have the selector stop poking us if we haven't got any more data to work on, so:
							//  as long the pipe's listener's interest-reg enqueue happens-after the disinterest-reg here, it's fine.
							//  thus we have the listener sync on the transport base, and so do we.
							if ($pipe.size() == 0) {
								$ps.deregisterWrite((SelectableChannel)$trans.$base);
								break;
							}
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
						if ($last == null) break;
						// normally i'd say a translator might be allowed to make partial progress (and i'm checking for it just in case), but i don't think that really makes any sense here.
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
			$ps.cancel((SelectableChannel)$trans.$base);
		}
	}
	
	/**
	 * Associates a socket channel with a selector to perform nonblocking reads using
	 * the standard "Babble" protocol (i.e., simple frames of binary data preceeded by
	 * a 32-bit signed int).
	 * 
	 * @param $chan
	 * @param $ps
	 * @return a ReadHead which yields a ByteBuffer for every frame of binary babble.
	 */
	public static ReadHead<ByteBuffer> makeNonblockingChannelReader(SocketChannel $chan, PumperSelector $ps) {
		Quu $fuu = new Quu($chan, new TranslatorChannelToByteBuffer.Nonblocking(), $ps);
		$ps.registerRead($chan, $fuu);
		return $fuu;
	}
	private static class Quu extends ReadHeadAdapter<ByteBuffer> implements Pump {
		/**
		 * @param $base
		 *                must also be a SelectableChannel or we'll throw
		 *                ClassCastException later on (unfortunately, the type
		 *                hierarchy of Java's NIO package doesn't allow me to
		 *                specify that clearly without making a decorator object
		 *                purely to conceal their mistakes (which I'm not willing
		 *                to do)).
		 */
		public Quu(ReadableByteChannel $base, TranslatorChannelToByteBuffer $trans, PumperSelector $p) {
			this.$trans = $trans;
			this.$base = $base;
			this.$ps = $p;
		}
		
		// IOException cause closure of the base channel, then get referred to the normal handler.
		public synchronized void run(final int $times) {
			for (int $i = 0; $i < $times; $i++) {
				if (isDone()) break;
				
				ByteBuffer $chunk;
				try {
					$chunk = $trans.translate($base);
					if ($chunk == null) break;
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
		private final PumperSelector			$ps;
		private final TranslatorChannelToByteBuffer	$trans;
		
		public boolean isDone() {
			return !$base.isOpen();
		}
		
		public void close() throws IOException {
			$base.close();
			$ps.cancel((SelectableChannel)$base);
		}
	}
}

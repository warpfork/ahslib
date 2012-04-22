///*
// * Copyright 2010, 2011 Eric Myhre <http://exultant.us>
// * 
// * This file is part of AHSlib.
// *
// * AHSlib is free software: you can redistribute it and/or modify
// * it under the terms of the GNU Lesser General Public License as published by
// * the Free Software Foundation, version 3 of the License, or
// * (at the original copyright holder's option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// * GNU Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public License
// * along with this program. If not, see <http://www.gnu.org/licenses/>.
// */
//
//package us.exultant.ahs.io;
//
//import us.exultant.ahs.core.*;
//import us.exultant.ahs.util.*;
//import us.exultant.ahs.io.TranslatorByteBufferToChannelByFrame.Completor;
//import us.exultant.ahs.thread.*;
//import java.io.*;
//import java.nio.*;
//import java.nio.channels.*;
//
//public class FlowAssembler {
//	/**
//	 * Produces a {@link Flow} that pairs a {@link ReadHead} and a {@link WriteHead}
//	 * that will communicate over the given channel using Babble frames in a
//	 * nonblocking fashion powered by the given selector.
//	 * 
//	 * @param $chan
//	 *                The channel to wrap with ReadHead and WriteHead. If this channel
//	 *                is not already set to nonblocking mode, it will be set.
//	 * @param $ps
//	 *                The selector to register the channel with.
//	 * @return a new {@link Flow} ready for use, or null if the channel was already
//	 *         closed.
//	 */
//	public static Flow<ByteBuffer> wrap(SocketChannel $chan, WorkTargetSelector $ps) {
//		try {
//			$chan.configureBlocking(false);
//		} catch (ClosedChannelException $e) {
//			return null;
//		} catch (IOException $e) {
//			X.cry($e);	/* If someone ever explains to me why this would happen, then I'll write logic to handle it properly. */
//		}
//		return new Flow.Basic<ByteBuffer>(
//				makeNonblockingChannelReader($chan, $ps),
//				makeNonblockingChannelWriter($chan, $ps)
//		);
//	}
//	
//	
//	
//	/**
//	 * Associates a socket channel with a selector to perform nonblocking reads using
//	 * the standard "Babble" protocol (i.e., simple frames of binary data preceeded by
//	 * a 32-bit signed int).
//	 * 
//	 * @param $chan
//	 * @param $ps
//	 * @return a ReadHead which yields a ByteBuffer for every frame of binary babble.
//	 */
//	public static ReadHead<ByteBuffer> makeNonblockingChannelReader(SocketChannel $chan, WorkTargetSelector $ps) {
//		Quu $fuu = new Quu($chan, new TranslatorChannelToByteBufferByFrame.Nonblocking(), $ps);
//		$ps.registerRead($chan, $fuu);
//		return $fuu;
//	}
//	private static class Quu extends ReadHeadAdapter<ByteBuffer> implements Pump {
//		public <$T extends SelectableChannel & ReadableByteChannel> Quu($T $base, TranslatorChannelToByteBufferByFrame $trans, WorkTargetSelector $p) {
//			this.$trans = $trans;
//			this.$base = $base;
//			this.$ps = $p;
//		}
//		
//		// IOException cause closure of the base channel, then get referred to the normal handler.
//		public synchronized void run(final int $times) {
//			for (int $i = 0; $i < $times; $i++) {
//				if (isDone()) break;
//				
//				ByteBuffer $chunk;
//				try {
//					$chunk = $trans.translate($base);
//					if ($chunk == null) break;
//				} catch (IOException $e) {
//					close();
//					handleException($e);
//					break;
//				}
//				$pipe.SINK.write($chunk);
//			}
//		}
//
//		private final ReadableByteChannel		$base;
//		private final WorkTargetSelector		$ps;
//		private final Translator<ReadableByteChannel,ByteBuffer>	$trans;
//		
//		public boolean isDone() {
//			return !$base.isOpen();
//		}
//		
//		public void close() {
//			try {
//				$base.close();
//			} catch (IOException $e) {
//				handleException($e);
//			}
//			$ps.cancel((SelectableChannel)$base);
//		}
//	}
//}

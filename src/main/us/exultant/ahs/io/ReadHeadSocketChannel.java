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
import us.exultant.ahs.thread.*;
import java.io.*;
import java.net.*;
import java.nio.channels.*;

/**
 * <p>
 * Creates a server socket for accepting new socket connections, makes it nonblocking, and
 * presents the whole thing as a {@link ReadHead}.
 * </p>
 * 
 * <p>
 * The new {@link SocketChannel} returned when reading from this ReadHead are already
 * configured to be nonblocking themselves (but of course have not yet been registered
 * with any {@link WorkTargetSelector}, since they aren't yet wrapped in any of the other
 * abstractions typical of the AHS library that would make a relationship with a
 * WorkTargetSelector appropriate).
 * </p>
 * 
 * @author hash
 * 
 */
public class ReadHeadSocketChannel extends ReadHeadAdapter<SocketChannel> {
	/**
	 * Opens and binds a new non-blocking server socket, registers it with a selector,
	 * and leaves you with a lovely ReadHead interface ready to rock.
	 * 
	 * @param $localBinding
	 *                as per {@link ServerSocket#bind(SocketAddress)}.
	 * @param $eventSource
	 *                the WorkTargetSelector to register this server socket with (the
	 *                registration will be performed by the time this constructor
	 *                returns).
	 * 
	 * @throws IOException
	 *                 if the bind operation fails, or if the socket is already bound.
	 * @throws SecurityException
	 *                 if a SecurityManager is present and its checkListen method
	 *                 doesn't allow the operation.
	 * @throws IllegalArgumentException
	 *                 if endpoint is a SocketAddress subclass not supported by this
	 *                 socket
	 */
	public ReadHeadSocketChannel(SocketAddress $localBinding, WorkTargetSelector $eventSource, WorkScheduler $scheduler) throws IOException {
		this.$pump = new PumpT();
		
		$ssc = ServerSocketChannel.open();
		$ssc.configureBlocking(false);
		$ssc.socket().bind($localBinding);
		$eventSource.register($ssc, getPump());	//TODO:AHS:IO: mkay so this should be a listener.  but who's he gonna tell?  we need a WorkFuture first.  and that implies we need to have schedule'd our WorkTarget with someone already!
		this.$ps = $eventSource;
	}
	
	private final ServerSocketChannel	$ssc;
	private final PumpT			$pump;
	private final WorkTargetSelector	$ps;
	
	/**
	 * Exposes the {@link ServerSocketChannel} that this ReadHead is decorating. (If
	 * you want to, for example, see what port was bound to after starting the socket,
	 * you can get it from this.)
	 */
	public ServerSocketChannel getServerSocketChannel() {
		return $ssc;
	}
	
	/**
	 * It should not prove necessary to use this method, since a ReadHeadSocketChannel
	 * is among the classes which it is appropriate to power via the indirection of a
	 * WorkTargetSelector and not by any other means.
	 */
	Pump getPump() {
		return $pump;
	}
	
	/**
	 * Closes the {@link ServerSocketChannel} that this ReadHead is decorating (as per
	 * the general contract for {@link ReadHead#close()}), then instructs the
	 * {@link WorkTargetSelector} that has been handling events for this channel to
	 * deregister it as soon as has a chance to do so.
	 */
	public void close() {
		try {
			$ssc.close();
		} catch (IOException $e) {
			handleException($e);
		}
		$ps.cancel($ssc);
	}
	
	
	//TODO:AHS:IO: so... should we have a method that exposes the WorkFuture here?  because it would actually be sensible to be able to wait for completion on that basis.  otherwise i guess the readhead's listener is the normative way to deal with that, and maybe that's enough.  but being able to use a future would let you use future pipes, as well as just plain being less noisy.
	//TODO:AHS:IO: if we want to make it possible to do flexible priority we'd have to either make a specific method for that, or expose the WorkTarget.  The latter would be kinda weird (we don't want to let people register it more than once, after all).
	//TODO:AHS:IO: shit, should we allow setting an additional listener that we have our internal one call in a chain?  NO, USE THE READHEAD DAMNIT.
	
	
	private class Wrok implements WorkTarget {
		public boolean isDone() {
			return isClosed();
		}
		
		public synchronized void run(final int $times) {
			for (int $i = 0; $i < $times; $i++) {
				if (isDone()) break;
				
				try {
					SocketChannel $chunk = TranslatorServerChannelToSocket.INSTANCE.translate($ssc);
					if ($chunk == null) break;
					$pipe.SINK.write($chunk);
				} catch (TranslationException $e) {
					handleException($e);
					break;
				}
			}
			if (!$ssc.isOpen()) $pipe.SRC.close();
		}
	}
}

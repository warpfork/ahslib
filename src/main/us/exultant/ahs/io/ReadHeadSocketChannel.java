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
 * @author Eric Myhre <tt>hash@exultant.us</tt>
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
	 * @param $scheduler
	 *                the scheduler that should be responsible for allocating threads
	 *                to do the work of accepting new sockets when they become
	 *                available.
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
		$work = new Matter();
		$listener = new Trigger();
		$wf = $scheduler.schedule($work, ScheduleParams.NOW);
		$ps = $eventSource;
		
		$ssc = ServerSocketChannel.open();
		$ssc.configureBlocking(false);
		$ssc.socket().bind($localBinding);
		$ps.registerAccept($ssc, $listener);
		
	}
	
	private final ServerSocketChannel		$ssc;
	private final Matter				$work;
	private final WorkFuture<SocketChannel>		$wf;
	private final Listener<SelectableChannel>	$listener;
	private final WorkTargetSelector		$ps;
	
	// i feel incredibly awkward about this method being a hanger-on to a readhead.  strongly feeling that there should be some other class here that's a container for a wf + rh.
	public WorkFuture<SocketChannel> getWorkFuture() {
		return $wf;
	}
	
	/**
	 * Exposes the {@link ServerSocketChannel} that this ReadHead is decorating. (If
	 * you want to, for example, see what port was bound to after starting the socket,
	 * you can get it from this.)
	 */
	public ServerSocketChannel getServerSocketChannel() {
		return $ssc;
	}
	
	/**
	 * Closes the {@link ServerSocketChannel} that this ReadHead is decorating (as per
	 * the general contract for {@link ReadHead#close()}), then instructs the
	 * {@link WorkTargetSelector} that has been handling events for this channel to
	 * deregister it as soon it has a chance to do so.
	 */
	public void close() {
		try {
			$ssc.close();
		} catch (IOException $e) {
			handleException($e);
		}
		$ps.cancel($ssc);
		$pipe.close();
	}
	
	
	
	private class Trigger implements Listener<SelectableChannel> {
		public void hear(SelectableChannel $x) {
			$work.$ready = true;
			$wf.update();
		}
	}
	
	
	
	private class Matter implements WorkTarget<SocketChannel> {
		private volatile boolean $ready;
		
		public boolean isDone() {
			return isClosed();
		}
		
		public boolean isReady() {
			return $ready;
		}
		
		public SocketChannel call() {
			if (isDone()) return null;
			
			try {
				/* set $ready to false on the optomistic assumption we're going about to do all the work */
				$ready = false;
				/* nom work from source */
				SocketChannel $chunk = TranslatorServerChannelToSocket.INSTANCE.translate($ssc);
				/* if selector again signalled more data came in here, $ready became true again.  (if we'd set $ready false after nom'ing, we'd have room for a race condition where we empty the source but new data comes in right after that and yet right before we false'd $ready.) */
				if ($chunk == null) return null;
				$pipe.SINK.write($chunk);
				$ready = true;	/* if we didn't find the work source already emptied out, we pessimistically assume that there's more to do. */
				return $chunk;
			} catch (TranslationException $e) {
				handleException($e);
			} finally {
				if (!$ssc.isOpen()) $pipe.SRC.close();
			}
			return null;
		}

		public int getPriority() {
			return 0;	//XXX:AHS:THREAD: this should be configurable later, at least at construction time and maybe just generally.
		}
	}
}

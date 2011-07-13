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
 * with any {@link PumperSelector}, since they aren't yet wrapped in any of the other
 * abstractions typical of the AHS library that would make a relationship with a
 * PumperSelector appropriate).
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
	 * @param $ps
	 *                the PumperSelector to register this server socket with (the
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
	public ReadHeadSocketChannel(SocketAddress $localBinding, PumperSelector $ps) throws IOException {
		this.$pump = new PumpT();
		
		$ssc = ServerSocketChannel.open();
		$ssc.configureBlocking(false);
		$ssc.socket().bind($localBinding);
		$ps.register($ssc, getPump());
		this.$ps = $ps;
	}
	
	private final ServerSocketChannel	$ssc;
	private final PumpT			$pump;
	private final PumperSelector		$ps;
	
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
	 * PumperSelector and not by any other means.
	 */
	Pump getPump() {
		return $pump;
	}
	
	/**
	 * Closes the {@link ServerSocketChannel} that this ReadHead is decorating (as per
	 * the general contract for {@link ReadHead#close()}), then instructs the
	 * {@link PumperSelector} that has been handling events for this channel to
	 * deregister it as soon as has a chance to do so.
	 */
	public void close() throws IOException {
		$ssc.close();
		$ps.cancel($ssc);
	}
	
	
	
	private class PumpT implements Pump {
		public boolean isDone() {
			return isClosed();
		}
		
		public synchronized void run(final int $times) {
			for (int $i = 0; $i < $times; $i++) {
				if (isDone()) break;
				
				try {
					SocketChannel $chunk = TranslatorChannelToSocket.instance.translate($ssc);
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

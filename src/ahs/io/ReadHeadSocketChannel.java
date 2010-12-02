package ahs.io;

import ahs.io.ReadHeadAdapter.Channelwise.*;
import ahs.util.*;
import ahs.util.thread.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;

/**
 * We assume you like your channels nonblocking.
 * 
 * @author hash
 * 
 */
public class ReadHeadSocketChannel extends ReadHeadAdapter<SocketChannel> {
	// I could have done all this with some interesting hacks and extending ReadHeadAdapter.ChannelwiseSelecting and hurling around wild nulls and having Translator with a shitload of state (including the serversocket) and making more abstract methods about closure state... but that seemed like more work than it was worth just to keep the same pump.  Oh, and I guess there would have been issues with exceptions from different places too. 
	
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
	
	public ServerSocketChannel getServerSocketChannel() {
		return $ssc;
	}
	
	public Pump getPump() {
		return $pump;
	}
	
	public void close() throws IOException {
		$ssc.close();
		$ps.deregister($pump);
	}
	
	
	
	
	
	private class PumpT implements Pump {
		public boolean isDone() {
			return isClosed();
		}
		
		public synchronized void run(final int $times) {
			for (int $i = 0; $i < $times; $i++) {
				if (isDone()) break;
				if (!$ssc.isOpen()) {
					baseEof();
					break;
				}
				
				try {
					try {
						SocketChannel $chunk = $ssc.accept();
						
						if ($chunk == null) break;
						$chunk.configureBlocking(false);
						
						$pipe.SINK.write($chunk);
					} catch (ClosedChannelException $e) {
						baseEof();
					}
				} catch (IOException $e) {
					ExceptionHandler<IOException> $dated_eh = $eh;
					if ($dated_eh != null) $dated_eh.hear($e);
					break;
				}
			}
		}
	}
}

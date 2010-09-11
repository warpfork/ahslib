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
	
	public ReadHeadSocketChannel(InetSocketAddress $localBinding, PumperSelector $ps) throws IOException {
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
				
				try {
					SocketChannel $chunk = $ssc.accept();
					
					// if we have no chunk it's just a non-blocking dude who doesn't have enough bytes for a semantic chunk
					if ($chunk == null) {
						if (!$ssc.isOpen())
							baseEof();
						break;	// we don't want to spin on it any more right now (and we might be done with it permanently).
					}
					$chunk.configureBlocking(false);
					
					// we have a chunk; wrap it up and enqueue to the buffer
					// any readers currently blocking will immediately Notice the new data due to the pipe's internal semaphore doing its job
					//  and the listener will automatically be notified as well
					$pipe.SINK.write($chunk);
					X.saye("alive");
				} catch (IOException $e) {
					ExceptionHandler<IOException> $dated_eh = $eh;
					if ($dated_eh != null) $dated_eh.hear($e);
					break;
				}
			}
		}
	}
}

package ahs.io;

import ahs.test.*;
import ahs.util.*;
import ahs.util.thread.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;

/**
 * Ho boy.
 * 
 * @author hash
 *
 */
public class NonBlockingTest extends TestCase {
	//	http://rox-xmlrpc.sourceforge.net/niotut/
	
	public void setUp() {
		$selector = new PumperSelector();
	}

	private static final int PORT = 34853;
	private PumperSelector $selector;
	
	public void testServerSocketSpin() throws IOException {
		ReadHead<SocketChannel> $rhsc = new ReadHeadSocketChannel(new InetSocketAddress(PORT), $selector);
		$selector.start();
		SocketChannel $sc1 = SocketChannel.open(new InetSocketAddress(PORT));
		$sc1.configureBlocking(false);
		SocketChannel $sc2 = $rhsc.read();
		
		WriteHead<ByteBuffer> $whsc1 = new WriteHeadBabbleToChannel($sc1);
		ReadHead <ByteBuffer> $rhsc2 = new ReadHeadChannelToBabble($sc2, $selector);
		
		
		X.saye("ack");
		X.chill(10000);
	}
}

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
		
		Socket $s1 = new Socket(InetAddress.getLocalHost(), PORT);
		$s1.
		WriteHead<ByteBuffer> $whbb = new WriteHeadBabbleToStream();
		new ReadHeadChannelToBabble();
		X.chill(10000);
	}
}

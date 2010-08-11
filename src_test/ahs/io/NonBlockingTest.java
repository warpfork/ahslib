package ahs.io;

import ahs.log.*;
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
		X.saye("");	// if running from the command line put a break after that dang period junit shoves in there
		$selector = new PumperSelector();
		LOG.DEBUG();
	}
	
	private static final Logger	LOG	= new Logger();
	private static final int	PORT	= 34853;
	private PumperSelector		$selector;
	
	public void testServerSocket() throws IOException {
		LOG.info(this, "Starting server socket test");
		
		ReadHead<SocketChannel> $rhsc = new ReadHeadSocketChannel(new InetSocketAddress(PORT), $selector);
		LOG.debug(this, "server socket bound and open");
		LOG.debug(this, "starting selector...");
		$selector.start();
		LOG.debug(this, "selector started.");
		LOG.debug(this, "opening new socket channel...");
		SocketChannel $sc1 = SocketChannel.open(new InetSocketAddress(PORT));
		LOG.debug(this, "new socket channel bound and open; setting nonblocking...");
		$sc1.configureBlocking(false);
		LOG.debug(this, "nonblocking set.");
		LOG.debug(this, "accepting new socket channel from ReadHeadSocketChannel...");
		SocketChannel $sc2 = $rhsc.read();
		LOG.debug(this, "new socket channel accepted.");
		
		LOG.info(this, "Opening ReadHead and WriteHead interfaces on socket channels");
		LOG.debug(this, "opening WriteHead on first socket channel...");
		WriteHead<ByteBuffer> $whsc1 = new WriteHeadBabbleToChannel($sc1);
		LOG.debug(this, "WriteHead open.");
		LOG.debug(this, "Opening ReadHead on accepted socket channel...");
		ReadHead <ByteBuffer> $rhsc2 = new ReadHeadChannelToBabble($sc2, $selector);
		LOG.debug(this, "ReadHead open and registered.");
		
		X.chill(10000);
		LOG.info(this, "server socket test finished.");
	}
}

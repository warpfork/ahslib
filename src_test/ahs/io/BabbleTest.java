package ahs.io;

import ahs.log.*;
import ahs.test.*;
import ahs.util.*;
import ahs.util.thread.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class BabbleTest extends JUnitTestCase {
	public void setUp() {
		X.saye("");	// if running from the command line put a break after that dang period junit shoves in there
		LOG.DEBUG();
	}
	
	private static final Logger	LOG	= new Logger();
	private static final int	PORT	= 34853;
	private static final ByteBuffer	T1 = ByteBuffer.wrap(new byte[] { 0x2, 0x7F, 0x44, -0x1, 0x0 });
	private static final ByteBuffer	T2 = ByteBuffer.wrap(new byte[] { 0x2, 0x7F, 0x44, -0x1, 0x7F, 0x44, -0x1, 0x7F, 0x44, -0x1, 0x7F, 0x44, -0x1, 0x7F, 0x44, -0x1, 0x7F, 0x44, -0x1, 0x7F, 0x44, -0x1, 0x7F, 0x44, -0x1, 0x0 });
	private static final ExceptionHandler<IOException> F = new ExceptionHandler<IOException>() {
		public void hear(IOException $ioe) {
			LOG.error(BabbleTest.class, "fuck", $ioe);
		}
	};
	
	public void testBlockingParadigm() throws IOException {
		LOG.info(this, "Starting blocking babble test");
		
		// open sockets and channels
		ServerSocketChannel $ssc = ServerSocketChannel.open();
		$ssc.configureBlocking(true);
		$ssc.socket().bind(new InetSocketAddress(PORT));
		SocketChannel $sc;
		LOG.debug(this, "server socket bound and open");
		LOG.debug(this, "opening new socket...");
		$sc = SocketChannel.open(new InetSocketAddress(PORT));
		assertTrue($sc.isBlocking());
		assertTrue($sc.isConnected());
		Socket $sock1 = $sc.socket();
		LOG.debug(this, "new socket bound and open.");
		LOG.debug(this, "accepting new socket from serversocket...");
		$sc = $ssc.accept();
		assertTrue($sc.isBlocking());
		assertTrue($sc.isConnected());
		Socket $sock2 = $sc.socket();
		LOG.debug(this, "new socket accepted.");
		
		// set up the Head interfaces
		WriteHead<ByteBuffer>	$whsc1;
		WriteHead<ByteBuffer>	$whsc2;
		ReadHead<ByteBuffer>	$rhsc1;
		ReadHead<ByteBuffer>	$rhsc2;
		LOG.info(this, "Opening ReadHead and WriteHead interfaces on socket channels");
		LOG.debug(this, "opening WriteHead on socket...");
		$whsc1 = new WriteHeadBabbleToStream($sock1);
		LOG.debug(this, "WriteHead open.");
		LOG.debug(this, "Opening ReadHead on accepted socket...");
		$rhsc2 = new ReadHeadStreamToBabble($sock2);
		$rhsc2.setExceptionHandler(F);
		//new PumperBasic($rhsc2.getPump()).start();
		LOG.debug(this, "ReadHead open and pumping.");
		LOG.debug(this, "opening WriteHead on accepted socket...");
		$whsc2 = new WriteHeadBabbleToStream($sock2);
		LOG.debug(this, "WriteHead open.");
		LOG.debug(this, "Opening ReadHead on socket...");
		$rhsc1 = new ReadHeadStreamToBabble($sock1);
		$rhsc1.setExceptionHandler(F);
		new PumperBasic($rhsc1.getPump()).start();
		LOG.debug(this, "ReadHead open and pumping.");
		
		// do writes
		LOG.debug(this, "writing to socket 1...");
		T1.rewind(); $whsc1.write(T1);
		LOG.debug(this, "writing to socket 2...");
		T2.rewind(); $whsc2.write(T2);
		LOG.debug(this, "writing to socket 2...");
		T1.rewind(); $whsc2.write(T1);
		LOG.debug(this, "writing to socket 1...");
		T1.rewind(); $whsc1.write(T1);
		LOG.debug(this, "writing to socket 1...");
		T2.rewind(); $whsc1.write(T2);
		
		// do reads and assert matches
		T1.rewind();
		T2.rewind();
		LOG.debug(this, "reading from socket 1...");
		assertEquals(T2, $rhsc1.read());
		LOG.debug(this, "reading from socket 1...");
		assertEquals(T1, $rhsc1.read());
		LOG.debug(this, "reading from socket 2...");
		assertEquals(T1, $rhsc2.read());
		LOG.debug(this, "reading from socket 2...");
		assertEquals(T1, $rhsc2.read());
		LOG.debug(this, "reading from socket 2...");
		assertEquals(T2, $rhsc2.read());
		
		$ssc.close();
		
		LOG.info(this, "blocking babble test finished.");
	}
	
	public void testBothParadigms() throws IOException {
		LOG.info(this, "Starting babble with channel and stream counterpart test");
		
		// open sockets and channels
		LOG.debug(this, "starting selector...");
		PumperSelector $selector = new PumperSelector();
		$selector.start();
		LOG.debug(this, "selector started.");
		ReadHead<SocketChannel> $rhsc = new ReadHeadSocketChannel(new InetSocketAddress(PORT), $selector);
		LOG.debug(this, "server socket bound and open");
		LOG.debug(this, "opening new socket...");
		Socket $sock = SocketChannel.open(new InetSocketAddress(PORT)).socket();
		LOG.debug(this, "new socket bound and open.");
		LOG.debug(this, "accepting new socket channel from ReadHeadSocketChannel...");
		SocketChannel $sc2 = $rhsc.read();
		LOG.debug(this, "new socket channel accepted.");
		
		// set up the Head interfaces
		WriteHead<ByteBuffer>	$whsc1;
		WriteHead<ByteBuffer>	$whsc2;
		ReadHead<ByteBuffer>	$rhsc1;
		ReadHead<ByteBuffer>	$rhsc2;
		LOG.info(this, "Opening ReadHead and WriteHead interfaces on socket channels");
		LOG.debug(this, "opening WriteHead on socket...");
		$whsc1 = new WriteHeadBabbleToStream($sock);
		LOG.debug(this, "WriteHead open.");
		LOG.debug(this, "Opening ReadHead on accepted socket channel...");
		$rhsc2 = new ReadHeadChannelToBabble($sc2, $selector);
		LOG.debug(this, "ReadHead open and registered.");
		LOG.debug(this, "opening WriteHead on accepted socket channel...");
		$whsc2 = new WriteHeadBabbleToChannel($sc2);
		LOG.debug(this, "WriteHead open.");
		LOG.debug(this, "Opening ReadHead on socket...");
		$rhsc1 = new ReadHeadStreamToBabble($sock);
		new PumperBasic($rhsc1.getPump()).start();
		LOG.debug(this, "ReadHead open and pumping.");
		
		// do writes
		LOG.debug(this, "writing to socket 1...");
		T1.rewind(); $whsc1.write(T1);
		LOG.debug(this, "writing to channel 2...");
		T2.rewind(); $whsc2.write(T2);
		LOG.debug(this, "writing to channel 2...");
		T1.rewind(); $whsc2.write(T1);
		LOG.debug(this, "writing to socket 1...");
		T1.rewind(); $whsc1.write(T1);
		LOG.debug(this, "writing to socket 1...");
		T2.rewind(); $whsc1.write(T2);
		
		// do reads and assert matches
		T1.rewind();
		T2.rewind();
		LOG.debug(this, "reading from socket 1...");
		assertEquals(T2, $rhsc1.read());
		LOG.debug(this, "reading from socket 1...");
		assertEquals(T1, $rhsc1.read());
		LOG.debug(this, "reading from channel 2...");
		assertEquals(T1, $rhsc2.read());
		LOG.debug(this, "reading from channel 2...");
		assertEquals(T1, $rhsc2.read());
		LOG.debug(this, "reading from channel 2...");
		assertEquals(T2, $rhsc2.read());
		
		LOG.info(this, "babble with channel and stream counterpart test finished.");
	}	
}

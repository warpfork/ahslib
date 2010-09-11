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
	private static final ByteBuffer	T1 = ByteBuffer.wrap(new byte[] { 0x2, 0x7F, 0x44, -0x1, 0x0 });
	private static final ByteBuffer	T2 = ByteBuffer.wrap(new byte[] { 0x2, 0x7F, 0x44, -0x1, 0x7F, 0x44, -0x1, 0x7F, 0x44, -0x1, 0x7F, 0x44, -0x1, 0x7F, 0x44, -0x1, 0x7F, 0x44, -0x1, 0x7F, 0x44, -0x1, 0x7F, 0x44, -0x1, 0x0 });
	private static final ByteBuffer TB;	// TestBig, not TeraByte.
	static {
		LOG.debug(NonBlockingTest.class, "making the big buffer...");
		Random $r = new Random();
		TB = ByteBuffer.allocate(1024 * 1024 * 10);	// 10MB
		while (TB.hasRemaining())
			TB.putInt($r.nextInt());
		LOG.debug(NonBlockingTest.class, "big buffer made.");
	}
	
	private static ReadHead<SocketChannel>	$rhsc;
	private static SocketChannel		$sc1;
	private static SocketChannel		$sc2;
	private static WriteHead<ByteBuffer>	$whsc1;
	private static WriteHead<ByteBuffer>	$whsc2;
	private static ReadHead<ByteBuffer>	$rhsc1;
	private static ReadHead<ByteBuffer>	$rhsc2;
	private static final ExceptionHandler<IOException> EH = new ExceptionHandler<IOException>() {
		public void hear(IOException $e) {
			LOG.warn(NonBlockingTest.class, "WAT", $e);
		}
	};
	
	public void testServerSocket() throws IOException {
		LOG.info(this, "Starting server socket test");
		
		$rhsc = new ReadHeadSocketChannel(new InetSocketAddress(PORT), $selector);
		$rhsc.setExceptionHandler(EH);
		LOG.debug(this, "server socket bound and open");
		LOG.debug(this, "starting selector...");
		$selector.start();
		LOG.debug(this, "selector started.");
		LOG.debug(this, "opening new socket channel...");
		$sc1 = SocketChannel.open(new InetSocketAddress(PORT));
		LOG.debug(this, "new socket channel bound and open; setting nonblocking...");
		$sc1.configureBlocking(false);
		LOG.debug(this, "nonblocking set.");
		LOG.debug(this, "accepting new socket channel from ReadHeadSocketChannel...");
		$sc2 = $rhsc.read();
		LOG.debug(this, "new socket channel accepted.");
		
		LOG.info(this, "Opening ReadHead and WriteHead interfaces on socket channels");
		LOG.debug(this, "opening WriteHead on first socket channel...");
		$whsc1 = new WriteHeadBabbleToChannel($sc1);
		LOG.debug(this, "WriteHead open.");
		LOG.debug(this, "Opening ReadHead on accepted socket channel...");
		$rhsc2 = new ReadHeadChannelToBabble($sc2, $selector);	// = ReadHeadAdapter.make($sc2, $selector, new ReadHeadAdapter.Channelwise.BabbleTranslator());
		$rhsc2.setExceptionHandler(EH);
		LOG.debug(this, "ReadHead open and registered.");
		LOG.debug(this, "opening WriteHead on accepted socket channel...");
		$whsc2 = new WriteHeadBabbleToChannel($sc2);
		LOG.debug(this, "WriteHead open.");
		LOG.debug(this, "Opening ReadHead on first socket channel...");
		$rhsc1 = new ReadHeadChannelToBabble($sc1, $selector);
		$rhsc1.setExceptionHandler(EH);
		LOG.debug(this, "ReadHead open and registered.");
		
		LOG.info(this, "server socket test finished.");
	}
	
	public void testBabble() throws IOException {
		LOG.info(this, "Starting babble test");
		
		//LOG.debug(this, "");
		LOG.debug(this, "writing to channel 1...");
		T1.rewind(); $whsc1.write(T1);
		LOG.debug(this, "writing to channel 2...");
		T2.rewind(); $whsc2.write(T2);
		LOG.debug(this, "writing to channel 2...");
		T1.rewind(); $whsc2.write(T1);
		LOG.debug(this, "writing to channel 1...");
		T1.rewind(); $whsc1.write(T1);
		LOG.debug(this, "writing to channel 1...");
		T2.rewind(); $whsc1.write(T2);
		
		T1.rewind();
		T2.rewind();
		// should get what we wrote to channel to the other channel, mind you.
		LOG.debug(this, "reading from channel 1...");
		assertEquals(T2, $rhsc1.read());
		LOG.debug(this, "reading from channel 1...");
		assertEquals(T1, $rhsc1.read());
		LOG.debug(this, "reading from channel 2...");
		assertEquals(T1, $rhsc2.read());
		LOG.debug(this, "reading from channel 2...");
		assertEquals(T1, $rhsc2.read());
		LOG.debug(this, "reading from channel 2...");
		assertEquals(T2, $rhsc2.read());
		
		LOG.info(this, "babble test finished.");
	}
	
	public void testBabbleBig() throws IOException {
		LOG.info(this, "Starting big babble test");
		
		LOG.debug(this, "writing to channel 1...");
		TB.rewind(); $whsc1.write(TB);
		
		TB.rewind();
		LOG.debug(this, "reading from channel 2...");
		assertEquals(TB, $rhsc2.read());
		
		LOG.info(this, "big babble test finished.");
	}
}

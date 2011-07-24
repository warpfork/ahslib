package us.exultant.ahs.io;

import us.exultant.ahs.core.*;
import us.exultant.ahs.log.*;
import us.exultant.ahs.test.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class NonblockingChannelTranslatorTest extends TestCase {
	public static void main(String... $args) {						new NonblockingChannelTranslatorTest().run();	}
	public NonblockingChannelTranslatorTest() {						super(new Logger(Logger.LEVEL_DEBUG), true);	}
	public NonblockingChannelTranslatorTest(Logger $log, boolean $enableConfirmation) {	super($log, $enableConfirmation);		}
	public List<Unit> getUnits() {
		List<Unit> $tests = new ArrayList<Unit>();
		//$tests.add(new ConnectionSetup());
		//$tests.add(new FlowSetup());
		$tests.add(new TestBasic());
		return $tests;
	}
	private static final TestData TD = TestData.getFreshTestData();
	private static final TestData TDA = TestData.getFreshTestData();
	
	public class ConnectionSetup extends TestCase.Unit {
		public PumperSelector $ps;
		public ReadHeadSocketChannel $rhsc;
		public SocketChannel $sc0c, $sc0s;
		public SocketChannel $sc1c, $sc1s;
		
		public Object call() throws IOException {
			$ps = new PumperSelector();
			$ps.start();
			$rhsc = new ReadHeadSocketChannel(null, $ps);
			
			$sc0c = SocketChannel.open();
			$sc0c.connect($rhsc.getServerSocketChannel().getLocalAddress());
			
			$sc0s = $rhsc.read();
			
			$sc1c = SocketChannel.open();
			$sc1c.connect($rhsc.getServerSocketChannel().getLocalAddress());
			
			$sc1s = $rhsc.read();
			
			assertEquals(0, $rhsc.$pipe.size());
			breakIfFailed();
			return null;
		}
	}
	
	private class FlowSetup extends ConnectionSetup {
		public Flow<ByteBuffer> $f0c;
		public Flow<ByteBuffer> $f0s;
		public Flow<ByteBuffer> $f1c;
		public Flow<ByteBuffer> $f1s;
		
		public Object call() throws IOException {
			super.call();
			
			$f0c = FlowAssembler.wrap($sc0c, $ps);
			$f0s = FlowAssembler.wrap($sc0s, $ps);
			$f1c = FlowAssembler.wrap($sc1c, $ps);
			$f1s = FlowAssembler.wrap($sc1s, $ps);
			
			return null;
		}
	}
	
	private class TestBasic extends FlowSetup {
		public Object call() throws IOException {
			super.call();
			
			$f0c.sink().write(TD.bb26);
			assertEquals(TDA.bb26, $f0s.source().read());
			
			return null;
		}
	}
}

package us.exultant.ahs.io;

import us.exultant.ahs.test.*;

public class PipeTest extends JUnitTestCase {
	public void testRead() {
		Pipe<String> $pipe = new Pipe<String>();
		$pipe.SINK.write("asdf");
		$pipe.SINK.write("qwer");
		assertEquals("asdf",$pipe.SRC.read());
		$pipe.SINK.write("zxcv");
		assertEquals("qwer",$pipe.SRC.read());
		assertEquals("zxcv",$pipe.SRC.read());
	}
	
	public void testClose() {
		Pipe<String> $pipe = new Pipe<String>();
		$pipe.SINK.write("asdf");
		$pipe.SINK.write("qwer");
		assertEquals("asdf",$pipe.SRC.read());
		$pipe.SINK.close();
		try {
			$pipe.SINK.write("zxcv");
			fail("should have thrown.");
		} catch (IllegalStateException $e) {
			/* good */
		}
		assertEquals("qwer",$pipe.SRC.read());
		assertEquals(null,$pipe.SRC.readNow());
	}
	
	
}

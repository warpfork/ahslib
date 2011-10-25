package us.exultant.ahs.thread;

import us.exultant.ahs.util.*;
import us.exultant.ahs.log.*;
import us.exultant.ahs.test.*;
import java.util.*;

public class PipeTest extends TestCase {
	public static void main(String... $args) {			new PipeTest().run();				}
	public PipeTest() {						super(new Logger(Logger.LEVEL_DEBUG), true);	}
	public PipeTest(Logger $log, boolean $enableConfirmation) {	super($log, $enableConfirmation);		}
	public List<Unit> getUnits() {
		List<Unit> $tests = new ArrayList<Unit>();
		$tests.add(new TestBasic());
		$tests.add(new TestBasic_WriteAll());
		TestBasic_WriteAllPartial $wap = new TestBasic_WriteAllPartial();
		$tests.add($wap.new Part1());
		$tests.add($wap.new Part2());
		$tests.add(new TestBasicClose_WriteAfter());
		$tests.add(new TestBasicClose_ReadAfter());
		$tests.add(new TestConcurrent_ReadWriteBlocking());
		$tests.add(new TestConcurrent_Close());
		return $tests;
	}
	private static final TestData TD = TestData.getFreshTestData();
	
	/** Just tests mixed read and writes in a single thread. */
	private class TestBasic extends TestCase.Unit {
		public Object call() {
			Pipe<String> $pipe = new Pipe<String>();
			$pipe.SINK.write(TD.s1);
			$pipe.SINK.write(TD.s2);
			assertEquals(2, $pipe.size());
			breakIfFailed();
			assertEquals(TD.s1, $pipe.SRC.read());
			$pipe.SINK.write(TD.s3);
			assertEquals(2, $pipe.size());
			assertEquals(TD.s2, $pipe.SRC.read());
			assertEquals(TD.s3, $pipe.SRC.read());
			assertEquals(0, $pipe.size());
			return null;
		}
	}
	
	/** Tests the group writing of collected chunks. */
	private class TestBasic_WriteAll extends TestCase.Unit {
		public Object call() {
			Pipe<String> $pipe = new Pipe<String>();
			$pipe.SINK.write(TD.s1);
			$pipe.SINK.writeAll(Arr.asList(TD.s2,TD.s2,TD.s3));
			assertEquals(4, $pipe.size());
			breakIfFailed();
			List<String> $arr = $pipe.SRC.readAllNow();
			assertEquals(0, $pipe.size());
			assertEquals(TD.s1, $arr.get(0));
			assertEquals(TD.s2, $arr.get(1));
			assertEquals(TD.s2, $arr.get(2));
			assertEquals(TD.s3, $arr.get(3));
			return null;
		}
	}
	
	/** Tests the consistency after a group write throws an exception from the middle of the operation. */
	private class TestBasic_WriteAllPartial {
		Pipe<String> $pipe = new Pipe<String>();
		
		/** Tests that yes, an exception is thrown. */
		private class Part1 extends TestCase.Unit {
			@SuppressWarnings("unchecked")
			public Class<NullPointerException> expectExceptionType() {
				return NullPointerException.class;
			}
			public Object call() {
				$pipe.SINK.write(TD.s1);
				$pipe.SINK.writeAll(Arr.asList(TD.s2,null,TD.s3));
				return null;
			}
		}
		
		/** Tests that the Pipe's size and contents are still consistent, and that it contains exactly the elements preceeding the one that caused the exception. */
		private class Part2 extends TestCase.Unit {
			public Object call() {
				assertEquals(2, $pipe.size());
				breakIfFailed();
				List<String> $arr = $pipe.SRC.readAllNow();
				assertEquals(0, $pipe.size());
				assertEquals(TD.s1, $arr.get(0));
				assertEquals(TD.s2, $arr.get(1));
				return null;
			}
		}
	}
	
	/** Tests that attempting to write after closing a pipe throws an exception. */
	private class TestBasicClose_WriteAfter extends TestCase.Unit {
		@SuppressWarnings("unchecked")
		public Class<IllegalStateException> expectExceptionType() {
			return IllegalStateException.class;
		}
		public Object call() {
			Pipe<String> $pipe = new Pipe<String>();
			$pipe.SINK.write(TD.s1);
			$pipe.SINK.write(TD.s2);
			assertEquals(TD.s1, $pipe.SRC.read());
			$pipe.SINK.close();
			$pipe.SINK.write(TD.s3);	// this should throw
			return null;
		}
	}
	
	private class TestBasicClose_ReadAfter extends TestCase.Unit {
		public Object call() {
			Pipe<String> $pipe = new Pipe<String>();
			$pipe.SINK.write(TD.s1);
			$pipe.SINK.write(TD.s2);
			assertEquals(2, $pipe.size());
			assertEquals(TD.s1, $pipe.SRC.read());
			$pipe.SINK.close();
			assertEquals(TD.s2, $pipe.SRC.read());
			assertEquals(0, $pipe.size());
			assertEquals(null, $pipe.SRC.readNow());
			breakIfFailed();	// we'd rather not block on this next call if we already know there's something wrong.
			assertEquals(null, $pipe.SRC.read());		// this may block forever if something's broken
			assertEquals(0, $pipe.SRC.readAll().size());	// this may block forever if something's broken
			return null;
		}
	}
	
	private class TestConcurrent_ReadWriteBlocking extends TestCase.Unit {
		Pipe<String> $pipe = new Pipe<String>();
		ConcurrentCounter<String> $counter = ConcurrentCounter.make(Arr.asList(TD.s1, TD.s2, TD.s3));
		final int n = 10000;
		
		// VAGUE PERFORMANCE OBSERVATIONS (at n=1000000):
		//  about (min:23k; max:35k; ave:27k)/sec on a 2.7ghz+4core+ubuntu10.10; only about 50% of 2 cores utilized (20% userspace, 30% kernel). 
		
		public Object call() {
			Runnable[] $tasks = new Runnable[4];
			$tasks[0] = new Writer(TD.s1);
			$tasks[1] = new Writer(TD.s2);
			$tasks[2] = new Reader();
			$tasks[3] = new Reader();
			ThreadUtil.doAll($tasks);
			assertEquals(n, $counter.getCount(TD.s1));
			assertEquals(n, $counter.getCount(TD.s2));
			assertEquals(0, $counter.getCount(TD.s3));
			return null;
		}
		

		private class Writer implements Runnable {
			public Writer(String $str) { this.$str = $str; }
			private String $str;
			public void run() {
				for (int $i = 0; $i < n; $i++)
					$pipe.SINK.write($str);
			}
		}
		private class Reader implements Runnable {
			public Reader() {}
			public void run() {
				for (int $i = 0; $i < n; $i++)
					$counter.hear($pipe.SRC.read());
			}
		}
	}

	private class TestConcurrent_Close extends TestCase.Unit {
		Pipe<String> $pipe = new Pipe<String>();
		ConcurrentCounter<String> $counter = ConcurrentCounter.make(Arr.asList(TD.s1));
		final int n = 10000;
		final int n2 = 100;
		
		public Object call() {
			Runnable[] $tasks = new Runnable[4];
			$tasks[0] = new Writer(TD.s1);	// puts 2n+n2
			$tasks[1] = new Reader();	// consumes up to n
			$tasks[2] = new Reader();	// consumes up to n
			$tasks[3] = new FinalReader();	// consumes some arbitrary amount based on thread scheduling, minimum n2.
			ThreadUtil.doAll($tasks);
			assertEquals(2*n+n2, $counter.getCount(TD.s1));
			return null;
		}
		

		private class Writer implements Runnable {
			public Writer(String $str) { this.$str = $str; }
			private String $str;
			public void run() {
				for (int $i = 0; $i < (2*n)+n2; $i++)
					$pipe.SINK.write($str);
				$pipe.close();
			}
		}
		private class Reader implements Runnable {
			public Reader() {}
			public void run() {
				for (int $i = 0; $i < n; $i++)
					$counter.hear($pipe.SRC.read());
			}
		}
		private class FinalReader implements Runnable {
			public FinalReader() {}
			public void run() {
				for (String $s : $pipe.SRC.readAll())
					$counter.hear($s);
			}
		}
	}
	
	// if a pipe is fed, closed, and then drained, we should see exactly n+2 events (one for the closure, one for the final drain, and one for each of the (unbatched) writes)... even if there is more than one person trying to get that last read.
	//  jk, that's all impossible because pipes can't be arsed to check that that final drain event is a once-only.
}

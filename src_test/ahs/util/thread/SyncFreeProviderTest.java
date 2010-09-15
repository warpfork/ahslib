package ahs.util.thread;

import ahs.util.*;
import ahs.test.*;

import java.util.*;
import java.util.concurrent.locks.*;

public class SyncFreeProviderTest extends JUnitTestCase {
	public static class Counter {
		private int $i = 0;
		public int get() { return $i; }
		public void inc() { $i++; }
		protected Object clone() { Counter $t = new Counter(); $t.$i = $i; return $t; }
	}
	public static class Sprinter implements Runnable {
		public Sprinter(int $goal, SyncFreeProvider<Counter> $sfp) {
			$sink = new ReentrantLock(true);
			$tool = new Counter();
			$tool.$i = -1;
			this.$goal = $goal;
			this.$sfp = $sfp;
		}

		private int $goal;
		private Lock $sink;
		private SyncFreeProvider<Counter> $sfp;
		private Counter $tool;
		
		public void run() {
			$sink.lock();
			X.saye("Thread "+Thread.currentThread().getId()+" started.");
			try {
				for (int $i = 0; $i < $goal; $i++) {
					//X.saye("RAH, thread "+Thread.currentThread().getId()+" counted.");
					$tool = $sfp.get();
					$tool.inc();
				}
			} catch (Throwable $t) {
				System.err.println(X.toString($t));
			}
			X.saye("Thread "+Thread.currentThread().getId()+" unlocking...");
			$sink.unlock();
			X.saye("Thread "+Thread.currentThread().getId()+" finished.");
		}
		
		public void test() {
			$sink.lock();
			assertEquals($goal, $tool.get());
			$sink.unlock();
		}
	}
	
	
	public void testBasicUsingClone() throws InterruptedException {
		final int TRIALS = 500;
		
		SyncFreeProvider<Counter> $sfp = new SyncFreeProvider<Counter>(new Counter());
		List<Sprinter> $ts = new ArrayList<Sprinter>(TRIALS);
		List<Thread> $ths = new ArrayList<Thread>(TRIALS);
		for (int $i = 0; $i < TRIALS; $i++)
			$ts.add(new Sprinter(500010, $sfp));	// init sprinter
		for (int $i = 0; $i < TRIALS; $i++)
			$ths.add(new Thread($ts.get($i)));	// init thread
		for (int $i = 0; $i < TRIALS; $i++)
			$ths.get($i).start();			// start thread
		for (int $i = 0; $i < TRIALS; $i++)
			$ths.get($i).join();			// join thread
		for (int $i = 0; $i < TRIALS; $i++)
			$ts.get($i).test();			// assert victory
	}
}

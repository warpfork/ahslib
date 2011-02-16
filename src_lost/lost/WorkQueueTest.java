package ahs.util.thread;

import ahs.test.*;
import ahs.util.*;
import java.util.*;

import junit.framework.*;
import ahs.test.TestCase;

public class WorkQueueTest extends TestCase {
	public void setUp() {
		$q = new WorkQueue<String>();
	}
	
	private WorkQueue<String> $q;
	private static final String s0 = "s0";
	private static final String s1 = "s1";
	private static final String s2 = "s2";
	private static final String s3 = "s3";
	private static final String s4 = "s4";
	private static final String s5 = "s5";
	private static final String s6 = "s6";
	private static final String s7 = "s7";
	private static final String s8 = "s8";
	private static final String s9 = "s9";	
	
	
	
	/**
	 * One thread doing all writes and all reads (though the two are intermingled);
	 * all operations concern only the head and the tail.
	 */
	public void testBasic() {
		assertEquals(0, $q.size());
		$q.add(s0);
		assertEquals(1, $q.size());
		$q.offer(s1);
		assertEquals(2, $q.size());
		assertEquals(s0,$q.remove());
		assertEquals(1, $q.size());
		assertEquals(s1,$q.peek());
		assertEquals(1, $q.size());
		$q.add(s2);
		assertEquals(2, $q.size());
		assertEquals(s1,$q.poll());
		assertEquals(1, $q.size());
		assertEquals(s2,$q.element());
		assertEquals(1, $q.size());
		assertEquals(s2,$q.remove());
		assertEquals(0, $q.size());
		assertEquals(null,$q.poll());
	}
	
	
	
	/**
	 * One thread doing all writes and all reads (though the two are intermingled);
	 * operations are tested even if they concern more than only the head and the
	 * tail.
	 */
	public void testBasicMore() {
		$q.add(s0);
		$q.offer(s1);
		assertEquals(2, $q.size());
		$q.addAll(Arr.asList(new String[] { s0, s1, s2}));
		assertEquals(5, $q.size());
		assertTrue($q.contains(s0));
		assertTrue($q.contains(s1));
		assertTrue($q.contains(s2));
		assertTrue($q.containsAll(Arr.asList(new String[] { s0, s1, s2})));
		assertEquals(5, $q.size());
		
		$q.removeAll(Arr.asList(new String[] { s0, s2}));
		assertEquals(2, $q.size());
		assertEquals(s1,$q.element());
		assertEquals(s1,$q.element());
		assertEquals(s1,$q.poll());
		assertEquals(s1,$q.element());
		assertEquals(1, $q.size());
	}
	
	
	
	/**
	 * Operations affecting only the head and tail are effected by 8 threads and are
	 * expected to simply reach a zero sum.
	 */
	public void testBasicThread() throws InterruptedException {
		final int $threads = 8;
		final int $mult = 1000000;
		
		int $i;
		List<Thread> $ts = new ArrayList<Thread>();
		$ts.add(new Thread(new Bee($q, $mult, s0)));
		$ts.add(new Thread(new Bee($q, $mult, s1)));
		$ts.add(new Thread(new Bee($q, $mult, s2)));
		$ts.add(new Thread(new Bee($q, $mult, s3)));
		$ts.add(new Thread(new Bee($q, $mult, s4)));
		$ts.add(new Thread(new Dee($q, $mult)));
		$ts.add(new Thread(new Dee($q, $mult)));
		$ts.add(new Thread(new Dee($q, $mult)));
		for ($i = 0; $i < $threads; $i++)
			$ts.get($i).start();
		for ($i = 0; $i < $threads; $i++)
			$ts.get($i).join();
		
		assertEquals($mult*2, $q.size());
	}
	private static final class Bee implements Runnable {
		public Bee(WorkQueue<String> $q, int $c, String $s) {
			this.$q = $q;
			this.$c = $c;
			this.$s = $s;
		}
		
		private WorkQueue<String>	$q;
		private int			$c;
		private String			$s;
		
		public void run() {
			for (; $c > 0; $c--)
				$q.add($s);
		}
	};
	private static final class Dee implements Runnable {
		public Dee(WorkQueue<String> $q, int $c) {
			this.$q = $q;
			this.$c = $c;
		}
		
		private WorkQueue<String>	$q;
		private int			$c;
		
		public void run() {
			for (; $c > 0; $c--) {
				$q.element();
				$q.remove();
			}
		}
	};
	
	

	/**
	 * Operations affecting the entire queue are effected by 16 threads and are
	 * expected to reach a sane state.
	 */
	public void testBasicMoreThreadMore() throws InterruptedException {
		final int $threads = 16;
		final int $mult = 1000;
		
		int $i;
		List<Thread> $ts = new ArrayList<Thread>();
		$ts.add(new Thread(new Qwee($q, $mult)));
		$ts.add(new Thread(new Qwee($q, $mult)));
		$ts.add(new Thread(new Qwee($q, $mult)));
		$ts.add(new Thread(new Qwee($q, $mult)));
		$ts.add(new Thread(new Zee($q, $mult)));
		$ts.add(new Thread(new Zee($q, $mult)));
		$ts.add(new Thread(new Zee($q, $mult)));
		$ts.add(new Thread(new Zee($q, $mult)));
		$ts.add(new Thread(new Qwee($q, $mult)));
		$ts.add(new Thread(new Qwee($q, $mult)));
		$ts.add(new Thread(new Zee($q, $mult)));
		$ts.add(new Thread(new Zee($q, $mult)));
		$ts.add(new Thread(new Bee($q, $mult, s3)));
		$ts.add(new Thread(new Bee($q, $mult, s3)));
		$ts.add(new Thread(new Bee($q, $mult, s4)));
		$ts.add(new Thread(new Bee($q, $mult, s4)));
		for ($i = 0; $i < $threads; $i++)
			$ts.get($i).start();
		for ($i = 0; $i < $threads; $i++)
			$ts.get($i).join();
		
		assertEquals($q.qsize(), $q.size());	// this isn't the same on every run since we don't know if the last Zee happened after the last Qwee
		
		Thread $t = new Thread(new Zee($q, 1));
		$t.start();
		$t.join();
		assertEquals($mult*10, $q.size());
	}
	private static final class Qwee implements Runnable {
		public Qwee(WorkQueue<String> $q, int $c) {
			this.$q = $q;
			this.$c = $c;
		}
		
		private WorkQueue<String>	$q;
		private int			$c;
		
		public void run() {
			for (; $c > 0; $c--)
				$q.addAll(Arr.asList(new String[] { s0, s1, s2}));
		}
	};
	private static final class Zee implements Runnable {
		public Zee(WorkQueue<String> $q, int $c) {
			this.$q = $q;
			this.$c = $c;
		}
		
		private WorkQueue<String>	$q;
		private int			$c;
		
		public void run() {
			for (; $c > 0; $c--) {
				$q.element();
				$q.removeAll(Arr.asList(new String[] { s1, s2}));
			}
		}
	};
	
	/**
	 * Does the previous test as many times as my patience can readily bear (which
	 * turns out to be a few hundred -- 1000 is like 15 minutes).
	 */
	public void testBasicMoreThreadMoreRepeatedly() throws InterruptedException {
		final int $mult = 200;
		long $start = X.time();
		for (int $i = 0; $i < $mult; $i++) {
			try {
				setUp();
				testBasicMoreThreadMore();
			} catch (AssertionFailedError $e) {
				throw new Error("run "+$i, $e);
			}
		}
		X.saye((X.time()-$start)/$mult+" ms per QweeZee run");
	}
	
	
}

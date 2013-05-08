/*
 * Copyright 2010 - 2013 Eric Myhre <http://exultant.us>
 *
 * This file is part of AHSlib.
 *
 * AHSlib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License, or
 * (at the original copyright holder's option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package us.exultant.ahs.thread;

import us.exultant.ahs.util.*;
import us.exultant.ahs.test.*;
import java.util.*;

public class DataPipeTest extends TestCase {
	public static void main(String... $args) { new DataPipeTest().run(); }

	public List<Unit> getUnits() {
		List<Unit> $tests = new ArrayList<Unit>();
		$tests.add(new TestBasic());
		$tests.add(new TestBasic_WriteAll());
		TestBasic_WriteAllPartial $wap = new TestBasic_WriteAllPartial();
		$tests.add($wap.new Part1());
		$tests.add($wap.new Part2());
		$tests.add(new TestBasicClose_WriteAfter());
		$tests.add(new TestBasicClose_ReadAfterCloseReturns());
		$tests.add(new TestConcurrent_ReadBlockBeforeWrite());
		$tests.add(new TestConcurrent_ReadWriteBlocking());
		$tests.add(new TestConcurrent_ReadPauseyWriteBlocking());
		$tests.add(new TestConcurrent_Close());
		return $tests;
	}
	private static final TestData TD = TestData.getFreshTestData();

	/** Just tests mixed read and writes in a single thread. */
	private class TestBasic extends TestCase.Unit {
		public void call() {
			Pipe<String> $pipe = new DataPipe<String>();
			$pipe.sink().write(TD.s1);
			$pipe.sink().write(TD.s2);
			assertEquals(2, $pipe.size());
			breakUnitIfFailed();
			assertEquals(TD.s1, $pipe.source().read());
			$pipe.sink().write(TD.s3);
			assertEquals(2, $pipe.size());
			assertEquals(TD.s2, $pipe.source().read());
			assertEquals(TD.s3, $pipe.source().read());
			assertEquals(0, $pipe.size());
		}
	}

	/** Tests the group writing of collected chunks. */
	private class TestBasic_WriteAll extends TestCase.Unit {
		public void call() {
			Pipe<String> $pipe = new DataPipe<String>();
			$pipe.sink().write(TD.s1);
			$pipe.sink().writeAll(Arr.asList(TD.s2,TD.s2,TD.s3));
			assertEquals(4, $pipe.size());
			breakUnitIfFailed();
			List<String> $arr = $pipe.source().readAllNow();
			assertEquals(0, $pipe.size());
			assertEquals(TD.s1, $arr.get(0));
			assertEquals(TD.s2, $arr.get(1));
			assertEquals(TD.s2, $arr.get(2));
			assertEquals(TD.s3, $arr.get(3));
		}
	}

	/** Tests the consistency after a group write throws an exception from the middle of the operation. */
	private class TestBasic_WriteAllPartial {
		Pipe<String> $pipe = new DataPipe<String>();

		/** Tests that yes, an exception is thrown. */
		private class Part1 extends TestCase.Unit {
			@SuppressWarnings("unchecked")
			public Class<NullPointerException> expectExceptionType() {
				return NullPointerException.class;
			}
			public void call() {
				$pipe.sink().write(TD.s1);
				$pipe.sink().writeAll(Arr.asList(TD.s2,null,TD.s3));
			}
		}

		/** Tests that the Pipe's size and contents are still consistent, and that it contains exactly the elements preceeding the one that caused the exception. */
		private class Part2 extends TestCase.Unit {
			public void call() {
				assertEquals(2, $pipe.size());
				breakUnitIfFailed();
				List<String> $arr = $pipe.source().readAllNow();
				assertEquals(0, $pipe.size());
				assertEquals(TD.s1, $arr.get(0));
				assertEquals(TD.s2, $arr.get(1));
			}
		}
	}

	/** Tests that attempting to write after closing a pipe throws an exception. */
	private class TestBasicClose_WriteAfter extends TestCase.Unit {
		@SuppressWarnings("unchecked")
		public Class<IllegalStateException> expectExceptionType() {
			return IllegalStateException.class;
		}
		public void call() {
			Pipe<String> $pipe = new DataPipe<String>();
			$pipe.sink().write(TD.s1);
			$pipe.sink().write(TD.s2);
			assertEquals(TD.s1, $pipe.source().read());
			$pipe.sink().close();
			$pipe.sink().write(TD.s3);	// this should throw
		}
	}

	private class TestBasicClose_ReadAfterCloseReturns extends TestCase.Unit {
		public void call() throws InterruptedException {
			Pipe<String> $pipe = new DataPipe<String>();
			$pipe.sink().write(TD.s1);
			$pipe.sink().write(TD.s2);
			assertEquals(2, $pipe.size());
			assertEquals(TD.s1, $pipe.source().read());
			$pipe.sink().close();
			assertEquals(TD.s2, $pipe.source().read());
			assertEquals(0, $pipe.size());
			assertEquals(null, $pipe.source().readNow());
			breakUnitIfFailed();	// we'd rather not block on this next call if we already know there's something wrong.
			assertEquals(null, $pipe.source().read());		// this may block forever if something's broken
			assertEquals(0, $pipe.source().readAll().size());	// this may block forever if something's broken
		}
	}

	private class TestConcurrent_ReadBlockBeforeWrite extends TestCase.Unit {
		Pipe<String> $pipe = new DataPipe<String>();
		volatile boolean $won = false;

		public void call() {
			new Thread() { public void run() {
					$pipe.source().read();
					$won = true;
			}}.start();
			$pipe.sink().write(TD.s1);
			while (!$won) X.chill(5);
			// honestly, just making it out of here alive is test enough.
		}
	}

	private class TestConcurrent_ReadWriteBlockingGeneral extends TestCase.Unit {
		/**
		 * @param $msgsPerThread
		 *                you'll end up with ($msgsPerThread*$threadPairsToSpawn)
		 *                messages being passed
		 * @param $threadPairsToSpawn
		 *                you'll end up with ($threadPairsToSpawn*2) threads; half
		 *                of them writers and half of them readers.
		 * @param $writesBetweenDelay
		 *                if >0, a one millisecond wait will be performed by the
		 *                writing threads after every $writesBetweenDelay
		 *                messages.
		 */
		public TestConcurrent_ReadWriteBlockingGeneral(int $msgsPerThread, int $threadPairsToSpawn, int $writesBetweenDelay) {
			this.$msgsPerThread = $msgsPerThread;
			this.$threadPairsToSpawn = $threadPairsToSpawn;
			this.$writesBetweenDelay = $writesBetweenDelay;
			List<String> $words = new ArrayList<String>($threadPairsToSpawn);
			for (int $i = 0; $i < $threadPairsToSpawn; $i++)
				$words.add("w"+$i);
			this.$counter = ConcurrentCounter.make($words);
		}

		final Pipe<String> $pipe = new DataPipe<String>();
		final ConcurrentCounter<String> $counter;
		final int $msgsPerThread;
		final int $threadPairsToSpawn;
		final int $writesBetweenDelay;

		// VAGUE PERFORMANCE OBSERVATIONS (at $msgsPerThread=1000000, $threadPairsToSpawn=2):
		// first of all, note that these are really, really vague.  i made no attempt to factor out the impact of that event counter.
		//  same as below test on same code and exact same hardware but with ubuntu11.10 (and a 3.0.x kernel and java 1.6.0.30)
		//   about 930k easily; about 80% of cores utilized (~13% kernel, ~67% userspace).
		//   so if you didn't believe it until now, your kernel version most definitely Matters to concurrency performance.
		//  with the modern generation of flippable-semaphore-based pipes:
		//   about (min;432k; max:663k; ave:541k)/sec on a 2.7ghz+4core+ubuntu11.04; about 95% of all cores utilized (~5% kernel, ~90% userspace).
		//   performance remains in that range when increasing n another 100x, as well, if you're wondering.
		//   of course if you drop n it goes to crap: n=100:~7k/sec; n=1000:~20k/sec; n=10000:~40k/sec; n=100000:~174k/sec;
		//  with the older generation of interrupt-based pipes:
		//   about (min:23k;  max:35k;  ave:27k)/sec  on a 2.7ghz+4core+ubuntu10.10; only about 50% of 2 cores utilized (20% userspace, 30% kernel).

		public void call() {
			Runnable[] $tasks = new Runnable[$threadPairsToSpawn*2];
			for (int $i = 0; $i < $threadPairsToSpawn; $i++)
				$tasks[$i] = new Reader();
			for (int $i = 0; $i < $threadPairsToSpawn; $i++)
				$tasks[$threadPairsToSpawn+$i] = new Writer("w"+$i);

			long $start = X.time();
			ThreadUtil.doAll($tasks);
			long $time = X.time() - $start;

			for (int $i = 0; $i < $threadPairsToSpawn; $i++)
				assertEquals($msgsPerThread, $counter.getCount("w"+$i));
			$log.info("performance {} kops/sec", (($msgsPerThread/1000.0)/($time/1000.0)));
		}

		private class Writer implements Runnable {
			public Writer(String $str) { this.$str = $str; }
			private String $str;
			public void run() {
				for (int $i = 0; $i < $msgsPerThread; $i++) {
					$pipe.sink().write($str);
					if ($log.isTraceEnabled()) $log.trace("wrote \""+$str+"\", pipe size now "+$pipe.size());
					if ($writesBetweenDelay > 0 && $i % $writesBetweenDelay == 0) X.chill(1);
				}
				$log.trace("writing thread done.");
			}
		}
		private class Reader implements Runnable {
			public Reader() {}
			public void run() {
				for (int $i = 0; $i < $msgsPerThread; $i++) {
					String $lol = $pipe.source().read();
					if ($log.isTraceEnabled()) $log.trace("read \""+$lol+"\", pipe size now "+$pipe.size());
					$counter.hear($lol);
				}
				$log.trace("reading thread done.");
			}
		}
	}



	private class TestConcurrent_ReadWriteBlocking extends TestConcurrent_ReadWriteBlockingGeneral {
		public TestConcurrent_ReadWriteBlocking() {
			super(100, 2, 0);
		}
	}



	private class TestConcurrent_ReadPauseyWriteBlocking extends TestConcurrent_ReadWriteBlockingGeneral {
		public TestConcurrent_ReadPauseyWriteBlocking() {
			super(100, 2, 10);
		}
	}



	private class TestConcurrent_Close extends TestCase.Unit {
		Pipe<String> $pipe = new DataPipe<String>();
		ConcurrentCounter<String> $counter = ConcurrentCounter.make(Arr.asList(TD.s1));
		final int n = 10000;
		final int n2 = 100;

		public void call() {
			Runnable[] $tasks = new Runnable[4];
			$tasks[0] = new Writer(TD.s1);	// puts 2n+n2
			$tasks[1] = new Reader();	// consumes up to n
			$tasks[2] = new Reader();	// consumes up to n
			$tasks[3] = new FinalReader();	// consumes some arbitrary amount based on thread scheduling, minimum n2.
			ThreadUtil.doAll($tasks);
			assertEquals(2*n+n2, $counter.getCount(TD.s1));
		}


		private class Writer implements Runnable {
			public Writer(String $str) { this.$str = $str; }
			private String $str;
			public void run() {
				for (int $i = 0; $i < (2*n)+n2; $i++)
					$pipe.sink().write($str);
				$pipe.sink().close();
			}
		}
		private class Reader implements Runnable {
			public Reader() {}
			public void run() {
				for (int $i = 0; $i < n; $i++)
					$counter.hear($pipe.source().read());
			}
		}
		private class FinalReader implements Runnable {
			public FinalReader() {}
			public void run() {
				try {
					for (String $s : $pipe.source().readAll())
						$counter.hear($s);
				} catch (InterruptedException $e) {
					breakUnit($e);
				}
			}
		}
	}

	// if a pipe is fed, closed, and then drained, we should see exactly n+2 events (one for the closure, one for the final drain, and one for each of the (unbatched) writes)... even if there is more than one person trying to get that last read.
	//  jk, that's all impossible because pipes can't be arsed to check that that final drain event is a once-only.
}

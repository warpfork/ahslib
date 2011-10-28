/*
 * Copyright 2010, 2011 Eric Myhre <http://exultant.us>
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

import java.util.*;
import java.util.concurrent.locks.*;

import us.exultant.ahs.test.junit.*;
import us.exultant.ahs.util.*;

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

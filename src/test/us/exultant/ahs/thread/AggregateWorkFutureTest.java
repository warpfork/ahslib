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

import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;
import us.exultant.ahs.log.*;
import us.exultant.ahs.test.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * <p>
 * Tests {@link AggregateWorkFuture}.
 * </p>
 * 
 * <p>
 * {@code DEPENDS: }
 * <ul>
 * <li>{@link WorkSchedulerTest} &mdash; I actually use
 * {@link WorkManager#getDefaultScheduler()}, so things <b>really</b> need to be stable.
 * <li>{@link FuturePipeTest} 
 * </ul>
 * </p>
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * 
 */
public class AggregateWorkFutureTest extends TestCase {
	public static void main(String... $args) {					new AggregateWorkFutureTest().run();		}
	public AggregateWorkFutureTest() {						super(new Logger(Logger.LEVEL_TRACE), true);	}
	public AggregateWorkFutureTest(Logger $log, boolean $enableConfirmation) {	super($log, $enableConfirmation);		}
	
	public List<Unit> getUnits() {
		List<Unit> $tests = new ArrayList<Unit>();
		$tests.add(new TestBasic());
		$tests.add(new TestBasic());
		$tests.add(new TestBasic());
		$tests.add(new TestUpdate());
		$tests.add(new TestCancellation());
		$tests.add(new TestManyLongTasks());
		return $tests;
	}
	
	
	
	private static class StickableWorkTarget extends WorkTarget.TriggerableAdapter<Void> {
		public StickableWorkTarget(CountDownLatch $latch, int $priority) {
			super(false, true, $priority);
			this.$latch = $latch;
		}
		private final CountDownLatch $latch;
		public Void run() throws InterruptedException {
			if ($latch != null) $latch.await();
			return null;
		}
	}
	
	
	
	/**
	 * Test just one piece of work being put into the aggregate, starting without the
	 * aggregate finishing, and finishing leaving the aggregate finished and the
	 * completion listener of the aggregate being fired.
	 */
	private class TestBasic extends TestCase.Unit {
		private WorkScheduler $ws = new WorkSchedulerFlexiblePriority(8);
		private final CountDownLatch $success = new CountDownLatch(1);
		public Object call() throws TimeoutException, CancellationException, ExecutionException, InterruptedException {
			CountDownLatch $latch = new CountDownLatch(1);
			StickableWorkTarget $wt = new StickableWorkTarget($latch, 0);
			WorkFuture<Void> $wf = $ws.schedule($wt, ScheduleParams.NOW);
			
			Collection<WorkFuture<Void>> $wfc = new ArrayList<WorkFuture<Void>>();
			$wfc.add($wf);
			AggregateWorkFuture<Void> $awf = new AggregateWorkFuture<Void>($wfc);
			$awf.addCompletionListener(new Listener<WorkFuture<?>>() {
				public void hear(WorkFuture<?> $wf) {
					$success.countDown();
				}
			});
			
			// shouldn't be done before the scheduler even starts, obviously
			assertFalse($awf.isDone());
			assertEquals(1L,$success.getCount());
			
			// merely starting the scheduler shouldn't cause doneness
			$ws.start();
			X.chill(2);
			assertFalse($awf.isDone());
			assertEquals(1,$success.getCount());
			
			// triggering the task and getting it scheduled shouldn't cause doneness
			$wt.trigger();
			$wf.update();
			X.chill(2);
			assertFalse($awf.isDone());
			assertEquals(1,$success.getCount());
			
			// okay, now we let the task return... this should cause prompt doneness.
			$latch.countDown();
			$wf.get(2, TimeUnit.MILLISECONDS);
			$awf.get();
			assertTrue($awf.isDone());
			
			// and the completion listner should also have been called.  it can be momentarily after isDone returns true, though.
			$success.await(1, TimeUnit.MILLISECONDS);
			
			$ws.stop(false);
			return null;
		}
	}
	
	
	
	private abstract class TestTemplate extends TestCase.Unit {
		@SuppressWarnings("unchecked")
		public TestTemplate(int $tasks) {
			this.$tasks = $tasks;
			$wts = Arr.newInstance(StickableWorkTarget.class, $tasks);
			$wfs = Arr.newInstance(WorkFuture.class, $tasks);
		}
		protected final int $tasks;
		protected WorkScheduler $ws = new WorkSchedulerFlexiblePriority(8).start();
		protected CountDownLatch $latch = new CountDownLatch(1);
		protected StickableWorkTarget[] $wts;
		protected WorkFuture<Void>[] $wfs;
	}
	
	
	
	/**
	 * Test just two pieces of work being put into the aggregate, being updated
	 * correctly in one move via the aggregate, and finishing.
	 */
	private class TestUpdate extends TestTemplate {
		public TestUpdate() { super(2); }
		public Object call() throws TimeoutException, CancellationException, InterruptedException {
			for (int $i = 0; $i < $tasks; $i++)
				$wts[$i] = new StickableWorkTarget(null, 0);
			for (int $i = 0; $i < $tasks; $i++)
				$wfs[$i] = $ws.schedule($wts[$i], ScheduleParams.NOW);
			for (int $i = 0; $i < $tasks; $i++)
				$wts[$i].trigger();
			AggregateWorkFuture<Void> $awf = new AggregateWorkFuture<Void>(Arr.asList($wfs));
			
			// nothing should be able to finish because they weren't ready when we scheduled them.
			X.chill(2);
			assertFalse($wfs[0].isDone());
			assertFalse($wfs[1].isDone());
			
			// and now things should finish promptly.  both of them.
			$awf.update();
			$awf.get(2, TimeUnit.MILLISECONDS);
			assertTrue($awf.isDone());
			assertTrue($wfs[0].isDone());
			assertTrue($wfs[1].isDone());
			
			$ws.stop(false);
			return null;
		}
	}
	
	
	
	/**
	 * Like {@link TestBasic} but with more tasks, half of them finishing immeidately
	 * and the other half being long lived.
	 */
	private class TestManyLongTasks extends TestTemplate {
		private final CountDownLatch $success = new CountDownLatch(1);
		public TestManyLongTasks() { super(10); }
		public Object call() throws TimeoutException, CancellationException, InterruptedException {
			for (int $i = 0; $i < $tasks; $i+=2)
				$wts[$i] = new StickableWorkTarget(null, 0);
			for (int $i = 1; $i < $tasks; $i+=2)
				$wts[$i] = new StickableWorkTarget($latch, 0);
			for (int $i = 0; $i < $tasks; $i++)
				$wts[$i].trigger();
			for (int $i = 0; $i < $tasks; $i++)
				$wfs[$i] = $ws.schedule($wts[$i], ScheduleParams.NOW);
			
			AggregateWorkFuture<Void> $awf = new AggregateWorkFuture<Void>(Arr.asList($wfs));
			$awf.addCompletionListener(new Listener<WorkFuture<?>>() {
				public void hear(WorkFuture<?> $wf) {
					$success.countDown();
				}
			});
			
			// half of the tasks being done shouldn't cause doneness 
			X.chill(2);
			assertFalse($awf.isDone());
			assertEquals(1,$success.getCount());
			
			// letting the other half finish should cause doneness
			$latch.countDown();
			$awf.get(2, TimeUnit.MILLISECONDS);
			assertTrue($awf.isDone());
			
			// and the completion listner should also have been called.  it can be momentarily after isDone returns true, though.
			$success.await(1, TimeUnit.MILLISECONDS);
			
			$ws.stop(false);
			return null;
		}
	}
	
	
	
	/**
	 * Like {@link TestManyLongTasks}, except those tasks that are long lived are
	 * cancelled before they are allowed to return normally.
	 */
	private class TestCancellation extends TestTemplate {
		private final CountDownLatch $success = new CountDownLatch(1);
		public TestCancellation() { super(10); }
		public Object call() throws TimeoutException, CancellationException, InterruptedException {
			for (int $i = 0; $i < $tasks; $i+=2)
				$wts[$i] = new StickableWorkTarget(null, 0);
			for (int $i = 1; $i < $tasks; $i+=2)
				$wts[$i] = new StickableWorkTarget($latch, 0);
			for (int $i = 0; $i < $tasks; $i++)
				$wts[$i].trigger();
			for (int $i = 0; $i < $tasks; $i++)
				$wfs[$i] = $ws.schedule($wts[$i], ScheduleParams.NOW);
			
			AggregateWorkFuture<Void> $awf = new AggregateWorkFuture<Void>(Arr.asList($wfs));
			$awf.addCompletionListener(new Listener<WorkFuture<?>>() {
				public void hear(WorkFuture<?> $wf) {
					$success.countDown();
				}
			});
			
			// half of the tasks being done shouldn't cause doneness 
			X.chill(2);
			assertFalse($awf.isDone());
			assertEquals(1,$success.getCount());
			
			// then cancel the rest...
			//  that strong/interrupting cancel should make them return now even though they were blocking on a latch we haven't released yet.
			$awf.cancel(true);
			
			// since the last thing to complete is due to cancellation, there's no doubt that the aggregate should feel cancelled as well.
			//  and it should be able to return immediately since the cancel method doesn't return until done.
			assertEquals(WorkFuture.State.CANCELLED, $awf.getState());
			try {
				$awf.get(0, TimeUnit.MILLISECONDS);
				throw new TestCase.AssertionFailed("this task should throw a CancellationException!");
			} catch (CancellationException $e) {
				/* good! */
			}	// TODO!!!! must test that cancellation in one thread causes return from one already blocking on get!
			assertTrue($awf.isDone());

			// and the completion listner should also have been called.  it can be momentarily after isDone returns true, though.
			$success.await(1, TimeUnit.MILLISECONDS);
			
			// the ones who were finished already when the cancel came in should look fine
			for (int $i = 0; $i < $tasks; $i+=2)
				assertEquals(WorkFuture.State.FINISHED, $wfs[$i].getState());
			
			// the ones who were cancelled before they finished should look cancelled
			for (int $i = 0; $i < $tasks; $i+=2)
				assertEquals(WorkFuture.State.CANCELLED, $wfs[$i].getState());
			
			$ws.stop(false);
			return null;
		}
	}
	
	
	
	/**
	 * 
	 */
	private class TestTest extends TestCase.Unit {
		private WorkScheduler $ws = new WorkSchedulerFlexiblePriority(8);
		public Object call() {
			$ws.start();
			
			$ws.stop(false);
			return null;
		}
	}
}

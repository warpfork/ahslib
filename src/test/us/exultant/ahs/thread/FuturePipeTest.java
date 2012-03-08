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
 * Tests {@link FuturePipe}.
 * </p>
 * 
 * <p>
 * {@code DEPENDS: }
 * <ul>
 * <li>{@link WorkSchedulerTest} &mdash; I actually use
 * {@link WorkManager#getDefaultScheduler()}, so things <b>really</b> need to be stable.
 * </ul>
 * </p>
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * 
 */
public class FuturePipeTest extends TestCase {
	public static void main(String... $args) {				new FuturePipeTest().run();			}
	public FuturePipeTest() {						super(new Logger(Logger.LEVEL_TRACE), true);	}
	public FuturePipeTest(Logger $log, boolean $enableConfirmation) {	super($log, $enableConfirmation);		}
	
	public List<Unit> getUnits() {
		List<Unit> $tests = new ArrayList<Unit>();
		$tests.add(new TestBasic());
		$tests.add(new TestConcurrent());
		//$tests.add(new TestOrdering());
		return $tests;
	}
	
	
	
	public static final WorkTarget.TriggerableAdapter<Void> makeNoopWork(boolean $alreadyReady) {
		return new WorkTarget.RunnableWrapper(new Runnable() { public void run() {} }, $alreadyReady, true);
	}
	
	
	
	/**
	 * One WorkFuture is added a FuturePipe, and the Pipe closed. The work is then
	 * made ready, and after running becomes finished.
	 */
	private class TestBasic extends TestCase.Unit {
		private WorkScheduler $ws = new WorkSchedulerFlexiblePriority(8);
		public Object call() {
			Flow<WorkFuture<Void>> $wfp = new FuturePipe<Void>();
			$ws.start();
			
			WorkTarget.TriggerableAdapter<Void> $wt = makeNoopWork(false);
			WorkFuture<Void> $wf = $ws.schedule($wt, ScheduleParams.NOW);
			$wfp.sink().write($wf);
			$wfp.sink().close();
			assertFalse("FuturePipe is has no readable elements at start", $wfp.source().hasNext());
			assertTrue("FuturePipe became closed for writing when asked", $wfp.sink().isClosed());
			assertFalse("FuturePipe is still open for reading", $wfp.source().isClosed());
			breakIfFailed();
			
			$wt.trigger();
			$wf.update();
			
			$wfp.source().setListener(new Listener<ReadHead<WorkFuture<Void>>>() {
				/*
				 * This will actually be called up to FOUR times.
				 * - once "spuriously" when the listener is assigned
				 * - once for the actual completion event being made available
				 * - once because the pipe is closed
				 * - once because that is the last completion event on the closed future pipe, so the read is the exhaustive read.
				 * These can come in in pretty much any order at all.
				 * Also, that last one will occur once per read that is on a now-exhausted pipe... which means it's going to happen once for our read, and once for the readall we do after it.
				 */
				public void hear(ReadHead<WorkFuture<Void>> $x) {
					$log.trace(this, "event"
							//, new Exception()
					);
				}
			});
			
			$wfp.source().read();
			assertTrue("WorkFuture read from FuturePipe was done", $wf.isDone());
			breakIfFailed();
			assertEquals("No unexpected additional items read from FuturePipe", 0, $wfp.source().readAll().size());
			assertFalse("FuturePipe is empty when expected", $wfp.source().hasNext());
			assertTrue("FuturePipe became closed for reading when empty", $wfp.source().isClosed());
			$ws.stop(false);
			return null;
		}
	}
	
	
	
	/**
	 * Several WorkFuture are added to a FuturePipe (some of which go off quite
	 * immediately, some of which go off with delays), and the Pipe closed at some
	 * point where there will probably be work finishing on either side of the closure.
	 */
	private class TestConcurrent extends TestCase.Unit {
		private WorkScheduler $ws = new WorkSchedulerFlexiblePriority(8);
		static final int N = 1000;
		static final int D0 = 700;
		static final int D1 = N - D0;
		public Object call() {
			Flow<WorkFuture<Void>> $wfp = new FuturePipe<Void>();
			
			@SuppressWarnings("unchecked")
			WorkTarget<Void>[] $wts = Arr.newInstance(WorkTarget.class, N);
			@SuppressWarnings("unchecked")
			WorkFuture<Void>[] $wfs = Arr.newInstance(WorkFuture.class, N);
			
			for (int $i = 0; $i < N; $i++)
				$wts[$i] = makeNoopWork(true);
			for (int $i = 0; $i < D0; $i++)
				$wfs[$i] = $ws.schedule($wts[$i], ScheduleParams.NOW);
			for (int $i = D0; $i < N; $i++)
				$wfs[$i] = $ws.schedule($wts[$i], ScheduleParams.makeDelayed(1));
			for (int $i = 0; $i < N; $i++)
				$wfp.sink().write($wfs[$i]);
			assertFalse("FuturePipe is has no readable elements at start", $wfp.source().hasNext());
			assertFalse("FuturePipe is still open for writing", $wfp.sink().isClosed());
			assertFalse("FuturePipe is still open for reading", $wfp.source().isClosed());
			breakIfFailed();
			
			$ws.start();
			$wfp.sink().close();
			assertTrue("FuturePipe became closed for writing when asked", $wfp.sink().isClosed());
			breakIfFailed();
			
			for (int $i = 0; $i < N; $i++) {
				WorkFuture<Void> $wf = $wfp.source().read();
				assertTrue("WorkFuture read from FuturePipe was done", $wf.isDone());
			}
			assertEquals("No unexpected additional items read from FuturePipe",0, $wfp.source().readAll().size());
			assertFalse("FuturePipe is empty when expected", $wfp.source().hasNext());
			assertTrue("FuturePipe became closed for reading when empty", $wfp.source().isClosed());
			$ws.stop(false);
			return null;
		}
	}
	
	
	
	/**
	 * Several WorkFuture are added to a FuturePipe (with different priorities). They
	 * must come out of the FuturePipe in the order which they were completed
	 * (assuming that the scheduler completed them as their priority ordering
	 * dictates; the scheduler is not started until after all work is added to
	 * facilitate clarity in that).
	 */
	private class TestOrdering extends TestCase.Unit {
		private WorkScheduler $ws = new WorkSchedulerFlexiblePriority(8);
		public Object call() {
			return null;
		}
	}
	
	
}

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
 * @author hash
 * 
 */
public class FuturePipeTest extends TestCase {
	public static void main(String... $args) {				new FuturePipeTest().run();			}
	public FuturePipeTest() {						super(new Logger(Logger.LEVEL_TRACE), true);	}
	public FuturePipeTest(Logger $log, boolean $enableConfirmation) {	super($log, $enableConfirmation);		}
	
	public List<Unit> getUnits() {
		List<Unit> $tests = new ArrayList<Unit>();
		$tests.add(new TestBasic());
		$tests.add(new TestBasicConcurrent());
		return $tests;
	}
	
	
	
	public static final WorkTarget<Void> makeNoopWork() {
		return new WorkTarget.RunnableWrapper(new Runnable() { public void run() {} });
	}
	
	/** One WorkFuture is added a FuturePipe, and the Pipe closed. The work is scheduled with a delay to take place after the pipe closure. */
	private class TestBasic extends TestCase.Unit {
		public Object call() throws InterruptedException, ExecutionException {
			Flow<WorkFuture<Void>> $wfp = new FuturePipe<Void>();	// this type boundary here is fantastically powerful abstraction and very important.
			
			WorkFuture<Void> $wf = WorkManager.getDefaultScheduler().schedule(makeNoopWork(), ScheduleParams.makeDelayed(3));
			$wfp.sink().write($wf);
			$wfp.sink().close();
			assertFalse($wfp.source().hasNext());
			assertTrue($wfp.sink().isClosed());
			assertFalse($wfp.source().isClosed());
			
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
					$log.trace(this, "event", new Exception());
				}
			});
			
			$wfp.source().read();
			assertTrue($wf.isDone());
			assertEquals(0, $wfp.source().readAll().size());
			assertTrue($wfp.source().isClosed());	// actually yeah, this is allowed to come in AFTER the last read.  which is a little surprising.  but on the other hand, wouldn't it be even more surprising if you got close events BEFORE the last read?  either way, the point is moot: from a technical level, i CANT emit the closure before the unblocking of the final read without an outrageous amount of effort, since if i recycle the standard pipe abstraction the unblocking is done by writing, which obviously you aren't allowed to do something closed because of how that makes zero sense everywhere else.
			assertFalse($wfp.source().hasNext());
			return null;
		}
	}
	
	/** Same as {@link TestBasic}, but the work is scheduled without delay so it can (and probably will) take place before the WorkFuture is written into the FuturePipe, or at approximately the same time. */
	private class TestBasicConcurrent extends TestCase.Unit {
		private boolean	$win	= false;
		
		public Object call() throws InterruptedException, ExecutionException {
			return null;
		}
	}
	
	
}

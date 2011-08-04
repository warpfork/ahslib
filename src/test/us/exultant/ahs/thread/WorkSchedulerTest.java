package us.exultant.ahs.thread;

import us.exultant.ahs.util.*;
import us.exultant.ahs.log.*;
import us.exultant.ahs.test.*;
import java.util.*;

public abstract class WorkSchedulerTest extends TestCase {
	public WorkSchedulerTest() {
		super(new Logger(Logger.LEVEL_DEBUG), true);
	}
	
	public WorkSchedulerTest(Logger $log, boolean $enableConfirmation) {
		super($log, $enableConfirmation);
	}
	
	public List<Unit> getUnits() {
		List<Unit> $tests = new ArrayList<Unit>();
		$tests.add(new TestRunOnce());
		$tests.add(new TestWtAlwaysReady());
		$tests.add(new TestNbWt());
		return $tests;
	}
	
	private static final TestData	TD	= TestData.getFreshTestData();
	
	protected abstract WorkScheduler makeScheduler();
	
	/** Helper method &mdash I want a message straight to stdout every time I throw a major exception, because there's a dangerous tendancy for Runnable to eat those if there's a mistake somewhere. */
	protected void blow(String $msg) {
		X.saye("BLOW: "+$msg);
		throw new AssertionFailed($msg);
	}
	
	
	/** One runnable wrapped to be a one-shot WorkTarget. */
	private class TestRunOnce extends TestCase.Unit {
		public Object call() {
			WorkScheduler $ws = makeScheduler();
			Work $w = new Work();
			$ws.schedule(new WorkTarget.RunnableWrapper($w, 0, true));
			X.chill(200);
			assertEquals(999, $w.x);
			return null;
		}
		private class Work implements Runnable {
			public int x = 1000;
			public void run() {
				x--;
			}
		}
	}
	
	
	
	/** Eight work targets, all always ready until they're done. */
	private class TestWtAlwaysReady extends TestCase.Unit {
		public Object call() {
			WorkScheduler $ws = makeScheduler();
			Work[] $wt = new Work[8];
			for (int $i = 0; $i < 8; $i++) $wt[$i] = new Work();
			for (int $i = 0; $i < 8; $i++) $ws.schedule($wt[$i]);
			X.chill(400);
			for (int $i = 0; $i < 8; $i++) assertEquals(0, $wt[$i].x);
			return null;
		}
		private class Work implements WorkTarget {
			public int x = 1000;
			public synchronized void run() {
				x--;
			}
			public synchronized boolean isReady() {
				return !isDone();
			}
			public synchronized boolean isDone() {
				return (x <= 0);
			}
			public int getPriority() {
				return 0;
			}
		}
	}
	
	
	
	/** Test two work targets, once of which must always follow the other (in other words, one is always ready, but the other changes readiness based on the progress of the first). */
	private class TestNbWt extends TestCase.Unit {
		WorkScheduler $ws = makeScheduler();
		
		public Object call() {
			WorkLeader $w1 = new WorkLeader();
			WorkFollower $w2 = new WorkFollower();
			$w1.$follower = $w2;
			$w2.$leader = $w1;
			$ws.schedule($w2);
			$ws.schedule($w1);
			X.chill(200);
			assertEquals(100, $w2.x);
			assertEquals(100, $w1.x);
			return null;
		}
		private class WorkLeader implements WorkTarget {
			public volatile WorkFollower $follower;
			public volatile int x = 1000;
			public synchronized void run() {
				x--;
				$ws.update($follower);
			}
			public synchronized boolean isReady() {
				return !isDone();
			}
			public synchronized boolean isDone() {
				return (x <= 100);
			}
			public int getPriority() {
				return 0;
			}
		}
		private class WorkFollower implements WorkTarget {
			public volatile WorkLeader $leader;
			public volatile int x = 1000;
			public synchronized void run() {
				if (!isReady()) blow("");	// not normal semantics for ready, obviously, but true for this test, since it should only be possible to flip to unready by running and one should never be scheduled for multiple runs without a check of readiness having already occurred between each run.
				x--;
			}
			public synchronized boolean isReady() {
				return (x > $leader.x);
			}
			public synchronized boolean isDone() {
				return (x <= 0);
			}
			public int getPriority() {
				return 0;
			}
		}
	}
	
	
	
	/**  */
	private class TestBasic extends TestCase.Unit {
		public Object call() {
			breakIfFailed();
			return null;
		}
	}
}

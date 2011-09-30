package us.exultant.ahs.thread;

import us.exultant.ahs.log.*;

public class WorkSchedulerFlexiblePriorityTest extends WorkSchedulerTest {
	public static void main(String... $args) {
		try {
			new WorkSchedulerFlexiblePriorityTest().run();
		} catch (Throwable $e) {	// this seems a bit daft, no?  but otherwise my eclipse console is missing some kinds of class cast exception, so... welp.
			$e.printStackTrace();
		}
	}
	
	public WorkSchedulerFlexiblePriorityTest() {
		super();
	}
	
	public WorkSchedulerFlexiblePriorityTest(Logger $log, boolean $enableConfirmation) {
		super($log, $enableConfirmation);
	}
	
	protected WorkScheduler makeScheduler() {
		return new WorkSchedulerFlexiblePriority(4);
	}
}

package us.exultant.ahs.thread;

import us.exultant.ahs.log.*;

public class WorkSchedulerFlexiblePriorityTest extends WorkSchedulerTest {
	public static void main(String... $args) {
		new WorkSchedulerFlexiblePriorityTest().run();
	}
	
	public WorkSchedulerFlexiblePriorityTest() {
		super(new Logger(Logger.LEVEL_DEBUG), true);
	}
	
	public WorkSchedulerFlexiblePriorityTest(Logger $log, boolean $enableConfirmation) {
		super($log, $enableConfirmation);
	}
	
	protected WorkScheduler makeScheduler() {
		return new WorkSchedulerFlexiblePriority(4);
	}
}

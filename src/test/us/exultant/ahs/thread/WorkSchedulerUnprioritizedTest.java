package us.exultant.ahs.thread;

import us.exultant.ahs.log.*;

public class WorkSchedulerUnprioritizedTest extends WorkSchedulerTest {
	public static void main(String... $args) {
		new WorkSchedulerUnprioritizedTest().run();
	}
	
	public WorkSchedulerUnprioritizedTest() {
		super(new Logger(Logger.LEVEL_DEBUG), true);
	}
	
	public WorkSchedulerUnprioritizedTest(Logger $log, boolean $enableConfirmation) {
		super($log, $enableConfirmation);
	}
	
	protected WorkScheduler makeScheduler() {
		return new WorkSchedulerUnprioritized();
	}
}

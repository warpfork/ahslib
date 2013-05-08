package us.exultant.ahs.thread;

import us.exultant.ahs.util.*;
import java.util.concurrent.*;
import org.slf4j.*;

public class WorkFailureLoggerDemo {
	public static void main(String... $args) throws ExecutionException, InterruptedException {
		LOG.info("starting!");
		WorkFuture<Void> $wf = WorkManager.getDefaultScheduler().schedule(
				new WorkTargetWrapperCallable<Void>(new Callable<Void>() {
					public Void call() throws Exception {
						throw new NullPointerException(
								"this isn't a real null pointer problem.  "+
								"but you should see it logged as an error!  "+
								"Twice, actually.  Once from the failure logger that's mounted standard on the default scheduler...  "+
								"and once when the main thread does a blocking get() on this work, and then blows up."
						);
					}
				}),
				ScheduleParams.NOW
		);
		LOG.info("scheduling the doomed task on the defaul scheduler is complete");
		try {
			$wf.get();
		} catch (Throwable $t) {
			LOG.info("this is the exception thrown from the blocking get()'ing:", $t);
		}
		LOG.info("chilling out for a moment... completion listeners fire *after* get() flips, so we don't want to exit before those have a chance");
		X.chill(150);
	}
	private static final Logger LOG = LoggerFactory.getLogger(WorkFailureLoggerDemo.class);
}

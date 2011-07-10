package us.exultant.ahs.thread;

import us.exultant.ahs.util.*;

/**
 * Handy convenience methods for doing dead-simple stuff with threads. In this case, mind,
 * convenience does not mix with efficiency or good design &mdash; these methods are
 * intended only for rapid prototyping or testing purposes.
 * 
 * @author hash
 * 
 */
public abstract class ThreadUtil {
	/**
	 * Attempts to instantiate a number of a runnable type (via a public nullary
	 * constructor found by reflection), then wraps these in threads, starts all the
	 * threads concurrently, then waits for the threads to complete before returning.
	 * 
	 * @param $type
	 * @param $count
	 * @throws MajorBug
	 *                 if you're a dolt and ignore any of the statements about the
	 *                 need for public nullary constructors and so forth.
	 */
	public static void doAll(Class<? extends Runnable> $type, int $count) {
		Runnable[] $tasks = new Runnable[$count];
		try {
			for (int $i = 0; $i < $count; $i++)
				$tasks[$i] = $type.newInstance();
		} catch (Throwable $e) {
			throw new MajorBug($e);
		}
		doAll($tasks);
	}
	
	/**
	 * Starts all the threads concurrently and then waits for them to complete before
	 * returning.
	 * 
	 * @param $threads
	 */
	public static void doAll(Thread... $threads) {
		startAll($threads);
		joinAll($threads);
	}
	
	/**
	 * Starts all of the Runnable tasks concurrently each in their own new thread and
	 * then waits for them to complete before returning.
	 * 
	 * @param $tasks
	 */
	public static void doAll(Runnable... $tasks) {
		doAll(wrapAll($tasks));
	}
	
	/**
	 * Produces a thread wrapped around each of the given Runnable.
	 * 
	 * @param $tasks
	 * @return an array of new (unstarted) Thread of the same magnitude as the given
	 *         Runnable array.
	 */
	public static Thread[] wrapAll(Runnable... $tasks) {
		Thread[] $threads = new Thread[$tasks.length];
		for (int $i = 0; $i < $tasks.length; $i++)
			$threads[$i] = new Thread($tasks[$i]);
		return $threads;
	}
	
	/**
	 * Starts all the given threads concurrently.
	 * 
	 * @param $threads
	 */
	public static void startAll(Thread... $threads) {
		for (int $i = 0; $i < $threads.length; $i++)
			$threads[$i].start();
	}
	
	/**
	 * Waits for all the given threads to complete before returning.
	 * 
	 * @param $threads
	 */
	public static void joinAll(Thread... $threads) {
		for (int $i = 0; $i < $threads.length; $i++)
			try {
				$threads[$i].join();
			} catch (InterruptedException $e) {
				$e.printStackTrace();
			}
	}
}

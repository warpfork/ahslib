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

import us.exultant.ahs.util.*;
import java.util.*;

/**
 * Handy convenience methods for doing dead-simple stuff with threads. In this case, mind,
 * convenience does not mix with efficiency or good design &mdash; these methods are
 * intended only for rapid prototyping or testing purposes.
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
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
	 *                 need for public nullary constructors and so forth, or if
	 *                 non-static nested classes get in the way.
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
	 * Attempts to instantiate a number of several runnable types (via the public
	 * nullary constructors found by reflection), then wraps these in threads, starts
	 * all the threads concurrently, then waits for the threads to complete before
	 * returning.
	 * 
	 * @param $types
	 * @param $count
	 *                how many of each type to instantiate (so the total number of
	 *                threads will become <tt>$count * $types.length</tt>).
	 * @throws MajorBug
	 *                 if you're a dolt and ignore any of the statements about the
	 *                 need for public nullary constructors and so forth, or if
	 *                 non-static nested classes get in the way.
	 */
	public static void doAll(List<Class<? extends Runnable>> $types, int $count) {
		$count = $count*$types.size();
		Runnable[] $tasks = new Runnable[$count];
		try {
			for (int $i = 0; $i < $count;)
				for (int $j = 0; $j < $types.size(); $j++, $i++)
					$tasks[$i] = $types.get($j).newInstance();
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
	 * @return the time taken in ms
	 */
	public static long doAll(Thread... $threads) {
		long $start = X.time();
		startAll($threads);
		joinAll($threads);
		return (X.time() - $start);
	}
	
	/**
	 * Starts all of the Runnable tasks concurrently each in their own new thread and
	 * then waits for them to complete before returning.
	 * 
	 * @param $tasks
	 * @return the time taken in ms
	 */
	public static long doAll(Runnable... $tasks) {
		return doAll(wrapAll($tasks));
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
	 * Produces a number of threads, each wrapped the given Runnable.
	 * 
	 * @param $task
	 * @param $number
	 *                how many threads to produce
	 * @return an array of new (unstarted) Thread of the same magnitude as the given
	 *         Runnable array.
	 */
	public static Thread[] wrapAll(Runnable $task, int $number) {
		Thread[] $threads = new Thread[$number];
		for (int $i = 0; $i < $number; $i++) {
			$threads[$i] = new Thread($task);
			$threads[$i].setDaemon(true);
		}
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

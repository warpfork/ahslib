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

import java.util.concurrent.locks.*;

/**
 * <p>
 * Allows user interrupt of all acquires; ignores actual thread-based interrupts. Once
 * invoked, the interrupted state is permanent: all future as well as all present blocking
 * acquires will return InterruptedException immediately (but all nonblocking acquires
 * (i.e. {@link #tryAcquire()} still function normally).
 * </p>
 * 
 * <p>
 * An example of a potential use for this kind of functionality in a semaphore is when the
 * semaphore is to be used in as a gateway controlling access to some sort of data stream
 * or work flow which can be terminated -- after the stream being gated has been
 * terminated, it would be inappropriate to ever again block on a read, overriding the
 * normal reasoning that a semaphore with zero permits must block.
 * </p>
 * 
 * <p>
 * This implementation is not sufficiently flexible to offer all of the options of the
 * standard java Semaphore (such as timeouts).
 * </p>
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * @deprecated {@link FlippableSemaphore} is better, faster, and more flexible than this
 *             for all purposes.
 */
@Deprecated
public class InterruptableSemaphore {
	/**
	 * Fairness defaults to false.
	 * 
	 * @param $permits
	 */
	public InterruptableSemaphore(int $permits) {
		this($permits, false);
	}
	public InterruptableSemaphore(int $permits, boolean $fair) {
		// i'm not using the lock here because frankly if you start doing multithreaded things before the constructor returns you're just psycho.
		$lock = new ReentrantLock($fair);
		$permitsAvailable = $lock.newCondition();
		$interrupted = false;
		this.$permits = $permits;
	}
	
	private final Lock	$lock;
	private final Condition	$permitsAvailable;
	private int		$permits;
	private boolean		$interrupted;
	
	public void acquire() throws InterruptedException {	// i'd be happier if this returned a boolean for success and reserved the throw for a real interrupt
		$lock.lock();
		try {
			while ($permits < 1) {
				if ($interrupted) throw new InterruptedException();
				try {
					$permitsAvailable.await();
				} catch (InterruptedException $e) { /* just loop normally -- we'll either ignore completely or notice it was intentional at the beginning of the next loop. */ }
			}
			if ($interrupted) throw new InterruptedException();
			$permits--;
		} finally {
			$lock.unlock();
		}
	}
	
	public boolean tryAcquire() {
		$lock.lock();
		try {
			if ($permits > 0) {
				$permits--;
				return true;
			}
			return false;
		} finally {
			$lock.unlock();
		}
	}
	
	public int drainPermits() {
		$lock.lock();
		try {
			int $v = $permits;
			$permits = 0;
			return $v;
		} finally {
			$lock.unlock();
		}
	}
	
	public int availablePermits() {
		$lock.lock();
		try {
			return $permits;
		} finally {
			$lock.unlock();
		}
	}
	
	public void release() {
		$lock.lock();
		try {
			$permits++;
			$permitsAvailable.signal();
		} finally {
			$lock.unlock();
		}
	}
	
	public void release(int $p) {
		$lock.lock();
		try {
			$permits += $p;
			$permitsAvailable.signal();
		} finally {
			$lock.unlock();
		}
	}
	
	public void interrupt() {
		$lock.lock();
		try {
			$interrupted = true;
			$permitsAvailable.signalAll();
		} finally {
			$lock.unlock();
		}
	}
}

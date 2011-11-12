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
/*
 * This code is inspired by and borrows heavily from code originally written
 * by Doug Lea with assistance from members of JCP JSR-166 Expert Group and
 * released to the public domain.  The author of this code greatfully 
 * acknowledges their contributions to the field.
 */

package us.exultant.ahs.thread;

import java.util.concurrent.*;

public class ClosableSemaphore extends FlippableSemaphore {
	public boolean isClosed() {
		return isFlipped();
	}
	
	public void flip(boolean $no) {
		throw new UnsupportedOperationException();
	}
	
	public void close() {
		super.flip(true);
	}
	
	public void acquire() throws InterruptedException {
		super.acquire();
		//CORESTRAT:
		//    - make tryAcquireShared(int) actually return a positive when we're closed, but without actually draining shit.
		//            implication: must deal with that in every other method too, because we don't want to look like we actually got an acquire if we didn't.
		//             ... I'm pretty sure this is actually impossible to do with AQS.  if doAcquireSharedInterruptibly(int) would return the int from tryAcquireShared(int), i could use that to see if an acquire was fake-successful/return-by-cancel, but without that i see no real options.
		//                 even if we could do that, then in this whole situation, i don't think we can really reuse FlippableSemaphore, because it has no idea that there should be different kinds of acquire success, and it certainly doesn't know what it has to do in tryAcquireShared when flipped that's special.  we could overide tryAcquireShared, actually, but only if those classes of the implementation weren't final and we had a rational way to override their construction in the superclass.
		//    - then somehow magically get the list of blocked threads
		//            on the plus side, you'll note that once we're closed we CAN stop new threads from starting to block, so we're safe there.  so maybe this is doable.
		//    - call LockSupport.unpark with those threads.
		
	}
	
	public void acquireUninterruptibly() {
		super.acquireUninterruptibly();
	}
	
	public boolean tryAcquire(long $timeout, TimeUnit $unit) throws InterruptedException {
		return super.tryAcquire($timeout, $unit);
	}
	
	public void acquire(int $permits) throws InterruptedException {
		super.acquire($permits);
	}
	
	public void acquireUninterruptibly(int $permits) {
		super.acquireUninterruptibly($permits);
	}
	
	public boolean tryAcquire(int $permits, long $timeout, TimeUnit $unit) throws InterruptedException {
		return super.tryAcquire($permits, $timeout, $unit);
	}
	
	public String toString() {
		return super.toString() + "[Permits=" + availablePermits() + ";Closed="+isClosed()+"]";
	}
}

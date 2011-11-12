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
		//CORESTRAT CAREFUL WAKEUPS AND CROSSCUTTING:
		//    - make tryAcquireShared(int) actually return a positive when we're closed, but without actually draining shit.
		//            implication: must deal with that in every other method too, because we don't want to look like we actually got an acquire if we didn't.
		//             ... I'm pretty sure this is actually impossible to do with AQS.  if doAcquireSharedInterruptibly(int) would return the int from tryAcquireShared(int), i could use that to see if an acquire was fake-successful/return-by-cancel, but without that i see no real options.
		//                 even if we could do that, then in this whole situation, i don't think we can really reuse FlippableSemaphore, because it has no idea that there should be different kinds of acquire success, and it certainly doesn't know what it has to do in tryAcquireShared when flipped that's special.  we could overide tryAcquireShared, actually, but only if those classes of the implementation weren't final and we had a rational way to override their construction in the superclass.
		//             also i have a bit of a problem with this on interface reusability level.  There's both the problem mentioned above about having to override the AQS subclasses again, but also this:  FlippableSemaphore should only reveal boolean whether or not an acquire succeeded, amirite?  That's not gonna work here.
		//                       hmm.  maybe we could implement some sort of Decider interface?  hand it to AQS-extender, have them keep it as a final field.  i think that would actually be pretty dang inline-able and efficient.
		//    - then somehow magically get the list of blocked threads
		//            on the plus side, you'll note that once we're closed we CAN stop new threads from starting to block, so we're safe there.  so maybe this is doable.
		//              (you can still have people escape via interruption, but it's actually fine if we try to unpark them anyway; unparking is allowed to be spurious for exactly this reason.)
		//    - call LockSupport.unpark with those threads.
		
		//CORESTRAT OVERRELEASE:
		//    really i only have one problem with this, but it's a big one: you can't turn the availablePermits() number into a lie.  that's not okay.
		
		//CORESTRAT SPIN BABY SPIN:
		//    just set up acquires to do fairly stupid timed waits.
		//         in some ways i'm fine with this, because
		//            if you're using a fully blocking call, you obviously aren't all that efficiency-minded to begin with.
		//            even if you get a whole millisecond delay, it's not going to happy to you repeatedly and add up to a real problem.  it'll happen at most once per close.
		//         the thing that does make me kinda twitch is that concepts of fairness are just totally shredded.
		
		//CORESTRAT INTERRUPT:
		//    this is what an older generation of work did, and it... works...
		//    the problem is that if another thread interrupts this thread AND there's a system interrupt to deal with closure at the same time, the interrupt from elsewhere in the application is either going to get eaten because we assume it was ours, or it's going to let that thread escape with an interrupted exception while we're about to interrupt it (it violates the assumption that we can have a well-defined set of blocked threads once closure and emptiness sets in).
		//         now, that's not exactly something that's ever before been a practical problem for me, but it's still just not a battle that i want to concede.
	}
	
	public void acquireUninterruptibly() {
		return isAcquireSuccessful($sync.acquireSharedInterruptibly(1));
	}
	
	public boolean tryAcquire(long $timeout, TimeUnit $unit) throws InterruptedException {
		return super.tryAcquire($timeout, $unit);
	}
	
	public boolean acquire(int $permits) throws InterruptedException {
		if ($permits < 0) throw new IllegalArgumentException();
		return isAcquireSuccessful($sync.acquireSharedInterruptibly($permits));
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
	
	
	
	
	
	private boolean isAcquireSuccessful(int $response) {	// actually i guess i could save myself a lot of footwork if i made this protected too or specified by the Decider
		return ($response >= 0 && $response != 2);
	}
}

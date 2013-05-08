/*
 * Copyright 2010 - 2013 Eric Myhre <http://exultant.us>
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

package us.exultant.ahs.todo;

import java.util.*;
import java.util.concurrent.atomic.*;

// I'm finding this to work almost twice as fast in a double-checked-locking design than directly synchronized (with 4 threads).

//TODO:AHS: merge this with the factory patterns and sync-free-provider.  belongs in which module, core (because that's where people look first for patterns) or thread (because it deals with synchronization)?  i'm thinking former, because it doesn't rely on anything except core java language syncs.
//		also, make a flexible version that resembles CAS: takes a new value, and puts that as the multiton iff the key is free.  the factory-based version can be a concise subtype of that... although they do end up with unpleasantly different interfaces.
//		i do have to admit that i find these to be dangerously close to an antipattern.  if anything one should craft a multiton that can exist in instances, and then make one of those a singleton only if necessary.  still, that's just opinion.

// the most relevant part of the explanation for this on http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html is under the heading "Double-Checked Locking Immutable Objects"

public class FooMultiton {
	private static final Map<Object,FooMultiton>	instances	= new HashMap<Object,FooMultiton>();

	private FooMultiton() {
		n.incrementAndGet();
	}
	public static final AtomicInteger n; static { n = new AtomicInteger(); }

	public static FooMultiton getInstance(Object key) {
		// Our "per key" singleton
		FooMultiton instance = instances.get(key);

		// if the instance has never been created ...
		if (instance == null) {
			synchronized (instances) {
				// Check again, after having acquired the lock to make sure
				// the instance was not created meanwhile by another thread
				instance = instances.get(key);

				if (instance == null) {
					// Lazily create instance
					instance = new FooMultiton();

					// Add it to map
					instances.put(key, instance);
				}
			}
		}
		return instance;
	}

	private static class Poke implements Runnable {
		public void run() {
			for (int i = 0; i < 10000000; i++) {
				FooMultiton.getInstance(i);
			}
		}
	}

	public static void main(String... args) throws InterruptedException {
		long tstart = System.currentTimeMillis();

		Thread[] threads = new Thread[4];

		for (int i = 0; i < threads.length; i++)
			threads[i] = new Thread(new Poke());

		for (int i = 0; i < threads.length; i++)
			threads[i].start();

		for (int i = 0; i < threads.length; i++)
			threads[i].join();

		long tend = System.currentTimeMillis();
		System.out.println("Created "+FooMultiton.n.get()+" instances.");
		System.out.println("Took "+(tend - tstart)+" ms.");
	}
}

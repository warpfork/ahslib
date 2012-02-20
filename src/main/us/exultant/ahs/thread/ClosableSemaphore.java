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

import us.exultant.ahs.anno.*;
import java.util.concurrent.locks.*;

public class ClosableSemaphore extends FlippableSemaphore {
	/**
	 * Creates a {@code ClosableSemaphore} with zero permits with the nonfair fairness
	 * setting in the unclosed state.
	 */
	public ClosableSemaphore() {
		this(false);
	}
	
	/**
	 * Creates a {@code ClosableSemaphore} with zero permits with with the given
	 * fairness setting in the unclosed state.
	 * 
	 * @param $fair
	 *                {@code true} if this semaphore will guarantee first-in first-out
	 *                granting of permits under contention, else {@code false}
	 */
	public ClosableSemaphore(boolean $fair) {
		super($fair, Decider.INSTANCE);
	}
	
	/**
	 * @return true if the semaphore is permanently closed; false otherwise.
	 */
	@Nullipotent
	@ThreadSafe
	public boolean isClosed() {
		return isFlipped();
	}
	
	/**
	 * @return true if {@link #availablePermits()} will return zero now and forever;
	 *         false otherwise (i.e., even if {@link #availablePermits()} is currently
	 *         zero, {@link #isClosed()} is currently false, so permits may be
	 *         available in the future).
	 */
	@Nullipotent
	@ThreadSafe
	public boolean isPermanentlyEmpty() {
		return isFlippedAndZero();
	}
	
	/** <i>invalid operation</i>. Flipping is used internally to implement closure. */
	protected final void flip(boolean $no) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Permanently closes the semaphore. All future attempts to release permits will
	 * fail. Once all the currently available permits have been acquired, all attempts
	 * to acquire permits that would normally have blocked will now return instantly.
	 */
	@Idempotent
	@ThreadSafe
	public void close() {
		super.flip(true);
		for (Thread $t : $sync.getQueuedThreads())
			LockSupport.unpark($t);
	}

	@ThreadSafe
	public String toString() {
		return super.toString() + "[Permits=" + availablePermits() + ";Closed="+isClosed()+"]";
	}
	
	
	
	private final static class Decider extends BlockPolicyDecider {
		public static final BlockPolicyDecider INSTANCE = new Decider();
		
		/**
		 * Returns -1 (an instruction to block), unless we're closed in which case it returns 2.
		 */
		@Deterministic
		public int answerTooFewPermits(boolean $currentlyFlipped) {
			return $currentlyFlipped ? 2 : -1;
		}
		
		/**
		 * Non-negative answers from tryAcquire means we got a permit unless it's 2 in
		 * which case it was released because we were just sick of it.
		 */
		@Deterministic
		public boolean isAcquireSuccessful(int $response) {
			return ($response >= 0 && $response != 2);
		}
		
		/**
		 * Releases are permitted only when state isn't negative. (Zero and
		 * positive are open; negative represents closed.)
		 */
		@Deterministic
		public boolean isReleasePermitted(int $status) {
			return $status >= 0;
		}
	}
}

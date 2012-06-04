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

import us.exultant.ahs.core.*;

/**
 * <p>
 * Allows an action to be attached to a ReadHead directly, triggering it with a piece of
 * data every time one is available. Compared to building a full WorkTarget (or using
 * something similar to {@link WorkTarget.FlowingAdapter}) to handle data from a ReadHead,
 * this is much simpler: this doesn't require you to deal with spurious wakeups, and you
 * don't have to schedule this; you just specify a {@link Listener} for the message type
 * and you're done. On the other hand, there are definitely reasons to prefer the more
 * complex tools: they're more correct! SimpleReactor consumes the thread that delivers
 * events to the ReadHead listener to do its action! That means you should NOT use
 * SimpleReactor to do any heavy-duty work at all if you're building a concurrent system,
 * as you could create instability by end up slowing down operations that other parts of
 * the system expect to be nearly instantaneous.
 * </p>
 * 
 * <p>
 * Class hierarchy note: SimpleReactor does not implement {@code Listener<$T>}, despite
 * the signiture of the methods nearly matching. The reason for this is that this class is
 * not intended to be handed to any other system to be triggered; it produces its own
 * purely internal triggers. Therefore it would not be appropriate to represent it as a
 * Listener.
 * </p>
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * 
 * @param <$T>
 */
public abstract class SimpleReactor<$T> {
	/**
	 * <p>
	 * Calls a Listener every time a piece of data becomes available to a ReadHead.
	 * </p>
	 * 
	 * <p>
	 * This is NOT the same as calling {@code $rh.setListener($handler)} &mdash; that
	 * would cause the listener to be called every time there is a lifecycle event on
	 * the ReadHead (and you may notice would also not be allowed by the type system,
	 * since it is semantically quite different).
	 * </p>
	 * 
	 * <p>
	 * {@link ReadHead#setListener(Listener)} will be called on the given ReadHead, so
	 * the ReadHead must not already have another Listener set unless you don't mind
	 * it being dislodged.
	 * </p>
	 * 
	 * @param $rh
	 *                a ReadHead to attach a handler to
	 * @param $handler
	 *                called every time a piece of data is read. Never called
	 *                spuriously, and never called with null.
	 */
	public static <$T> void bind(ReadHead<$T> $rh, Listener<$T> $handler) {
		new Bridge<$T>($rh, $handler);
	}
	
	public SimpleReactor(ReadHead<$T> $rh) {
		$source = $rh;
		$firedDone = false;
		$source.setListener(new Listener<ReadHead<$T>>() {
			public void hear(ReadHead<$T> $x) {
				while (true) {
					$T $msg = $x.readNow();
					if ($msg == null)
						break;
					else
						SimpleReactor.this.hear($msg);
					//TODO:AHS:THREAD: we really REALLY should have a try/catch around here to keep things from blowing upwards the listener stack, since that's a forbidden thing.  But what to do with exceptions?  This code has no idea.  Perhaps we should introduce a logger in WorkManager that is the destination for all untouchable exceptions like this.
				}
				if ($x.isExhausted() && !$firedDone) {
					$firedDone = true;
					done();
				}
			}
		});
	}
	
	private final ReadHead<$T>	$source;
	private volatile boolean	$firedDone;
	
	protected abstract void hear($T $msg);
	
	protected void done() {}
	
	
	
	private static final class Bridge<$T> extends SimpleReactor<$T> {
		public Bridge(ReadHead<$T> $rh, Listener<$T> $handler) {
			super($rh);
			this.$handler = $handler;
		}
		private final Listener<$T> $handler;
		protected void hear($T $msg) {
			$handler.hear($msg);
		}
	}
}

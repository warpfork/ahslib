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
import us.exultant.ahs.anno.*;
import java.util.concurrent.*;

/**
 * <p>
 * Ackable objects are used to create transactional systems. They work with the existing
 * {@link Flow} system; for example, you can wrap data as you feed it into the
 * {@link WriteHead} of a {@link DataPipe}, and
 * </p>
 * 
 * <p>
 * More interesting is how easy it is to compose this stuff to make distributed
 * transactional systems. Suppose you have a system where one {@link WorkTarget} accepts
 * requests from a user, another WorkTarget is responsible for doing some fairly heavy
 * processing, a third WorkTarget writes some stuff to a database, and a fourth and final
 * WorkTarget sends an acknowledgement back to the user but only after the database commit
 * is completed. This is no whimpy task to accomplish in a concurrent way! But Ackable
 * used with DataPipe can do it:
 * <ol>
 * <li>The WT1 sends an Ackable-wrapped message to WT2, spawns WT4, and sets WT4 to run
 * when the Ackable's WorkFuture finishes.
 * <li>WT2 reads the Ackable, <i>holds on to it</i>, does its work, makes a second
 * Ackable, sets a completion listener on the second Ackable to fire the ack on the first
 * (use {@link #chain(Ackable, Ackable)} to make that a one-liner), and sends the second
 * Ackable and payload along to WT3.
 * <li>WT3 commits stuff to the database and calls ack on its Ackable (which is second
 * one).
 * </ol>
 * ... and after that the rest just works: Ackable 2 fires ack on Ackable 1, and that lets
 * WT4 know that it's ready to go!
 * </p>
 * 
 * <p>
 * You can bridge a system that wants acks to a system that doesn't normally provide them
 * by using a {@link AckableReadHeadBridge} to make the latter system automatically ack as
 * it reads while showing that system only the payload type it was built to expect.
 * Similarly, there is {@link AckableWriteHeadBridge} for systems that expect to read
 * Ackable objects but you don't have a system that provides them.
 * </p>
 * 
 * <p>
 * The kind of behavior that can be added to {@link Flow flows} by using Ackable is not
 * the default because there is a nontrivial amount of memory used and object recreations
 * required to construct the thread safety mechanisms.
 * </p>
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * 
 * @param <PAYLOAD>
 *                the type of payload object that we'd like to request an ack of when it
 *                is recieved.
 */
public class Ackable<PAYLOAD> {
	public Ackable(PAYLOAD $payload) {
		this.$payload = $payload;
		this.$future = new WorkFutureLatched<PAYLOAD>();
	}
	
	private final PAYLOAD				$payload;
	private final WorkFutureLatched<PAYLOAD>	$future;
	
	/**
	 * The payload object this Ackable was constructed around.
	 * 
	 * @return the payload
	 */
	@ThreadSafe
	@Nullipotent
	public PAYLOAD getPayload() {
		return this.$payload;
	}
	
	/**
	 * The WorkFuture to wait on or register callbacks with that will return/fire when
	 * this Ackable is {@link #ack() acknowleged}.
	 * 
	 * @return the payload
	 */
	@ThreadSafe
	@Nullipotent
	public WorkFuture<PAYLOAD> getWorkFuture() {
		return this.$future;
	}
	
	/**
	 * <p>
	 * Fires acknowlegement, causing the WorkFuture from {@link #getWorkFuture()} to
	 * become {@link WorkFuture.State#FINISHED}.
	 * </p>
	 * 
	 * <p>
	 * If acknowlegement is invoked repeatedly, no attempts after the first one will
	 * have any further effects.
	 * </p>
	 */
	@ThreadSafe
	@Idempotent
	public void ack() {
		$future.set($payload);
	}
	
	/**
	 * <p>
	 * Fires (negative) acknowlegement with an exception, causing the WorkFuture from
	 * {@link #getWorkFuture()} to become {@link WorkFuture.State#FINISHED} with an
	 * exception; {@code getWorkFuture().isFinishedGracefully()} will return
	 * {@code false}.
	 * </p>
	 * 
	 * <p>
	 * If acknowlegement is invoked repeatedly, no attempts after the first one will
	 * have any further effects.
	 * </p>
	 */
	@ThreadSafe
	@Idempotent
	public void nak(Throwable $exception) {
		$future.setException($exception);
	}
	
	
	
	/**
	 * <p>
	 * Chain a downstream Ackable to fire {@link #ack() ack} on an upstream Ackable as
	 * soon as it itself is ack'd.
	 * </p>
	 * 
	 * <p>
	 * If the downstream Ackable was {@link #nak(Throwable) nak}'d, the upstream will
	 * be also be nak'd with the same Throwable.
	 * </p>
	 * 
	 * <p>
	 * Multiple upstreams can be chained to a single downstream without a problem. However, if
	 * multiple downstreams are chained to a single upstream, the downstream to be
	 * ack'd will cause the upstream to be ack'd (and thus implicitly may also
	 * determine whether the upstream finishes with an ack or nak).
	 * </p>
	 * 
	 * @param $upstream
	 *                the guy to send an acknowledgement to.
	 * @param $downstream
	 *                the guy who gets acknowledgements first and who should pass them
	 *                on.
	 */
	public static void chain(final Ackable<?> $upstream, final Ackable<?> $downstream) {
		$downstream.getWorkFuture().addCompletionListener(new Listener<WorkFuture<?>>() {
			public void hear(WorkFuture<?> $downstreamResult) {
				boolean $interrupted = false;
				while (true) {
					try {
						$downstreamResult.get();
						$upstream.ack();
					} catch (ExecutionException $e) {
						$upstream.nak($e.getCause());
					} catch (InterruptedException $e) {
						$interrupted = true;
					} finally {
						if ($interrupted) Thread.currentThread().interrupt();
					}
				}
			}
		});
	}
}

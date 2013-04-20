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

package us.exultant.ahs.thread;

import us.exultant.ahs.core.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * <p>
 * Bridges a system that expects to read Ackable objects to a system that doesn't provide
 * them. This class wraps a WriteHead that deals with Ackable objects so that bare
 * payloads can be supplied and they will be automatically wrapped into ackable objects.
 * Whether or not the write methods should wait for acks before returning is configurable
 * at the time of the bridge's construction.
 * </p>
 * 
 * <p>
 * If waiting for acks is enabled, interruption of the thread waiting in a write that has
 * not been acked will cause {@link RuntimeException} wrapping
 * {@link InterruptedException} to be thrown, and {@link Ackable#nak(Throwable) nak}ing
 * will similarly cause the throwing of {@link RuntimeException} wrapping an
 * {@link ExecutionException} wrapping whatever the nak exception was. If these unchecked
 * exceptions present a worry to your application, then you should redesign to use Ackable
 * properly rather than using this simplistic bridge!
 * </p>
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * 
 * @param <$PAYLOAD>
 */
public class AckableWriteHeadBridge<$PAYLOAD> implements WriteHead<$PAYLOAD> {
	/**
	 * Constructs an AckableWriteHeadBridge which immediately returns after writes
	 * without waiting for its messages to be ack'd.
	 * 
	 * @param $ackableHead
	 *                the WriteHead to wrap
	 */
	public AckableWriteHeadBridge(WriteHead<Ackable<$PAYLOAD>> $ackableHead) {
		this($ackableHead, false);
	}
	
	/**
	 * Constructs an AckableWriteHeadBridge with write methods which will optionally
	 * block until their messages are ack'd.
	 * 
	 * @param $ackableHead
	 *                the WriteHead to wrap
	 * @param $wait
	 *                whether or not write methods should block until payload is
	 *                acknowledged
	 */
	public AckableWriteHeadBridge(WriteHead<Ackable<$PAYLOAD>> $ackableHead, boolean $wait) {
		this.$wrap = $ackableHead;
		this.$wait = $wait;
	}
	
	private final WriteHead<Ackable<$PAYLOAD>>	$wrap;
	private final boolean				$wait;
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws RuntimeException
	 *                 if waiting for acks was enabled, and there is a nak or this
	 *                 thread is interrupted while waiting for ack. See the docs on
	 *                 the class for more info.
	 */
	public void write($PAYLOAD $chunk) {
		Ackable<$PAYLOAD> $msg = new Ackable<$PAYLOAD>($chunk);
		$wrap.write($msg);
		if ($wait) try {
			$msg.getWorkFuture().get();
		} catch (ExecutionException $e) {
			throw new RuntimeException("AckableWriteHeadBridge cannot handle exception:", $e);
		} catch (InterruptedException $e) {
			throw new RuntimeException("AckableWriteHeadBridge cannot handle exception:", $e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws RuntimeException
	 *                 if waiting for acks was enabled, and there is a nak or this
	 *                 thread is interrupted while waiting for ack. See the docs on
	 *                 the class for more info.
	 */
	public void writeAll(Collection<? extends $PAYLOAD> $chunks) {
		Collection<Ackable<$PAYLOAD>> $bunches = new ArrayList<Ackable<$PAYLOAD>>();
		for ($PAYLOAD $chunk : $chunks)
			$bunches.add(new Ackable<$PAYLOAD>($chunk));
		$wrap.writeAll($bunches);
		if ($wait) try {
			for (Ackable<$PAYLOAD> $msg : $bunches)
				$msg.getWorkFuture().get();
		} catch (ExecutionException $e) {
			throw new RuntimeException("AckableWriteHeadBridge cannot handle exception:", $e);
		} catch (InterruptedException $e) {
			throw new RuntimeException("AckableWriteHeadBridge cannot handle exception:", $e);
		}
	}
	
	public boolean hasRoom() {
		return $wrap.hasRoom();
	}
	
	public boolean isClosed() {
		return $wrap.isClosed();
	}
	
	public void close() {
		$wrap.close();
	}	
}

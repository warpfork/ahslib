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
 * Bridges a system that wants acks to a system that doesn't normally provide them. This
 * class wraps a ReadHead that deals with Ackable objects to automatically ack everything
 * as it reads it, and then exposes only the payload type.
 *
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 *
 * @param <$PAYLOAD>
 */
public class AckableReadHeadBridge<$PAYLOAD> implements ReadHead<$PAYLOAD> {
	public AckableReadHeadBridge(ReadHead<Ackable<$PAYLOAD>> $ackableHead) {
		$wrap = $ackableHead;
	}

	ReadHead<Ackable<$PAYLOAD>>	$wrap;

	public void setListener(final Listener<ReadHead<$PAYLOAD>> $el) {
		this.$wrap.setListener(new Listener<ReadHead<Ackable<$PAYLOAD>>>() {
			public void hear(ReadHead<Ackable<$PAYLOAD>> $x) {
				$el.hear(AckableReadHeadBridge.this);
			}
		});
	}

	public $PAYLOAD read() {
		Ackable<$PAYLOAD> $x = this.$wrap.read();
		$x.ack();
		return $x.getPayload();
	}

	public $PAYLOAD readNow() {
		Ackable<$PAYLOAD> $x = this.$wrap.readNow();
		$x.ack();
		return $x.getPayload();
	}

	public $PAYLOAD readSoon(long $timeout, TimeUnit $unit) {
		Ackable<$PAYLOAD> $x = this.$wrap.readSoon($timeout, $unit);
		$x.ack();
		return $x.getPayload();
	}

	public boolean hasNext() {
		return this.$wrap.hasNext();
	}

	public List<$PAYLOAD> readAll() throws InterruptedException {
		List<Ackable<$PAYLOAD>> $xs = this.$wrap.readAll();
		List<$PAYLOAD> $v = new ArrayList<$PAYLOAD>($xs.size());
		for (Ackable<$PAYLOAD> $x : $xs) {
			$x.ack();
			$v.add($x.getPayload());
		}
		return $v;
	}

	public List<$PAYLOAD> readAllNow() {
		List<Ackable<$PAYLOAD>> $xs = this.$wrap.readAllNow();
		List<$PAYLOAD> $v = new ArrayList<$PAYLOAD>($xs.size());
		for (Ackable<$PAYLOAD> $x : $xs) {
			$x.ack();
			$v.add($x.getPayload());
		}
		return $v;
	}

	public boolean isClosed() {
		return this.$wrap.isClosed();
	}

	public void close() {
		this.$wrap.close();
	}

	public boolean isExhausted() {
		return this.$wrap.isExhausted();
	}
}

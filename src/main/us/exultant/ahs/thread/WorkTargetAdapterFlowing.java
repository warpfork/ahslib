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
 * Implements most of the guts for the readiness and doneness functions for tasks
 * that deal with streams of data via {@link ReadHead} and {@link WriteHead}.
 * Tasks that are more of a one-shot thing or feel like a callback are probably
 * better expressed using a {@link WorkTargetAdapterTriggerable}.
 * </p>
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * 
 */
public abstract class WorkTargetAdapterFlowing<$IN, $OUT> implements WorkTarget<Void> {
	/**
	 * @param $workSource
	 *                a ReadHead to get data for working on. The availablity
	 *                and exhaustion of this object define the readiness and
	 *                the doneness of this WorkTarget.
	 * @param $workSink
	 *                a WriteHead to push the results of work into. May be
	 *                null, which behaves exactly like
	 *                {@link us.exultant.ahs.core.WriteHead.NoopAdapter}.
	 * @param $priority
	 *                a fixed priority for {@link #getPriority()} to report.
	 */
	public WorkTargetAdapterFlowing(ReadHead<$IN> $workSource, WriteHead<$OUT> $workSink, int $priority) {
		if ($workSource == null) throw new NullPointerException();
		if ($workSink == null) $workSink = new WriteHead.NoopAdapter<$OUT>();
		$src = $workSource;
		$sink = $workSink;
		$prio = $priority;
	}
	
	/** Direct access to this field is not typically necessary or recommended, but is allowed in case for example a subclass should wish to close the stream. */ 
	protected final ReadHead<$IN>	$src;
	/** Direct access to this field is not typically necessary or recommended, but is allowed in case for example a subclass should wish to close the stream. */
	protected final WriteHead<$OUT>	$sink;
	private final int		$prio;
	
	/**
	 * This method attempts to read some data for working on, then if it is
	 * available, passes control to the {@link #run(Object)} method which you
	 * must define.
	 */
	public final Void call() throws Exception {
		$IN $a = $src.readNow();
		if ($a == null) return null;
		$OUT $b = run($a);
		if ($b == null) return null;
		$sink.write($b);
		return null;
	}
	protected abstract $OUT run($IN $chunk) throws Exception;
	
	/** @inheritDocs */
	public final boolean isReady() { return $src.hasNext(); }
	/** @inheritDocs */
	public final boolean isDone() { return $src.isExhausted(); }
	/** @inheritDocs */
	public final int getPriority() { return $prio; }
}

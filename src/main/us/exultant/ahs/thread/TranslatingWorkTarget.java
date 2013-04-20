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
import us.exultant.ahs.util.*;
import us.exultant.ahs.anno.*;

/**
 * <p>
 * Ferries data from a {@link ReadHead} to a {@link WriteHead}, using a thread to perform
 * some translation during the journey.
 * </p>
 * 
 * <p>
 * The typical way of setting up a TranslatingWorkTarget involves setting a
 * {@link Listener} on the <tt>$src</tt> {@link ReadHead} that calls
 * {@link WorkScheduler#update(WorkFuture)} on the scheduler this WorkTarget is registered
 * with.
 * For example:
 * <pre>
 * WorkTarget&lt;Void&gt; $work = new TranslatingWorkTarget&lt;A,B&gt;($src, $trans, $sink);
 * WorkFuture&lt;Void&gt; $future = WorkManager.getScheduler().scheduler($work);
 * $src.setListener(new Listener&lt;ReadHead&lt;A&gt;&gt;() { WorkManager.getScheduler().update($future); }
 * // the scheduler will now notice every time the translator gets fresh work
 * </pre>
 * </p>
 * 
 * <p>
 * An example application of this class would be in moving data from the {@link ReadHead}
 * of an incoming network connection into the {@link WriteHead} from a Pipe that buffers
 * incoming messages ready for the application logic to read, while using a
 * {@link Translator} to convert the raw data binary data from the wire into messages of
 * an application protocol.
 * </p>
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * 
 * @param <$FROM>
 * @param <$TO>
 */
public class TranslatingWorkTarget<$FROM, $TO> implements WorkTarget<Integer> {
	public TranslatingWorkTarget(ReadHead<$FROM> $src, Translator<$FROM, $TO> $trans, WriteHead<$TO> $sink) {
		this($src, $trans, $sink, 0);
	}
	public TranslatingWorkTarget(ReadHead<$FROM> $src, Translator<$FROM, $TO> $trans, WriteHead<$TO> $sink, int $priority) {
		this.$trans = $trans;
		this.$src = $src;
		this.$sink = $sink;
		this.$prio = $priority;
		this.$actsDone = 0;
	}
	
	private final ReadHead<$FROM>			$src;
	private final WriteHead<$TO>			$sink;
	private final Translator<$FROM,$TO>		$trans;
	private ExceptionHandler<TranslationException>	$eh;
	private final int				$prio;
	private int					$actsDone;
	
	public boolean isDone() {
		return $src.isExhausted();
	}
	
	public boolean isReady() {
		return $src.hasNext();
	}
	
	/** Priority in TranslatingWorkTarget is a constant specified when the WorkTarget was constructed. */
	public int getPriority() {
		return $prio;
	}
	
	/**
	 * @return an integer that represents how many times this TranslatingWorkTarget
	 *         has read and performed some action on the read data. Note that this is
	 *         not necessarily the same number as how many bits of data it has written
	 *         out again, since exceptions or null returns from translators will not
	 *         lead to a write.
	 * 
	 * @throws TranslationException
	 *                 if a translation exception is thrown from the translator and an
	 *                 exception handler hasn't been set to deal with it.
	 */
	public Integer call() throws TranslationException {
		if (isDone()) return $actsDone;
		
		$FROM $a = $src.readNow();
		if ($a == null) return $actsDone;
		$actsDone++;
		
		try {
			$TO $b = $trans.translate($a);
			
			if ($b == null) return $actsDone;
			
			$sink.write($b);
		} catch (TranslationException $e) {
			ExceptionHandler<TranslationException> $dated_eh = $eh;
			if ($dated_eh == null) throw $e;
			$dated_eh.hear($e);
		}
		return $actsDone;
	}
	
	/**
	 * <p>
	 * In the case of {@link TranslationException} that occur in the course of a
	 * TranslatingWorkTarget's operation of its {@link Translator}, those exceptions
	 * are sent to the handler specified by this method (or otherwise they may be
	 * discarded silently if no handler has been set).
	 * </p>
	 * 
	 * <p>
	 * Exceptions throw during the operation of the TranslatingWorkTarget that are NOT
	 * types of TranslationException are NOT pushed through this interface; they will
	 * be thrown out of the {@link #call()} method (presumably halting future
	 * executions of the WorkTarget).
	 * </p>
	 * 
	 * <p>
	 * The handler's <code>hear(*)</code> method is invoked by the pumping thread, and
	 * will be executed before the any other actions such as attempting to continue
	 * running the Translator or returning from the {@link #call()} method.
	 * </p>
	 * 
	 * <p>
	 * If changed while the WorkTarget is running, no concurrency errors will occur;
	 * however, be advised that the working thread may take notice of the change in an
	 * extremely lazy fashion, and indeed if more than one thread uses the same
	 * (presumably reentrant) WorkTarget, different threads may notice the update at
	 * markedly and unpredictably different points in their relative execution.
	 * </p>
	 * 
	 * @param $eh
	 * @return this selfsame object
	 */
	@ChainableInvocation
	public TranslatingWorkTarget<$FROM,$TO> setExceptionHandler(ExceptionHandler<TranslationException> $eh) {
		this.$eh = $eh;
		return this;
	}
	
	public String toString() {
		return Reflect.getObjectName(this)+"(acts="+$actsDone+")";
	}
}

package us.exultant.ahs.thread;

import us.exultant.ahs.core.*;

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
 * @author hash
 * 
 * @param <$FROM>
 * @param <$TO>
 */
public class TranslatingWorkTarget<$FROM, $TO> implements WorkTarget<Void> {
	public TranslatingWorkTarget(ReadHead<$FROM> $src, Translator<$FROM, $TO> $trans, WriteHead<$TO> $sink) {
		this($src, $trans, $sink, 0);
	}
	public TranslatingWorkTarget(ReadHead<$FROM> $src, Translator<$FROM, $TO> $trans, WriteHead<$TO> $sink, int $priority) {
		this.$trans = $trans;
		this.$src = $src;
		this.$sink = $sink;
		this.$prio = $priority;
	}
	
	private final ReadHead<$FROM>			$src;
	private final WriteHead<$TO>			$sink;
	private final Translator<$FROM,$TO>		$trans;
	private ExceptionHandler<TranslationException>	$eh;
	private final int				$prio;
	
	public boolean isDone() {
		return $src.isClosed() && !$src.hasNext();
	}
	
	public boolean isReady() {
		return $src.hasNext();
	}
	
	/** Priority in TranslatingWorkTarget is a constant specified when the WorkTarget was constructed. */
	public int getPriority() {
		return $prio;
	}
	
	public Void call() {
		if (isDone()) return null;
		
		$FROM $a = $src.readNow();
		if ($a == null) return null;
	
		try {
			$TO $b = $trans.translate($a);
			
			if ($b == null) return null;
			
			$sink.write($b);
		} catch (TranslationException $e) {
			// this error handling is the SAME for both errors in the translator and errors from writing the the sink.
			ExceptionHandler<TranslationException> $dated_eh = $eh;
			if ($dated_eh != null) $dated_eh.hear($e);
		}
		return null;
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
	 */
	public void setExceptionHandler(ExceptionHandler<TranslationException> $eh) {
		this.$eh = $eh;
	}
}
package ahs.io;

import ahs.util.*;
import ahs.util.thread.*;

import java.io.*;
import java.util.*;

// it might be better semantics to ALWAYS have the normal read methods return full chunks,
//  and then provide a single extra readLast() method that throws exceptions unless called after the head is closed.
// on the other hand, nobody ever said that a particular subclass can't choose to do that anyway.  there's no need to make it explicit here.

/**
 * <p>
 * Provides a decorator to make file, network, and internal pipe operations all
 * transparent and operational in either blocking or nonblocking modes via interfaces for
 * possible buffering and a unified scheme for pumping underlying streams or channels.
 * This unified scheme has the noteworthy aspect of always allowing incoming data to be
 * requested at the application's leisure (as opposed to requiring any sort of event
 * listener), while simultaneously allowing event listeners to be attached if the old
 * "listener" pattern is desired or "select"-like functionality need be implemented across
 * multple ReadHead instances.
 * </p>
 * 
 * <p>
 * Unlike other stream and channel interfaces, ReadHead intends to always be aligned to
 * something <i>meaningful</i> -- though what exactly constitutes "meaningful" is left up
 * to the implementation, and is specified by the generic type. ReadHead specifically
 * intends to prevent any excessive copying of arrays around by its clients; a single
 * instance of the generic object returned by the read methods should be sufficient to
 * warrant processing in its own right, and the readAll methods are provided only as a a
 * matter of completeness and convenience (and because many stream and channel interfaces
 * lack such simple conveniences) for use when the total volume of data is manageably
 * small.
 * </p>
 * 
 * <p>
 * Implementers of this interface could potentially be used to decorate an instance
 * java.io.InputStream, java.util.concurrent.ConcurrentLinkedQueue,
 * java.nio.channels.Channel... whatever.
 * </p>
 * 
 * <p>
 * Implementers are typically expected to follow some sort of RAII (Resource Acquisition
 * Is Initialization) pattern, as no methods regarding opening or initializing are
 * provided by this interface, but there is absolutely no requirement that implementers do
 * so, and methods responsible for initialization may be as apt.
 * </p>
 * 
 * <p>
 * Implementers should avoid throwing OperationNotSupported exceptions from methods
 * relating to nonblocking reads if at all possible even when the underlying stream or
 * channel is provides only blocking services. Such issues can be resolved by reading the
 * blocking stream into a nonblocking buffer using the thread provided by the ReadHead's
 * pump, then providing direct access only to the nonblocking buffer. (The standard
 * library's ConcurrentLinkedQueue class is an ideal helper for such a role.)
 * </p>
 * 
 * <p>
 * Note that the lack of methods for reading contiguous blocks; this is neither accidental
 * nor an oversight. The author asserts that if you find yourself with a desire for this
 * behavior, you're Doing It Wrong. The problem you are confronting is probably best
 * solved by using a ReadHead with a generic type that it itself a generic List, or using
 * some other sort of object as a container for batches.
 * </p>
 * 
 * @author hash
 * 
 * @param <$T>
 */
public interface ReadHead<$T> {
	/**
	 * <p>
	 * Grants access to the Pump which powers the channel underlying this ReadHead.
	 * Some implementations may not require a Pump (in-program pipes are typically in
	 * this pattern, since their data is effectively pumped in by the thread doing the
	 * write to the pipe), in which case they must return null.
	 * </p>
	 * 
	 * <p>
	 * Calling <code>getPump().run(1)</code> followed by <code>read()</code> should
	 * effectively make any ReadHead that requires pumping act exactly as if it was
	 * backed by a blocking stream being pumped by the current thread.
	 * </p>
	 * 
	 * @return the Pump instance associated with this ReadHead, or null if this
	 *         implementation does not require pumping.
	 */
	public Pump getPump();
	
	/**
	 * <p>
	 * In the case of exceptions that occur in the course of a Pump's operations, the
	 * Pump sends those exceptions to the handler specified by this method (or may
	 * discard them silently if no handler has been set). Exceptions caught by the
	 * Pump that are not IOExceptions are still pushed through this interface by
	 * listing them as the cause of a new IOException that is then rethrown.
	 * Exceptions not caught by the Pump can still bubble out of the Pump without
	 * being pushed through this interface, but no exception should do both.
	 * </p>
	 * 
	 * <p>
	 * The handler's <code>hear(*)</code> method is invoked by the pumping thread, and
	 * will be executed before the Pump takes any other actions such as attempting to
	 * continue reading.
	 * </p>
	 * 
	 * @param $eh
	 */
	public void setExceptionHandler(ExceptionHandler<IOException> $eh);
	
	/**
	 * <p>
	 * Sets a Listener for critical events in the lifecycle of the ReadHead. The
	 * Listener must be invoked by the implementer every time a new chunk of data
	 * becomes available. Implementations may also choose to use it to signal other
	 * critical events such as the closing of the underlying stream. In all
	 * situations, the Listener is handed a reference to this ReadHead.
	 * </p>
	 * 
	 * <p>
	 * Despite the listener's intended purpose, it is critical to note that even if a
	 * <code>hasNext()</code> call is the first thing within the Listener's <code>hear(*)</code>
	 * procedure, that call may return false. This may situation may arise even if the
	 * call to the Listener was intended to signal new data availability, since
	 * multithreaded access to the ReadHead can result in another thread having
	 * pre-empted the Listener and consumed the data before the Listener can respond.
	 * </p>
	 * 
	 * <p>
	 * The Listener's <code>hear(*)</code> method will be invoked from the thread
	 * currently powering the pump (or in some cases the thread responsible for the
	 * write that triggered the event), and as such should not be responsible for any
	 * intensive or time-consuming operations. The recommended usage is to simply
	 * provide an event listener that marks the ReadHead as (probably) having data
	 * available in some other "selector"-like scheme.
	 * </p>
	 * 
	 * <p>
	 * Future versions of this library may depricate this method in favor of a
	 * selector scheme provided by this package, and thereafter remove the method from
	 * the public namespace entirely.
	 * </p>
	 * 
	 * @param $el
	 */
	public void setListener(Listener<ReadHead<$T>> $el);
	
	/**
	 * <p>
	 * Blocking read. Elements that are read are removed from the stream.
	 * </p>
	 * 
	 * <p>
	 * If multiple threads block on this concurrently, the choice of whether or not to
	 * provide a guarantee of fairness is left up to the implementor. Regardless,
	 * basic synchronicity must be maintained -- there will be no double-reads, null
	 * pointer exceptions, concurrent modification exceptions, or etc.
	 * </p>
	 * 
	 * @return next chunk of input, or null if there is no data available and the
	 *         underlying stream has reached an <code>EOF</code> state. Some
	 *         implementations may also choose to return null in the case that the
	 *         underlying stream reaches an EOF state while this call is still
	 *         blocking for more input; others may try to return a partial chunk.
	 */
	public $T read();
	
	/**
	 * Nonblocking read. Elements that are read are removed from the stream.
	 * 
	 * @return a chunk of input if possible, or null otherwise; null may indicate
	 *         either <code>EOF</code> or simply nothing available at the time.
	 *         <code>isClosed()</code> should be used to determine the difference.
	 */
	public $T readNow();
	
	/**
	 * Tells whether or not input is immediately availale to be read. If the ReadHead
	 * is shared by multiple threads this should not be relied upon to determine if a
	 * subsequent blocking call will return without waiting, since other threads may
	 * have already pre-empted it.
	 * 
	 * @return true if a chunk of input is stream to be read immediately; false
	 *         otherwise. Similarly to <code>readNow()</code>, a return of false may
	 *         indicate either <code>EOF</code> or simply nothing available at the time.
	 *         <code>isClosed()</code> should be used to determine the difference.
	 */
	public boolean hasNext();
	
	/**
	 * <p>
	 * Blocks until the stream is closed, then returns its entire contents at once
	 * (minus any entries that have already been read, even if those other reads take
	 * place <i>after</i> the invocation of <code>readAll()</code>). If multiple
	 * threads invoke this, then one of them will recieve a normal result, and the
	 * rest will receive empty arrays (as will subsequent invocations).
	 * </p>
	 * 
	 * <p>
	 * <i>Note:</i> if you feel this behavior (waiting until the end of stream but
	 * still allowing other reads) odd, consider the following points:
	 * <ol>
	 * <li>If you're afraid of losing data between invocation and reaching EOF, simply
	 * ensure you've already stopped reading that might take place in any other
	 * threads.
	 * <li>This behavior is meant to help ensure all data has been retrieved by the
	 * time a reading thread exits -- call it in every thread and between all of them
	 * they'll get everything.
	 * <li>In some implementations (namely those based on semphore permits), it's
	 * difficult to stop other readers internally without linking the locking of reads
	 * and writes, which is highly undesirable since it implies greater complexity and
	 * overhead to all calls.
	 * </ol>
	 * </p>
	 * 
	 * @return a primitive array containing one entry for each chunk of input
	 *         following the last invocation of a read method that is available from
	 *         the stream between the time of this method's invocation and the closing
	 *         of the stream. The array returned may have zero entries if no data ever
	 *         becomes available (including if the stream is already closed and empty
	 *         when the invocation occurs), but null may never be returned.
	 * @throws UnsupportedOperationException
	 *                 if the underlying stream has no notion of closed or finished,
	 *                 since this method is then not well defined.
	 * 
	 */
	public List<$T> readAll();
	
	/**
	 * Immediately returns entire contents of this stream at once (minus, of course,
	 * any entries that have already been read). Similarly to the blocking
	 * <code>readAll()</code> method, if multiple threads invoke this after the stream
	 * is closed then the first will receive a normal result, and other threads and
	 * subsequent invocations will receive empty arrays.
	 * 
	 * @return a primitive array containing one entry for each chunk of input
	 *         following the last invocation of a read method that is currently
	 *         available from the stream. The array returned may have zero entries if
	 *         there is no input currently available, but null may never be returned.
	 */
	public List<$T> readAllNow();
	
	
	
	/**
	 * @return true if the ReadHead has internally reached some sort of
	 *         <code>EOF</code> state; false otherwise. Upon returning true, data may
	 *         still exist in buffers waiting to be read; however, it is guaranteed
	 *         that once both isClosed() and hasNext() return true and false
	 *         respectively that no further invocations of hasNext() will return true.
	 */
	public boolean isClosed();
	
	/**
	 * <p>
	 * Closes the underlying stream or channel. The semantics of this are somewhat
	 * nebulous in many cases; however, at bare minimum, implementers should adhere to
	 * the general contract that read invocations blocking for any reason (and, in
	 * particular, reads blocking for stream completion) will return following
	 * invocation of this function.
	 * </p>
	 * 
	 * <p>
	 * Implementers may choose to simply relay the "close" invocation to their
	 * underlying stream or channel, and then none the less continue to pump that
	 * underlying mechanism until it begins to throw exceptions. This behavior avoids
	 * problems that would otherwise arise in some situations where we must avoid
	 * losing data in the abyss between buffers if the underlying mechanism is willing
	 * to return already buffered data even if it has been closed, but has the
	 * unfortunate side effect that invocations of <code>isClosed()</code> are not
	 * required to return <code>true</code> immediately after <code>close()</code> is
	 * called.
	 * </p>
	 * 
	 * <p>
	 * In situations that bear resemblance to pipes (that is, a ReadHead is paired
	 * with a WriteHead at some other position along the same underlying stream or
	 * channel; network connections are typically exemplary of this as well as
	 * in-program pipes), this method may typically be expected to maintain the same
	 * semantics as the general contract of close methods of the underlying type --
	 * namely, that the matching WriteHead (or equivalent) may find its stream or
	 * channel to have become closed as well.
	 * </p>
	 * 
	 * <p>
	 * Only the first invocation of this function should have any effect; closing a
	 * closed ReadHead is illogical (but should not typically throw exceptions).
	 * </p>
	 * 
	 * @throws IOException
	 *                 if the close operation results in problems or if the underlying
	 *                 stream throws an IOException.
	 */
	public void close() throws IOException;
}

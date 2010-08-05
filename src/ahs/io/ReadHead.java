package ahs.io;

import ahs.util.*;
import ahs.util.thread.*;

import java.io.*;

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
 * so.
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
 * @author hash
 * 
 * @param <$T>
 */
public interface ReadHead<$T> {
	/**
	 * <p>
	 * Grants access to the Pump which powers the channel underlying this ReadHead. A
	 * "blank" pump (which may also be a singleton) may be returned if the ReadHead
	 * implementation does not require a thread for pumping the underlying channel,
	 * but null may never be returned.
	 * </p>
	 * 
	 * <p>
	 * Calling <code>getPump().run(1)</code> followed by <code>read()</code> should
	 * effectively make any ReadHead act exactly as if it was backed by a blocking
	 * stream being pumped by the current thread.
	 * </p>
	 * 
	 * @return the Pump instance associated with this ReadHead
	 */
	public Pump getPump();
	
	public void setExceptionHandler(ExceptionHandler<IOException> $eh);
	
	/**
	 * <p>
	 * Sets a Listener that is invoked every time the ReadHead's pump adds a new chunk
	 * of data to the queue of data available in its internal buffer; the Listener
	 * will be handed a reference to this ReadHead.
	 * </p>
	 * 
	 * <p>
	 * The Listener's <code>hear()</code> method will be invoked from the thread
	 * currently powering the pump, and as such should not be responsible for any
	 * intensive or time-consuming operations. The recommended usage is to simply
	 * provide an event listener that marks the ReadHead as having data available in
	 * some other "selector"-like scheme. Future versions of this library may
	 * depricate this method in favor of a selector scheme provided by this package,
	 * and thereafter remove the method from the public namespace entirely.
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
	 * @return next chunk of input, or null if there is no data available and the underlying
	 *         stream has reached an EOF state.
	 */
	public $T read();
	
	/**
	 * Nonblocking read. Elements that are read are removed from the stream.
	 * 
	 * @return a chunk of input if possible, or null otherwise; null may indicate
	 *         either EOF or simply nothing available at the time.
	 *         <code>isClosed()</code> should be used to determine the difference.
	 */
	public $T readNow();
	
	/**
	 * Tells whether or not input is availale to be read. Obviously this semantics of
	 * this are somewhat unreadably if the ReadHead is shared by multiple threads.
	 * 
	 * @return true if a chunk of input is stream to be read immediately; false
	 *         otherwise. Similarly to <code>readNow()</code>, a return of false may
	 *         indicate either EOF or simply nothing available at the time.
	 *         <code>isClosed()</code> should be used to determine the difference.
	 */
	public boolean hasNext();
	
	/**
	 * Blocks until the stream is closed, then returns its entire contents at once
	 * (minus, of course, any entries that have already been read). The semantics of
	 * this are undefined if multiple threads attempt to read after the stream is
	 * closed.
	 * 
	 * @return a primitive array containing one entry for each chunk of input
	 *         following the last invocation of a read method that is available from
	 *         the stream between the time of this method's invocation and the closing
	 *         of the stream. The array returned may have zero entries if no data ever
	 *         becomes available, but null may never be returned.
	 */
	public $T[] readAll();
	
	/**
	 * Immediately returns entire contents of this stream at once (minus, of course,
	 * any entries that have already been read). The semantics of this are undefined
	 * if multiple threads attempt to read after the stream is closed.
	 * 
	 * @return a primitive array containing one entry for each chunk of input
	 *         following the last invocation of a read method that is currently
	 *         available from the stream. The array returned may have zero entries if
	 *         there is no input currently available, but null may never be returned.
	 */
	public $T[] readAllNow();
	
	
	
	/**
	 * @return true if the ReadHead has internally reached some sort of EOF state;
	 *         false otherwise. Upon returning true, data may still exist in buffers
	 *         waiting to be read; however, it is guaranteed that once both isClosed()
	 *         and hasNext() return true and false respectively that no further
	 *         invocations of hasNext() will return true.
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
	 * channel; network connections are typically exemplary of this), this method may
	 * typically be expected to maintain the same semantics as the general contract of
	 * close methods of the underlying type -- namely, that the matching WriteHead (or
	 * equivalent) may find its stream or channel to have become closed as well.
	 * </p>
	 * 
	 * <p>
	 * Only the first invocation of this function should have any effect; closing a
	 * closed ReadHead is illogical (but should not typically throw exceptions).
	 * </p>
	 * 
	 * @return the same object the call was invoked on, for invocation chaining.
	 *         (Implementers and subclasses should always override the return type to
	 *         match themselves).
	 * @throws IOException
	 */
	public ReadHead<$T> close() throws IOException;
}

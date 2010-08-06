package ahs.io;

import ahs.util.*;

import java.io.*;
import java.util.*;

/**
 * <p>
 * The complement of ReadHead.
 * </p>
 * 
 * <p>
 * In keeping with the philosophy that the WriteHead and ReadHead interfaces are for
 * dealing with "semantically meaningful" chunks of information, WriteHead provides no
 * notion of "flush" that is typical of some other stream interfaces, as it all chunks
 * should be meaningful enough to warrant "flushing" immediately upon write.
 * </p>
 * 
 * <p>
 * WriteHead are typically expected to do most of their operations in the current thread
 * and complete them before they return. In situations where this is untenable for some
 * reason, one ought not violate the standards of the WriteHead interface; instead, one
 * should use a Pipe to create a buffer between the real, blocking WriteHead, and a much
 * softer WriteHead that returns as soon as it has committed its chunk to the buffer.
 * Exceptions from the "real" WriteHead can then be handed to the ExceptionHandler in the
 * Pipe's ReadHead. TODO:AHS: make a class that does this generally and conveniently.
 * </p>
 * 
 * <p>
 * "Without blocking" for the purpose of this interface is a bit vague, and should be
 * taken more as "with minimal blocking as absolutely critical". For example, in
 * filesystem operations it means merely that the WriteHead will impose no additional wait
 * beyond normal disk access time; in the case of a network file system, there may still
 * be significant blocking for the disk access itself
 * </p>
 * 
 * @author hash
 */
public interface WriteHead<$T> {
	public void write($T $chunk) throws IOException;
	
	public void writeAll(Collection<? extends $T>  $chunks) throws IOException;
	
	/**
	 * This function allows for systems which contain some sort of limit on their
	 * underlying stream to indicate to users that such a limit has been reached. Its
	 * intended use situation is in pipes or queues that impose a capacity limit; in
	 * most cases, it is not used and always returns true regardless of state.
	 * (WriteHead implementers relating to disk access in particular should NOT use
	 * this to report on something like the state of fullness of the filesystem; that
	 * should be done through other methods or by the throwing of an IOException
	 * during a write that would overcommit.)
	 * 
	 * @return true if an immediately subsequent (all standard caveats about the
	 *         realisticness of "immediacy" in multithreading aside) call to write()
	 *         will return immediately without blocking.
	 */
	public boolean hasRoom();
	
	/**
	 * <p>
	 * Closes the underlying stream or channel. Invocations of write methods after
	 * this close method should result in failure and IOExceptions.
	 * </p>
	 * 
	 * <p>
	 * In situations that bear resemblance to pipes (that is, a ReadHead is paired
	 * with a WriteHead at some other position along the same underlying stream or
	 * channel; network connections are typically exemplary of this as well as
	 * in-program pipes), this method may typically be expected to maintain the same
	 * semantics as the general contract of close methods of the underlying type --
	 * namely, that the matching WriteHead (or equivalent) may find its stream or
	 * channel to have become closed as well. However, it's worth noting that there
	 * may be some delay between the invocation of close on one and the resolution of
	 * the closed state on the remote partner (even if the remote partner is in the
	 * same machine or program), since layers of buffering can delay the movement of
	 * the signal.
	 * </p>
	 * 
	 * <p>
	 * Only the first invocation of this function should have any effect; closing a
	 * closed WriteHead is illogical (but should not typically throw exceptions).
	 * </p>
	 * 
	 * @throws IOException
	 *                 if the close operation results in problems or if the underlying
	 *                 stream throws an IOException.
	 */
	public void close() throws IOException;
	

	/**
	 * @return true if the underlying stream is closed and writes are not possible.
	 */
	public boolean isClosed();
}

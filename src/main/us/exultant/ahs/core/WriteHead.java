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

package us.exultant.ahs.core;

import java.util.*;

/**
 * <p>
 * Provides an interface to make file, network, and internal pipe operations all
 * transparently; WriteHead is the complement of {@link ReadHead}.
 * </p>
 * 
 * <p>
 * In keeping with the philosophy that the WriteHead and ReadHead interfaces are for
 * dealing with "semantically meaningful" chunks of information, WriteHead provides no
 * notion of "flush" that is typical of some other stream interfaces, as all chunks should
 * be meaningful enough to warrant "flushing" immediately upon write.
 * </p>
 * 
 * <p>
 * WriteHead are typically expected to do most of their operations in the current thread
 * and complete them before they return. In situations where this is untenable for some
 * reason, one ought not violate the standards of the WriteHead interface; instead, one
 * should use a Pipe to create a buffer between the real, blocking WriteHead, and a much
 * softer WriteHead that returns as soon as it has committed its chunk to the buffer.
 * </p>
 * 
 * <p>
 * "Without blocking" for the purpose of this interface is a bit vague, and should be
 * taken more as "with minimal blocking as absolutely critical". For example, in
 * filesystem operations it means merely that the WriteHead will impose no additional wait
 * beyond normal disk access time; in the case of a network file system, there may still
 * be significant blocking for the disk access itself.
 * </p>
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 */
public interface WriteHead<$T> {
	/**
	 * Writes a chunk of data to a stream, returning when the write is complete.
	 * 
	 * @param $chunk
	 * @throws IllegalStateException
	 *                 if the WriteHead has been closed.
	 * @throws NullPointerException
	 *                 if the chunk is null
	 */
	public void write($T $chunk);
	
	/**
	 * <p>
	 * Writes a collection of data chunks to a stream, returning when the write is
	 * complete.
	 * </p>
	 * 
	 * <p>
	 * All elements added as a group in this way should be guaranteed to come out of a
	 * paired ReadHead in the same order as the original ordering of the collection,
	 * and shall not be intermingled with other objects; if an implementation does not
	 * support this behavior, it should be documented loudly.
	 * </p>
	 * 
	 * <p>
	 * Some elements added as a group in this way may be made available to a paired
	 * ReadHead before this call returns and before the entire group is added (so it
	 * may be possible for a thread reading from a pipe in a non-blocking fashion to
	 * read half of the group of elements, then get nulls, and then later return to
	 * see the other half of the group).
	 * </p>
	 * 
	 * <p>
	 * Though it is allowed for written objects to begin becoming available from a
	 * paired ReadHead before this method returns, it is not necessary for the
	 * listener on such a read head to be called as each of those objects become
	 * available (though it may be).
	 * </p>
	 * 
	 * <p>
	 * If an exception is thrown in the middle of writing the collection of data, that
	 * exception will bubble out of this method immediately and elements of the
	 * collection that have not yet been written shall not be written as a result.
	 * </p>
	 * 
	 * @param $chunks
	 * @throws IllegalStateException
	 *                 if the WriteHead has been closed.
	 * @throws NullPointerException
	 *                 if any chunk in the collection is null
	 */
	public void writeAll(Collection<? extends $T> $chunks);
	
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
	 * Reports whether or not the WriteHead is closed to the entry of data.
	 * 
	 * @return true if the underlying stream is closed and writes are not possible.
	 */
	public boolean isClosed();
	
	/**
	 * <p>
	 * Closes the underlying stream or channel. Invocations of write methods after
	 * this close method should result in failure and the throwing of
	 * IllegalStateException.
	 * </p>
	 * 
	 * <p>
	 * In situations that bear resemblance to pipes (that is, a ReadHead is paired
	 * with a WriteHead at some other position along the same underlying stream or
	 * channel; network connections are typically exemplary of this as well as
	 * in-program pipes), this method may typically be expected to maintain the same
	 * semantics as the general contract of close methods of the underlying type
	 * &mdash; namely, that the matching WriteHead (or equivalent) may find its stream
	 * or channel to have become closed as well. However, it's worth noting that there
	 * may be some delay between the invocation of close on one and the resolution of
	 * the closed state on the remote partner (even if the remote partner is in the
	 * same machine or program), since layers of buffering can delay the movement of
	 * the signal.
	 * </p>
	 * 
	 * <p>
	 * Only the first invocation of this function should have any effect; closing a
	 * closed WriteHead is illogical (but should not throw exceptions).
	 * </p>
	 */
	public void close();
}

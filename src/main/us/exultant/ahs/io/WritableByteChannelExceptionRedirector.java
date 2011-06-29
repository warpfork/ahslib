package us.exultant.ahs.io;

import us.exultant.ahs.core.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;



/**
 * <p>
 * Wraps an existing {@link WritableByteChannel} to hides all exceptions from the client,
 * rerouting them to an {@link ExceptionHandler} instead. (Primary intended use case is as
 * a utility for other higher-level abstractions in the AHS library rather than in client
 * code.)
 * </p>
 * 
 * <p>
 * This decorator allows the {@link #write(ByteBuffer)} and {@link #close()} operations
 * drop the potential to throw {@link IOException} from their prototypes; this change in
 * abstraction can in turn be useful in situations where multithreaded buffered operations
 * are used, since the code in the thread responsible interacting with the channel
 * directly may not be aware of the context that defines the appropriate response to such
 * exceptions (i.e., that context exists in a different thread on the other side of the
 * buffer) and it is instead more appropriate to allow registration of the error handling
 * code from a separate region that to use any form of catching in the code disbatching
 * writes from the buffer.
 * </p>
 * 
 * <p>
 * This class performs no synchronization. It is assumed that any necessary
 * synchronization is performed outside of this class; if there is none, then any
 * ExceptionHandler will of course be required to attend to their own thread-safety.
 * </p>
 */
class WritableByteChannelExceptionRedirector implements WritableByteChannel {
	/**
	 * @param $bc
	 *                the WritableByteChannel to wrap
	 * @param $eh
	 *                the error handling logic (to be used for both errors stemming
	 *                from writes as well as from closing).
	 */
	public WritableByteChannelExceptionRedirector(WritableByteChannel $bc, ExceptionHandler<IOException> $eh) {
		this.$bc = $bc;
		this.$ehw = $eh;
		this.$ehc = $eh;
	}
	
	/**
	 * @param $bc
	 *                the WritableByteChannel to wrap
	 * @param $ehw
	 *                the error handling logic to be used for errors stemming
	 *                from writes.
	 * @param $ehc
	 *                the error handling logic to be used for errors stemming
	 *                from closing.
	 */
	public WritableByteChannelExceptionRedirector(WritableByteChannel $bc, ExceptionHandler<IOException> $ehw, ExceptionHandler<IOException> $ehc) {
		this.$bc = $bc;
		this.$ehw = $ehw;
		this.$ehc = $ehc;
	}
	
	private ExceptionHandler<IOException>	$ehw;
	private ExceptionHandler<IOException>	$ehc;
	private WritableByteChannel		$bc;
	
	/**
	 * Exactly as per {@link WritableByteChannel#close()}, but when
	 * IOException is thrown by the underlying channel, it is handed to the
	 * ExceptionHandler (the calling thread continues to be used for this, so
	 * if the exception handler is called then it has returned by the time
	 * this close invocation returns).
	 */
	public void close() {
		try {
			$bc.close();
		} catch (IOException $ioe) {
			$ehc.hear($ioe);
		}
	}
	
	/**
	 * Exactly as per {@link WritableByteChannel#isOpen()}.
	 */
	public boolean isOpen() {
		return $bc.isOpen();
	}
	
	/**
	 * As per {@link WritableByteChannel#write(ByteBuffer)}, but when
	 * IOException is thrown by the underlying channel, it is handed to the
	 * ExceptionHandler (the calling thread continues to be used for this, so
	 * if the exception handler is called then it has returned by the time
	 * this write invocation returns) and this method returns -1.
	 * 
	 * @return The number of bytes written, possibly zero; -1 in the case of
	 *         errors.
	 */
	public int write(ByteBuffer $dst) {
		try {
			return $bc.write($dst);
		} catch (IOException $ioe) {
			$ehw.hear($ioe);
			return -1;
		}
	}
}
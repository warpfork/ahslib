package us.exultant.ahs.io;

import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

/**
 * <p>
 * This 'writes' by actually 'translating' bytes onto a communication channel. It follows
 * the dead-simple "babble" protocol: when given a series of arbitrary blobs, the bytes
 * that end up in the channel look like a series of 4-byte signed integer lengths,
 * followed by that blob of the size specified by the int.
 * </p>
 * 
 * <p>
 * Two distinct implementations of this are provided as nested classes; one does a
 * significantly better job of maintaining its promise to be nonblocking (
 * {@link Nonblocking}) but at the cost of greater complexity for the client code, while
 * the other is simpler to use but may sometimes block ({@link Blocking}).
 * </p>
 * 
 * @author hash
 * 
 */
public abstract class TranslatorByteBufferToChannel implements Translator<ByteBuffer,TranslatorByteBufferToChannel.Completor> {
	/**
	 * <p>
	 * This implementation of translating writes to channels is nonblocking. It
	 * returns an object (a {@link Completor}) that is responsible for the given blob.
	 * </p>
	 * 
	 * <p>
	 * (This class bears close resemblance to
	 * {@link TranslatorChannelToByteBuffer.Nonblocking} aside from the return type;
	 * TranslatorChannelToByteBuffer is just able to do a better job of hiding its
	 * equivalent of {@link Completor} because {@link TranslatorStack} allows it to
	 * return nulls if it isn't finished with an entire semantic read (whereas
	 * {@link TranslatorStack} unfortunately has no such route for permissiveness of
	 * incomplete writes).)
	 * </p>
	 * 
	 * @author hash
	 */
	public static class Nonblocking extends TranslatorByteBufferToChannel {
		public Nonblocking(WritableByteChannel $base) {
			this.$base = $base;
		}
		
		private final WritableByteChannel	$base;
		
		public Completor translate(ByteBuffer $blob) throws TranslationException {
			return new Completor($base, $blob);
		}
	}
	
	
	
	/**
	 * <p>
	 * This implementation of writing to channels is blocking.
	 * </p>
	 * 
	 * <p>
	 * In practice, blocking behavior is unlikely to be observed unless your
	 * application is pushing out data faster than it can be routed out, and if this
	 * occurs it is likely a sign that your worker threads ought to slow the heck down
	 * anyway (which occurs implicitly if the worker threads are the ones pushing out
	 * the data).
	 * </p>
	 * 
	 * <p>
	 * It is NOT possible to use the same instance of a
	 * TranslatorByteBufferToChannel.Blocking in several TranslationStack for any
	 * purpose if threads are involved.
	 * </p>
	 * 
	 * @author hash
	 */
	public static class Blocking extends TranslatorByteBufferToChannel {
		public Blocking(WritableByteChannel $base) {
			this.$base = $base;
		}
		
		private final WritableByteChannel	$base;
		private final ByteBuffer		$preint	= ByteBuffer.allocate(4);
		private static final Completor		$straw = new Completor();
		
		public Completor translate(ByteBuffer $blob) throws TranslationException {
			$preint.clear();
			$preint.putInt($blob.remaining());
			$preint.rewind();
			subwrite($preint);
			subwrite($blob);
			return $straw;
		}
		
		private void subwrite(ByteBuffer $lit) throws TranslationException {
			try {
				$base.write($lit);
				while ($lit.remaining() > 0) {
					X.chill(7);
					$base.write($lit);
				}
			} catch (IOException $e) {
				throw new TranslationException("problems with base channel", $e);
			}
		}
	}
	
	
	
	/**
	 * <p>
	 * A Completor is responsible for commiting the entirety of a blob to a channel,
	 * while allowing partial writes from every particular operation attempt.
	 * </p>
	 * 
	 * <p>
	 * A Completor should be repeatedly invoked to nonblockingly write as much of the
	 * blob as immediately possible until the blob is consumed. If several
	 * {@link Completor}s are produced regarding the same {@link WritableByteChannel},
	 * as long as they are all consumed completely after their first
	 * {@link Completor#write} call without writes called on any other competing
	 * Completors in the meanwhile, everything works out fine.
	 * </p>
	 * 
	 * <p>
	 * Some implementations of TranslatorByteBufferToChannel may return no-op
	 * Completors which are already "completed" without requiring any write operations
	 * because they've already completed their writes in a blocking fashion (in other
	 * words, such a Completor may be safely ignored, but may also be handled in the
	 * exact same way as a fully functional completor).
	 * </p>
	 * 
	 * @author hash
	 */
	public static class Completor {
		private Completor(WritableByteChannel $chan, ByteBuffer $blob) {
			this.$chan = $chan;
			this.$blob = $blob;
			this.$preint = ByteBuffer.allocate(4);
			$preint.putInt($blob.remaining());
			$preint.rewind();
		}
		private Completor() {
			this.$chan = null;
			this.$blob = null;
			this.$preint = null;
		}

		private final WritableByteChannel $chan;
		private final ByteBuffer $blob;
		private final ByteBuffer $preint;
		
		/**
		 * Immediately attempts to write as much data as possible to this
		 * Completor's channel from its assigned blob.
		 * 
		 * @return the number of bytes of the blob written to channel; may be zero
		 *         (if the channel was already full, or if there was only room to
		 *         write the length header, or if this Completor's blob was
		 *         already completely consumed).
		 * @throws IOException
		 *                 if the underlying channel throws IOException during a
		 *                 write attempt.
		 */
		// the assymmetry between where exceptions bubble out from the blocking and nonblocking versions bothers me a bit.
		public int write() throws IOException {
			if ($blob == null) return -1;	// it's a strawman
			int $v = 0;
			if ($preint.remaining() > 0) $v += $chan.write($preint);	// we might have done this already in a previous invocation, but so?  the buffer remembers how much is remaining.
			//ohgod: even if you're treating this channel single-threadedly in the jvm, the *kernel* is still draining at random, so you can't just happily jump onto trying to write the blob and assume that if the preint didn't finish writing then the blob will also fail properly.
			if ($preint.remaining() > 0) return $v;
			$v += $chan.write($blob);
			return $v;
		}
		
		/**
		 * Determines whether this Completor is completely consumed.
		 * 
		 * @return true if there is no more data awaiting a chance to be written
		 *         to channel; false otherwise.
		 */
		public boolean isComplete() {
			if ($blob == null) return true;	// it's a strawman
			return ($blob.remaining() == 0);
		}
	}
}

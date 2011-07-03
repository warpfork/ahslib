package us.exultant.ahs.io;

import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

/**
 * <p>
 * This actually 'translates' bytes onto a communication channel. It follows the
 * dead-simple "babble" protocol: when given a series of arbitrary blobs, the bytes that
 * end up in the channel look like a series of 4-byte signed integer lengths, followed by
 * that blob of the size specified by the int.
 * </p>
 * 
 * <p>
 * Two different implementations of this are provided as nested classes; one does a
 * significantly better job of maintaining its promise to be nonblocking (but at the cost
 * of greater complexity for the client code), while the other is simpler to use but may
 * sometimes block.
 * </p>
 * 
 * @author hash
 * 
 */
public abstract class TranslatorByteBufferToChannel {
	/**
	 * <p>
	 * This implementation of translating writes to channels is nonblocking. It
	 * returns an object (a {@link Completor}) that is responsible for the given blob;
	 * this object can be repeatedly invoked to nonblockingly write as much of the
	 * blob as immediately possible until the blob is consumed. If several such
	 * {@link Completor}s are produced regarding the same {@link WritableByteChannel},
	 * as long as they are all consumed completely after their first
	 * {@link Completor#write} call without writes called on any other Completors in
	 * the meanwhile, everything works out fine.
	 * </p>
	 * 
	 * <p>
	 * (This class bears more resemblance to {@link TranslatorChannelToByteBuffer}
	 * than does the {@link Blocking} implementation; TranslatorChannelToByteBuffer is
	 * just able to do a better job of hiding its equivalent of {@link Completor}
	 * because {@link TranslatorStack} allows it to return nulls if it isn't finished
	 * with an entire semantic read (whereas {@link TranslatorStack} unfortunately has
	 * no such route for permissiveness of incomplete writes).)
	 * </p>
	 * 
	 * @author hash
	 * 
	 */
	public static class Nonblocking implements Translator<ByteBuffer,Nonblocking.Completor> {
		public static class Completor {
			
			public int write() {
				return -1;
			}
			
			public boolean isComplete() {
				return false;
			}
		}
		
		public Completor translate(ByteBuffer $blob) throws TranslationException {
			return null;
			
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
	 * It is NOT possible to use the same instance of a TranslatorByteBufferToChannel
	 * in several TranslationStack for any purpose if threads are involved.
	 * </p>
	 * 
	 * <p>
	 * (A note about some of the reasons why this implementation is not completely
	 * nonblocking: it quite simply doesn't turn out anywhere near so elegant as
	 * reading. You end up having to pass in the same thing several times, or else
	 * having an alternate triggering mechanism, neither of which concepts play even
	 * remotely nicely with the whole idea of making translator stacks. Thus, if you
	 * want this nonblocking, you have to go out of your way to specify that by using
	 * a different class entirely, since despite the fact that such a class might also
	 * implements the same Translator interface, it would really screw with a lot of
	 * the normal concepts of Translator.)
	 * </p>
	 * 
	 * @author hash
	 * 
	 */
	public static class Blocking implements Translator<ByteBuffer,WritableByteChannel> {
		public Blocking(WritableByteChannel $base) {
			this.$base = $base;
		}
		
		private final WritableByteChannel	$base;
		private final ByteBuffer		$preint	= ByteBuffer.allocate(4);
		
		public WritableByteChannel translate(ByteBuffer $blob) throws TranslationException {
			$preint.clear();
			$preint.putInt($blob.remaining());
			$preint.rewind();
			subwrite($preint);
			subwrite($blob);
			return $base;
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
}

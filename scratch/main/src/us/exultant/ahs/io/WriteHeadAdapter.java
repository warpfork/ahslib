package us.exultant.ahs.io;

import us.exultant.ahs.io.ReadHeadAdapter.*;
import us.exultant.ahs.io.ReadHeadAdapter.Channelwise.*;
import us.exultant.ahs.util.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public abstract class WriteHeadAdapter<$T> implements WriteHead<$T> {
	public static <$T> WriteHead<$T> make(WritableByteChannel $wbc, Translator<$T, InfallibleWritableByteChannel> $ts) {
		return new ChannelwiseUnbuffered<$T>($wbc, $ts);
	}
	
	//TODO:AHS: someday write more factories here that accept PumperSelector as an arg and do fully nonblocking writes with buffering and etc.
	
	
	protected WriteHeadAdapter() {
		$eh = ExceptionHandler.STDERR_IOEXCEPTION;	// notice this default.  it's not silent.
	}
	
	protected ExceptionHandler<IOException>	$eh;
	
	public void setExceptionHandler(ExceptionHandler<IOException> $eh) {
		this.$eh = $eh;
	}
	
	
	
	
	
	/**
	 * <p>
	 * Warn: This class isn't actually 100% nonblocking. If your WritableByteChannel
	 * happens to be based on, say, a TCP stream, and your TCP buffer fills up, write
	 * calls WILL block the thread that called the write.
	 * </p>
	 * 
	 * <p>
	 * There is a reason for this. The way that select is implemented in java, if you
	 * register interest in write events, you get signals every single moment that the
	 * TCP buffer (in this example) isn't absolutely full. This can actually lead to
	 * performance problems. It's possible to make an implementation that flicks the
	 * write-interest on and off every time it handles a write request, but those
	 * operations require thread-safe queueing with the selector, and this system
	 * honestly works fine in practice unless you're queueing gigs of data all in one
	 * go.
	 * </p>
	 * 
	 * @author hash
	 * 
	 * @param <$T>
	 */
	public static class ChannelwiseUnbuffered<$T> extends WriteHeadAdapter<$T> {
		public ChannelwiseUnbuffered(WritableByteChannel $wbc, Translator<$T, InfallibleWritableByteChannel> $ts) {
			super();
			$trans = $ts;
			$iwbc = new InfallibleWritableByteChannel($wbc, new ExceptionHandler<IOException>() {
				public void hear(IOException $e) {
					ExceptionHandler<IOException> $dated_eh = $eh;
					$iwbc.close();	//XXX:AHS: i'm not actually sure that we should always close the underlying channel when it screams at us.  on the other hand, that is what most of the java.io stuff does.
					if ($dated_eh != null) $dated_eh.hear($e);
				}
			});
			
			// icky
			if ($ts instanceof TranslatorStack)
				for (Translator<?,?> $t : ((TranslatorStack<?,?>)$ts).expose())
					if ($t instanceof BabbleTranslator)
						((BabbleTranslator)$t).setBase($iwbc);
		}
		
		private final Translator<$T,InfallibleWritableByteChannel>	$trans;
		private final InfallibleWritableByteChannel			$iwbc;
		
		public void write($T $chunk) throws TranslationException {
			synchronized ($trans) {
				$trans.translate($chunk);
			}
		}
		
		public void writeAll(Collection<? extends $T> $chunks) throws TranslationException {
			synchronized ($trans) {
				for ($T $chunk : $chunks)
					write($chunk);
			}
		}
		
		public boolean hasRoom() {
			return true;
		}
		
		public boolean isClosed() {
			return !$iwbc.isOpen();
		}
		
		public void close() {
			$iwbc.close();
		}
		
		
		
		
		
		public static interface ChunkBuilder<$CHUNK> extends Translator<$CHUNK,InfallibleWritableByteChannel> {
			public InfallibleWritableByteChannel translate($CHUNK $m) throws TranslationException;
		}
		
		/**
		 * <p>
		 * This actually 'translates' bytes onto a communication channel. The
		 * bytes that end up in the channel look like a series of 4-byte signed
		 * integer lengths, followed by an arbitrary blob of the size specified by
		 * the int.
		 * </p>
		 * 
		 * <p>
		 * It is NOT possible to use the same instance of a BabbleTranslator in
		 * several TranslationStack for any purpose if threads are involved. Since
		 * a BabbleTranslator has no way to enforce the atomicity of some of its
		 * internal operations without becoming a blocking system, it must keep
		 * state.
		 * </p>
		 * 
		 * <p>
		 * Unfortunately, this is a little less elegant than its counterpart
		 * ReadHeadAdapter.Channelwise.BabbleTranslator -- it must be set up with
		 * the channel it is to be writing to. This is initialized via a protected
		 * method instead of via the constructor because the initialization is
		 * done when the BabbleTranslator is installed into a
		 * WriteHeadAdapter.ChannelwiseUnbuffered in order to hide
		 * unnecessary/redundant interfaces from users of the library.
		 * </p>
		 * 
		 * @author hash
		 * 
		 */
		// also, the reason that these exist as tied to their underlying channels instead of just outputting another bytebuffer is because i don't want to do copies of the whole blob -- which can be pretty massive -- just to put that four-byte header on their and they copy it all again to the wire.
		// well, and also because it really has to be that way in the reading varient, because the whole point of the the babble layer is to give you a primitive chunk size control.
		public static class BabbleTranslator implements ChunkBuilder<ByteBuffer> {
			public BabbleTranslator() {}
			
			// i HATE this method.  it's really, really hard to even get access at it since this class usually gets hidden within a TranslatorStack.
			// i'd hate it slightly less if i could at least put it in the ChunkBuilder interface, but then i'd have to make it public, which... ugh.
			protected void setBase(InfallibleWritableByteChannel $base) {
				if ($iwbc != null) throw new MajorBug(new IllegalStateException("Attempted to reset base channel.  This may be because someone tried to recycle a BabbleTranslator, which is not a good idea."));
				$iwbc = $base;
			}
			
			private final ByteBuffer		$preint	= ByteBuffer.allocate(4);
			private InfallibleWritableByteChannel	$iwbc;	// effectively final.  but if, god forbid, you get multiple attempts to set it from multiple threads... well, you're in hell already, so i'm not going to try to help you.
			
			public InfallibleWritableByteChannel translate(ByteBuffer $blob) throws TranslationException {
				$preint.clear();
				$preint.putInt($blob.remaining());
				$preint.rewind();
				subwrite($preint);
				subwrite($blob);
				return $iwbc;
			}
			
			private void subwrite(ByteBuffer $lit) {
				$iwbc.write($lit);
				while ($lit.remaining() > 0) {
					X.chill(7);	// this is the dirty hack.
					$iwbc.write($lit);
				}
			}
		}
	}
	
	/**
	 * Hides all exceptions from the client, rerouting them elsewhere.
	 * IOException thrown from the write method causes the method to return 0;
	 * if the ExceptionHandler doesn't do something in response to the
	 * exception when it gets it (like simply closing the channel), it's quite
	 * likely that mess will result.
	 */
	private static class InfallibleWritableByteChannel implements WritableByteChannel {
		private InfallibleWritableByteChannel(WritableByteChannel $bc, ExceptionHandler<IOException> $eh) {
			this.$bc = $bc;
			this.$eh = $eh;
		}
		
		private ExceptionHandler<IOException>	$eh;
		private WritableByteChannel		$bc;
		
		public void close() {
			try {
				$bc.close();
			} catch (IOException $ioe) {
				$eh.hear($ioe);
			}
		}
		
		public boolean isOpen() {
			return $bc.isOpen();
		}
		
		public int write(ByteBuffer $dst) {
			try {
				return $bc.write($dst);
			} catch (IOException $ioe) {
				$eh.hear($ioe);
				return -1;
			}
		}
	}
}

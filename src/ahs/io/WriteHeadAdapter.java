package ahs.io;

import ahs.util.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public abstract class WriteHeadAdapter<$T> implements WriteHead<$T> {
	public static <$T> WriteHead<$T> make(WritableByteChannel $wbc, Translator<$T, ByteBuffer> $ts) {
		return new Channelwise<$T>($wbc, (Channelwise.ChunkBuilder<$T>)$ts);
	}
	
	//TODO:AHS: someday write more factories here that accept PumperSelector as an arg and do fully nonblocking writes with buffering and etc.
	
	
	

	protected ExceptionHandler<IOException>	$eh;
	
	public void setExceptionHandler(ExceptionHandler<IOException> $eh) {
		this.$eh = $eh;
	}
	
	
	
	
	
	public static class Channelwise<$T> extends WriteHeadAdapter<$T> {
		public Channelwise(WritableByteChannel $wbc, ChunkBuilder<$T> $ts) {
			$trans = $ts;
			$preint	= ByteBuffer.allocate(4);
			$iwbc = new InfallibleWriteableByteChannel($wbc, new ExceptionHandler<IOException>() {
				public void hear(IOException $e) {
					ExceptionHandler<IOException> $dated_eh = $eh;
					$iwbc.close();	// this could kinda loop, but the method shouldn't KEEP throwing exceptions.
					if ($dated_eh != null) $dated_eh.hear($e);
				}
			});
		}
		
		private final ChunkBuilder<$T>			$trans;
		private final ByteBuffer			$preint;
		private final InfallibleWriteableByteChannel	$iwbc;
		
		public void write($T $chunk) throws IOException {
			ByteBuffer $bats = $trans.translate($chunk);
			$preint.clear();
			$preint.putInt($bats.remaining());
			$preint.rewind();
			subwrite($preint);
			subwrite($bats);
		}
		
		private void subwrite(ByteBuffer $lit) {
			$iwbc.write($lit);
			while ($lit.remaining() > 0) {
				X.chill(7);
				$iwbc.write($lit);
			}
		}
		
		public void writeAll(Collection<? extends $T> $chunks) throws IOException {
			for ($T $chunk : $chunks)
				write($chunk);
		}
		
		public boolean hasRoom() {
			return true;
		}
		
		public boolean isClosed() {
			return !$iwbc.isOpen();
		}
		
		public void close() throws IOException {
			$iwbc.close();
		}
	}
	
	
	// both the first entry in a TranslatorStack as well as the entire stack itself should be able to implement this, really.
	public static interface ChunkBuilder<$CHUNK> extends Translator<$CHUNK, ByteBuffer> {
		public ByteBuffer translate($CHUNK $m) throws TranslationException;
	}
	
	/**
	 * Hides all exceptions from the client, rerouting them elsewhere.
	 * IOException thrown from the write method causes the method to return 0;
	 * if the ExceptionHandler doesn't do something in response to the
	 * exception when it gets it (like simply closing the channel), it's quite
	 * likely that mess will result.
	 */
	private static class InfallibleWriteableByteChannel implements WritableByteChannel {
		private InfallibleWriteableByteChannel(WritableByteChannel $bc, ExceptionHandler<IOException> $eh) {
			$bc = $bc;
			$eh = $eh;
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

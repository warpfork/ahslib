package us.exultant.ahs.io;

import us.exultant.ahs.core.*;
import java.io.*;
import java.nio.channels.*;

public class TranslatorChannelToSocket implements Translator<ServerSocketChannel,SocketChannel> {
	/** Since TranslatorChannelToSocket's {@link #translate(ServerSocketChannel)} method is completely reentrant, a single instance of this may be used with impunity throughout the VM. */
	public static final TranslatorChannelToSocket instance = new TranslatorChannelToSocket();
	
	public SocketChannel translate(ServerSocketChannel $ssc) throws TranslationException {
		try {
			SocketChannel $chunk = $ssc.accept();
			if ($chunk == null) return null;
			$chunk.configureBlocking(false);
			return $chunk;
		} catch (IOException $e) {
			throw new TranslationException($e);
		}
	}
}

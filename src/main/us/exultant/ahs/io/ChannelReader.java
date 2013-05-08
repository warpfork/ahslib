package us.exultant.ahs.io;

import us.exultant.ahs.core.*;
import java.io.*;
import java.nio.channels.*;

/**
 * Interface which is implemented at the bottom of an IO stack and is responsible for
 * framing individual messages. For example, there are implementations of this that do
 * line-break delimited frames, literal binary "frames", implementations that produce HTTP
 * requests or responses, implementations that do a naked passthru, etc.
 *
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 *
 * @param <$MSG>
 *                the type of message we accept. {@link java.nio.ByteBuffer} is a common
 *                choice.
 */
public interface ChannelReader<$MSG> {
	/**
	 * <p>
	 * Reads a message from a byte channel based on this reader's concept of a message
	 * frame.
	 * </p>
	 *
	 * <p>
	 * This method either returns an object of the message type, or it may return null
	 * for any given invocation if there is not immediately (i.e. in a nonblocking
	 * sense) sufficient readable data to produce a full message.
	 * </p>
	 *
	 * @param $channel
	 * @return a message or null
	 * @throws IOException
	 *                 if the problems are encountered while trying to read from the
	 *                 channel; this is probably a fairly fatal scenario for this
	 *                 channel.
	 * @throws TranslationException
	 *                 if a message was corrupt or nonsensical. Depending on the
	 *                 protocol framing type, future messages on the same channel may
	 *                 still be readable.
	 */
	public $MSG read(ReadableByteChannel $channel) throws IOException, TranslationException;



	//public static class NoopFramer implements ChannelReader<$MSG> {}



	//public static class LinedFramer implements ChannelReader<$MSG> {}



	//public static class IfsFramer implements ChannelReader<$MSG> {}



	//public static class HttpRequestFramer implements ChannelReader<$MSG> {}



	//public static class HttpResponseFramer implements ChannelReader<$MSG> {}
}

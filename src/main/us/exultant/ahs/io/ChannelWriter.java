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
 *                the type of message we write. {@link java.nio.ByteBuffer} is a common
 *                choice.
 */
public interface ChannelWriter<$MSG> {
	/**
	 * <p>
	 * Writes a message from a byte channel based on this writer's concept of a
	 * message frame.
	 * </p>
	 * 
	 * <p>
	 * This method may fail to write all of the bytes of a message in a single
	 * invocation, in which case the writer shall buffer that message and will attempt
	 * to finish writing it on future calls. In this case, the caller should not call
	 * the method with a new piece of data until the previous piece has been flushed
	 * (if the caller tries to shove too much data in, the behavior may be undefined).
	 * Look at whether the call returns true or false to determine whether the writing
	 * process is hitting a need to buffer.
	 * </p>
	 * 
	 * @param $channel
	 * @return true if the entire last piece of data we were given has been written
	 *         and you can give us another one; false if we still have more to do in a
	 *         future call.
	 * @throws IOException
	 *                 if the problems are encountered while trying to write to the
	 *                 channel; this is probably a fairly fatal scenario for this
	 *                 channel.
	 * @throws TranslationException
	 *                 if a message was corrupt or nonsensical.
	 */
	public boolean write(WritableByteChannel $channel, $MSG $data) throws TranslationException, IOException;
	
	
	
	//public static class NoopFramer implements ChannelWriter<$MSG> {}

	
	
	//public static class LinedFramer implements ChannelWriter<$MSG> {}	// this is actually a just a specific instance of an IFS-oriented framer!
	
	
	
	//public static class IfsFramer implements ChannelWriter<$MSG> {}	// well that would be an odd name, if symmetrical.  but yeah, probs wrong.
	
	
	
	//public static class CsvFramer implements ChannelWriter<String[]> {}	// actually these are probably the wrong level.  they'd be line-framed; CSV/TDV is a translation layer.
	//public static class TdvFramer implements ChannelWriter<String[]> {}	// actually these are probably the wrong level.  they'd be line-framed; CSV/TDV is a translation layer.
	
	
	
	//public static class HttpRequestFramer implements ChannelWriter<$MSG> {}
	
	
	
	//public static class HttpResponseFramer implements ChannelWriter<$MSG> {}
}

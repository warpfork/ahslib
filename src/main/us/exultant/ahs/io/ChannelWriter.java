package us.exultant.ahs.io;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public interface ChannelWriter {
	public int write(WritableByteChannel $channel, ByteBuffer $data) throws IOException;
}

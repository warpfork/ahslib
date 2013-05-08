/*
 * Copyright 2010 - 2013 Eric Myhre <http://exultant.us>
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

package us.exultant.ahs.io.framer;

import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;
import us.exultant.ahs.io.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

/**
 * <p>
 * Reads messages with no frame at all, instead using the closure of the channel itself as
 * the message delimiter. Since this means you can only pass one message per entire
 * InputSystem/connection, this often works great for small config files, but is rarely a
 * very good idea for network protocols unless you for some reason like shortlived
 * sockets.
 * </p>
 *
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 */
public class NoopFrameReader implements ChannelReader<ByteBuffer> {
	/** Accumulator. */
	private final ByteAccumulator	$mess	= new ByteAccumulator();
	/** Per-call read buffer. */
	private final ByteBuffer	$buffer	= ByteBuffer.allocate(1024*1024);
	private static final int MAX_BUFFER_CYCLES_PER_READ = 6;

	public ByteBuffer read(ReadableByteChannel $channel) throws IOException, TranslationException {
		for (int $i = 0; $i < MAX_BUFFER_CYCLES_PER_READ; $i++) {
			$buffer.clear();
			int $progress = $channel.read($buffer);

			if ($progress == -1) {
				$channel.close();
				/* We take the backing array from $mess and wrap it in a ByteBuffer.
				 * This works correctly and saves us a copy, but may also cause us to be using more memory
				 * than strictly necessary until this ByteBuffer is discarded.  So, if this procedes quite
				 * promptly to deserialization (which is a pretty reasonable assumption I feel), this is
				 * a great trade since the extra bytes of unused buffer will be dropped after
				 * deserialization anyway. */
				return ByteBuffer.wrap($mess.getByteArray(), 0, $mess.size());
			} else {
				$mess.write($buffer.array(), 0, $progress);
				if ($buffer.remaining() > 0) break;
			}
		}
		return null;
	}
}

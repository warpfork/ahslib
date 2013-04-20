/*
 * Copyright 2010, 2011 Eric Myhre <http://exultant.us>
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
import us.exultant.ahs.io.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

/**
 * <p>
 * Writes messages with no frame at all, instead using the closure of the channel itself
 * as the message delimiter. Since this means you can only pass one message per entire
 * OutputSystem/connection, this often works great for small config files, but is rarely a
 * very good idea for network protocols unless you for some reason like shortlived
 * sockets.
 * </p>
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 */
public class NoopFrameWriter implements ChannelWriter<ByteBuffer> {
	private ByteBuffer $blob;

	public boolean write(WritableByteChannel $channel, ByteBuffer $data) throws IOException, TranslationException {
		if ($data != null) {
			if ($blob != null) throw new IllegalStateException("you may not push more data into a ChannelWriter that already has data in a buffer!");
			$blob = $data;
			/* I trust that you've already $blob.rewind() if that's what you want. */
		}

		$channel.write($blob);
		if ($blob.remaining() == 0) {
			$blob = null;
			$channel.close();
			return true;
		} else return false;
	}
}

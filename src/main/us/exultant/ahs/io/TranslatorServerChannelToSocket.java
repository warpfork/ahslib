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

package us.exultant.ahs.io;

import us.exultant.ahs.core.*;
import java.io.*;
import java.nio.channels.*;

public class TranslatorServerChannelToSocket implements Translator<ServerSocketChannel,SocketChannel> {
	/**
	 * Since TranslatorChannelToSocket's {@link #translate(ServerSocketChannel)}
	 * method is completely reentrant, a single instance of this may be used with
	 * impunity throughout the VM.
	 */
	public static final TranslatorServerChannelToSocket INSTANCE = new TranslatorServerChannelToSocket();
	
	/**
	 * Attempts to accept a new SocketChannel from the given ServerSocketChannel. If
	 * successful, the returned SocketChannel has been configured to non-blocking
	 * mode; it has not been registered with any selectors.
	 * 
	 * @param $ssc
	 *                a ServerSocketChannel to draw from. If this Translator is to
	 *                function nonblockingly, this must have already been placed in
	 *                nonblocking mode.
	 * @return a new SocketChannel in nonblocking mode if one was available, or null.
	 * @throws TranslationException
	 *                 if any of the operations result in an IOException, in which
	 *                 case it will be the cause of the thrown TranslationException.
	 */
	public SocketChannel translate(ServerSocketChannel $ssc) throws TranslationException {
		try {
			SocketChannel $chunk = $ssc.accept();
			if ($chunk == null) return null;
			$chunk.configureBlocking(false);
			return $chunk;
		} catch (IOException $e) {
			throw new TranslationException("failed to translate a new SocketChannel from a ServerSocketChannel.", $e);
		}
	}
}

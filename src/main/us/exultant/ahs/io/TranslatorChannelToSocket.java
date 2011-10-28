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

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
import us.exultant.ahs.thread.*;
import java.io.*;
import java.util.*;

public abstract class WriteHeadAdapter<$T> implements WriteHead<$T> {
	protected WriteHeadAdapter() {
		$pipe = new Pipe<$T>();
		$eh = null;
	}
	
	protected final Pipe<$T>		$pipe;
	private ExceptionHandler<IOException>	$eh;
	
	public void setExceptionHandler(ExceptionHandler<IOException> $eh) {
		this.$eh = $eh;
	}
	
	public void write($T $chunk) {
		$pipe.SINK.write($chunk);
	}
	
	public void writeAll(Collection<? extends $T> $chunks) {
		$pipe.SINK.writeAll($chunks);
	}
	
	public boolean hasRoom() {
		return $pipe.SINK.hasRoom();
	}
	
	public boolean isClosed() {
		return $pipe.SINK.isClosed();
	}
	
	public abstract void close();	// is abstract because you probably want to close any underlying channels, then have the pump close the pipe close itself when appropriate.
	
	protected void handleException(IOException $e) {
		ExceptionHandler<IOException> $dated_eh = $eh;
		if ($dated_eh != null) $dated_eh.hear($e);		
	}
}

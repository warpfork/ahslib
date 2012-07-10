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
import java.util.concurrent.*;

/**
 * Provides a ReadHead backed by a buffer which is only writable by subclasses and exposes
 * a Pump and option to designate ExceptionHandler. (It is highly similar to a Teamster,
 * except it also allows the overriding of some special functions needed by the relatively
 * "low-level" code that operates directly with java nio.)
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * 
 * @param <$T>
 */
// this isn't what i intended on several levels.  mainly: pipes are the boundaries between threads.  you set them up first, and you choose where they go as a developer.  they shouldn't just appear without your explicit desire.
//   
public abstract class ReadHeadAdapter<$T> implements ReadHead<$T> {
	protected ReadHeadAdapter() {
		$pipe = new DataPipe<$T>();
		$eh = null;
	}
	
	protected final Pipe<$T>		$pipe;
	private ExceptionHandler<IOException>	$eh;
	
	public void setExceptionHandler(ExceptionHandler<IOException> $eh) {
		this.$eh = $eh;
	}
	
	public void setListener(Listener<ReadHead<$T>> $el) {
		$pipe.source().setListener($el);
	}
	
	public $T read() {
		return $pipe.source().read();
	}
	
	public $T readNow() {
		return $pipe.source().readNow();
	}
	
	public $T readSoon(long $timeout, TimeUnit $unit) {
		return $pipe.source().readNow();
	}
	
	public boolean hasNext() {
		return $pipe.source().hasNext();
	}
	
	public List<$T> readAll() {
		return $pipe.source().readAll();
	}
	
	public List<$T> readAllNow() {
		return $pipe.source().readAllNow();
	}
	
	public boolean isClosed() {
		return $pipe.source().isClosed();
	}
	
	public abstract void close();	// is abstract because you probably want to close any underlying channels, then have the pump close the pipe close itself when appropriate.
	
	public boolean isExhausted() {
		return isClosed() && !hasNext();
	}
	
	protected void handleException(IOException $e) {
		ExceptionHandler<IOException> $dated_eh = $eh;
		if ($dated_eh != null) $dated_eh.hear($e);		
	}
}

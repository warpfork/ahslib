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
 * @author hash
 * 
 * @param <$T>
 */
public abstract class ReadHeadAdapter<$T> implements ReadHead<$T> {
	protected ReadHeadAdapter() {
		$pipe = new Pipe<$T>();
		$eh = null;
	}
	
	protected final Pipe<$T>		$pipe;
	private ExceptionHandler<IOException>	$eh;
	
	public void setExceptionHandler(ExceptionHandler<IOException> $eh) {
		this.$eh = $eh;
	}
	
	public void setListener(Listener<ReadHead<$T>> $el) {
		$pipe.SRC.setListener($el);
	}
	
	public $T read() {
		return $pipe.SRC.read();
	}
	
	public $T readNow() {
		return $pipe.SRC.readNow();
	}
	
	public $T readSoon(long $timeout, TimeUnit $unit) {
		return $pipe.SRC.readNow();
	}
	
	public boolean hasNext() {
		return $pipe.SRC.hasNext();
	}
	
	public List<$T> readAll() {
		return $pipe.SRC.readAll();
	}
	
	public List<$T> readAllNow() {
		return $pipe.SRC.readAllNow();
	}
	
	public boolean isClosed() {
		return $pipe.SRC.isClosed();
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

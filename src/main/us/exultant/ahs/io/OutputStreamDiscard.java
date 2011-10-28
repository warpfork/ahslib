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

import java.io.*;

/**
 * <p>
 * Ever wanted an effective <tt>/dev/null</tt> in portable java? Here ya go.
 * </p>
 * 
 * <p>
 * See also {@link IOForge#silentOutputStream} for a singleton instance of this &mdash
 * there's very little point in ever creating more than one instance of this class, seeing
 * as how it really truly does nothing at all.
 * </p>
 * 
 * @author hash
 * 
 */
public final class OutputStreamDiscard extends OutputStream {
	public OutputStreamDiscard() {
		
	}
	
	
	public void close(){
		; // shh.
	}

	
	public void flush() {
		; // shh.
	}

	public void write(int $arg0) {
		; // shh.
	}

	
	public void write(byte[] $b, int $off, int $len) {
		; // shh.
	}

	
	public void write(byte[] $b) throws IOException {
		; // shh.
	}

	
	public boolean equals(Object $obj) {
		return ($obj instanceof OutputStreamDiscard);
	}

	
	public int hashCode() {
		return 1;
	}
}

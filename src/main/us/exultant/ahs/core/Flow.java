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

package us.exultant.ahs.core;

/**
 * Pairs a source and a sink, typically as in a Socket.
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * 
 */
public interface Flow<$T> {	// or Membrane, or Doublet, or something...
	public static class Basic<$T> implements Flow<$T> {
		public Basic(ReadHead<$T> $src, WriteHead<$T> $sink) {
			SRC = $src;
			SINK = $sink;
		}
		
		public final ReadHead<$T> SRC;
		public final WriteHead<$T> SINK;
		
		public ReadHead<$T> source() {
			return SRC;
		}
		
		public WriteHead<$T> sink() {
			return SINK;
		}
	}
	

	public ReadHead<$T> source();
	public WriteHead<$T> sink();
}

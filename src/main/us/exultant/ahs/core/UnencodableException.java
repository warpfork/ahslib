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

package us.exultant.ahs.core;

/**
 * <p>
 * This class provides type-grouping for all exceptions that arise in the course of
 * attempting to translate an object to an encoded form and it is not possible for the
 * operation to succeed on the object (i.e. when data simply cannot be represented in a
 * given scheme, such as trying to store a double in a field meant for an int, or a
 * infinite number anywhere in a JSON scheme).
 * </p>
 * 
 * <p>
 * UnencodableException should be used where applicable in preference to the more general
 * TranslationException because applications may want to treat UnencodableException events
 * significantly differently (particularly since UnencodableException tend to only crop up
 * when type safety has already failed in some part of the code that should have been
 * better designed before compile time in the first place).
 * </p>
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * 
 */
public class UnencodableException extends TranslationException {
	public UnencodableException() {
		super();
	}
	
	public UnencodableException(String $arg0, Throwable $arg1) {
		super($arg0, $arg1);
	}
	
	public UnencodableException(String $arg0) {
		super($arg0);
	}
	
	public UnencodableException(Throwable $arg0) {
		super($arg0);
	}
}

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

package us.exultant.ahs.util;

/**
 * This exists to differenciate between situations where UnsupportedOperationException
 * means that something isn't supported by some particular implementation because it's
 * undefined or some such: a method throwing an ImBored exception generally means that
 * it's entirely possible to implement that method, but that it wasn't completed due to
 * time constraints.
 * 
 * @author hash
 * 
 */
public class NotYetImplementedException extends UnsupportedOperationException {
	public NotYetImplementedException() {
	}
	
	public NotYetImplementedException(String $message) {
		super($message);
	}
	
	public NotYetImplementedException(Throwable $cause) {
		super($cause);
	}
	
	public NotYetImplementedException(String $message, Throwable $cause) {
		super($message, $cause);
	}
}

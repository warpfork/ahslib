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

import java.io.*;

/**
 * <p>
 * Represents any form of error that arises when attempting to translation between
 * different representations of the same data. For example, most JSONException are
 * actually examples of TranslationException, and are often found as the "cause" of a
 * TranslationException. The same is true of virtually any kind of parsing error.
 * </p>
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * @see UnencodableException
 * 
 */
public class TranslationException extends IOException {
	public TranslationException() {
		super();
	}
	
	public TranslationException(String $arg0) {
		super($arg0);
	}
	
	public TranslationException(Throwable $arg0) {
		super($arg0);
	}
	
	public TranslationException(String $arg0, Throwable $arg1) {
		super($arg0, $arg1);
	}
}

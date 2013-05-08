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

package us.exultant.ahs.thread;

import us.exultant.ahs.core.*;

/**
 * <p>
 * A Pipe is a type of {@link Flow} that refers exclusively to in-process flows, and as a
 * result can be expected to provide more information and operations.
 * </p>
 *
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 *
 */
public interface Pipe<$T> extends Flow<$T> {
	/**
	 * <p>
	 * The minimal amount of entries which can be read immediately without blocking.
	 * </p>
	 *
	 * <p>
	 * (Note that the size method is not provided on the more broad {@link Flow} type
	 * because flows may represent interprocess or network communications in which the
	 * entire concept of size becomes meaningless and unknowable. Since the Pipe type
	 * refers exclusively to in-process flows, it can be expected to provide more
	 * information.)
	 * </p>
	 *
	 *
	 * @return the minimal amount of entries which can be read immediately without
	 *         blocking.
	 */
	// "size" is a TERRIBLE name for this if we're talking about anything other than a trivial readable-immediately-upon-write pipe.  "available" might be a better predicate.  i'd probably have been bothered by this long ago, but it's actually quite rare to need this method anyway.
	public int size();
}

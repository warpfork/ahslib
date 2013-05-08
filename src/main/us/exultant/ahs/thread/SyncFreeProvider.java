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
 * SyncFreeProvider is intended to ease multithreaded programming and help reduce common
 * bottlenecks by providing a unique object for each thread that requests one; this means
 * that each thread requesting an object from a SyncFreeProvider will pay the construction
 * cost for that object once in the lifetime of the thread.
 * </p>
 *
 * <p>
 * This provides almost the exact same semantics as {@link ThreadLocal} (in fact,
 * interally that class is used). The distinction is that this class allows the use of a
 * {@link Factory} pattern instead of requiring subclassing for customization of the
 * default objects.
 * </p>
 *
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 *
 * @param <$T>
 *                The type of object which will be provided.
 */
public class SyncFreeProvider<$T> extends ThreadLocal<$T> {
	/**
	 * Constructs a SyncFreeProvider that constructs new objects for threads via the
	 * make method provided by the Factory instance.
	 *
	 * @param $factory
	 *                The general contract for the SyncFreeProvider class requires
	 *                that this return objects that are not pointer-equals; typically
	 *                this need only be a tiny snippet wrapping a constructor or
	 *                another form of factory pattern.
	 */
	public SyncFreeProvider(Factory<$T> $factory) {
		super();
		$fact = $factory;
	}

	private final Factory<$T> $fact;

	protected final $T initialValue() {
		return $fact.make();
	}
}

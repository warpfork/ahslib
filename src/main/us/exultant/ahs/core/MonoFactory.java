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
 * <p>
 * A MonoFactory produces a single object; if invoked repeatedly, it always returns the
 * same object. This is very similar to the concept of a {@link Factory}, but represents a
 * different contract (i.e., a Factory need not be idempotent, while a MonoFactory
 * instance <b>must</b> be idempotent and must consistently return a pointer to the same
 * object every time it is called).
 * </p>
 * 
 * <p>
 * Typical strategies for implementing a MonoFactory include the following:
 * <ul>
 * <li>implement all of the logic in the MonoFactory's constructor, then cache a single
 * object of the Factory's generic type, always returning that object when {@link #make()}
 * is called
 * <li>expose methods for performing configuration after construction of the MonoFactory;
 * when {@link #make()} is first called, create and cache a single object of the Factory's
 * generic type, then null all the configuration data and have all configurations methods
 * throw {@link IllegalStateException} if called thereafter
 * </ul>
 * The first option is inflexible without resorting to subclassing; the second option is
 * more flexible, but tends to result in substantial boilerplate code.
 * </p>
 * 
 * @author hash
 * 
 * @param <$T>
 */
public interface MonoFactory<$T> extends Factory<$T> {
}

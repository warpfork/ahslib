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

import java.lang.reflect.*;

public class Reflect {
	public static String getObjectName(Object $obj) {
		// this is obviously a medium-janky use of hashcodes.  however, these kind of string are only intended to rough debugging, and if you're ever trying to do something serious with them in the code itself you're a jackass.
		return getShortClassName($obj)+"@"+Strings.padLeftToWidth(Strings.toHex(Primitives.byteArrayFromInt(System.identityHashCode($obj))), '0', 8);
	}
	
	public static String getShortClassName(Object $obj) {
		return getShortClassName($obj.getClass());
	}
	
	public static String getShortClassName(Class<?> $class) {
		return Strings.getPartAfterLast($class.getCanonicalName(),".");
	}
	
	public static void vom(Class<?> $k) {
		X.saye("CLASSN:: "+$k.getCanonicalName());
		for (Constructor<?> $c : $k.getConstructors())
			X.saye("CONSTR:: "+$c.toGenericString());
		for (Type $i : $k.getGenericInterfaces())
			X.saye("INTERF:: "+$i.toString());
	}
}

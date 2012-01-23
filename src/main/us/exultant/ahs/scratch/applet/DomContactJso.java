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

package us.exultant.ahs.scratch.applet;

import java.applet.*;
import netscape.javascript.*;

/**
 * Allows an applet to interact with the DOM it is embedded in via javascript.
 * 
 * @author hash
 * 
 */
public class DomContactJso implements DomContact.Exposure {
	public DomContactJso(Applet $applet) {
		$jso = JSObject.getWindow($applet);
		$precommand = new StringBuffer(1024);
	}
	
	private JSObject	$jso;
	private StringBuffer	$precommand;
	
	/**
	 * <p>
	 * Executes a javascript string in the context of the browser window containing
	 * our applet.
	 * </p>
	 * 
	 * <p>
	 * Note about return types: they're... very ambiguous and not at all convenient.
	 * The author recommends giving up using anything except strings; if you need full
	 * objects or arrays (or even simple things like integers), pass them around as
	 * JSON strings and deal with encoding and decoding on either side.
	 * </p>
	 * 
	 * @see <a
	 *      href="http://docstore.mik.ua/orelly/web/jscript/refp_186.html">Javascript
	 *      - The Definitive Guide</a>
	 * 
	 * @param $strs
	 *                a String or set of Strings which will be concatenated and run as
	 *                javascript.
	 * @return this may return a {@link JSObject}, {@link Boolean}, {@link Double}, or
	 *         {@link String}. If multiple javascript commands were in the strings we
	 *         were given to run, the value returned is from the last of them.
	 */
	public synchronized Object eval(String... $strs) {
		if ($strs.length == 0) return null;
		if ($strs.length == 1) return $jso.eval($strs[0]);
		return $jso.eval(condense($strs));
	}
	private synchronized String condense(String... $strs) {
		$precommand.setLength(0);
		for (String $s : $strs) $precommand.append($s);
		return $precommand.toString();
	}
}

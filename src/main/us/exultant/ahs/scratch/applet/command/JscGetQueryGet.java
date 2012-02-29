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

package us.exultant.ahs.scratch.applet.command;

import us.exultant.ahs.scratch.applet.DomContact.Exposure;

import java.util.*;
import java.util.regex.*;

/**
 * Grabs the "window location" from a browser window and parses the GET part of the it to
 * produce a Map<String,String> that maps keys to values; if the GET contains the same key
 * multiple times, the map will contain the value set in the last instance.
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * 
 */
public class JscGetQueryGet extends JsCommand.Adapter<Map<String,String>> {
	public JscGetQueryGet() {
		// we're pretty easy.
	}
	
	private static final String PAT_AMP = Pattern.quote("&");
	private static final String PAT_EQ = Pattern.quote("=");
	
	protected Map<String,String> execute(Exposure $power) throws Exception {
		String $get = (String)$power.eval("window.location.search.substring(1)");
		String[] $entries = $get.split(PAT_AMP);
		Map<String,String> $map = new HashMap<String,String>();
		for (String $ent : $entries) {
			String[] $split = $ent.split(PAT_EQ, 2);
			if ($split.length == 1)
				$map.put($split[0], "");
			else
				$map.put($split[0], $split[1]);
		}
		return $map;
	}
}

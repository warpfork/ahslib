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

import java.util.*;
import java.util.regex.*;

/**
 * <p>
 * Allows an applet to interact with the DOM it is embedded in via javascript.
 * </p>
 * 
 * <p>
 * Several pieces of groundwork are performed when this is initialized in order to make
 * the browser easier to deal with and make some shorthand functions globally available:
 * <ul>
 * <li>the ID of the first Body tag is set to "body"
 * <li>the ID of the first Head tag is set to "head"
 * <li>creates a div called "main" and adds it to id:body
 * <li>creates a div called "dev" and adds it to id:body (and sets id:dev to invisible
 * before adding it)
 * <li>function {@code gebi} aliases {@code document.getElementById}
 * <li>function {@code gebt} aliases {@code document.getElementsByTagName}
 * <li>function {@code newe} aliases {@code document.createElement}
 * <li>function {@code newt} aliases {@code document.createTextNode}
 * <li>finally, attempts to set the focus to id:main.
 * </ul>
 * The element id:main should be used to contain all the rest of the page &mdash;
 * modifying any elements of the DOM above that can be dangerous, because results are
 * unpredictable if the applet itself is unlinked from the DOM. The element id:dev is
 * intended for use as a debugging output area.
 * </p>
 * 
 * @author hash
 * 
 */
public class DomContact {
	public DomContact(Exposure $exposure) {
		this.$power = $exposure;
		$power.eval("dS = document.createElement('script'); dS.type = 'text/javascript'; dS.innerHTML = \""+CRIT+"\"; document.getElementsByTagName('head')[0].appendChild(dS);");
		$power.eval("gebt('head')[0].id = 'head';");
		$power.eval("gebt('body')[0].id = 'body';");
		$power.eval("dB = newe('div'); dB.id = 'main'; gebi('body').appendChild(dB);");
		$power.eval("dB = newe('div'); dB.id = 'dev'; dB.style.visibility = 'hidden'; gebi('body').appendChild(dB);");
		$power.eval("gebi('main').focus()");
	}
	private static final String CRIT =	"function gebi($id)  {	return document.getElementById($id);		}	\\n" + 
						"function gebt($tag) {	return document.getElementsByTagName($tag);	}	\\n" + 
						"function newe($tag) {	return document.createElement($tag);		}	\\n" + 
						"function newt($txt) {	return document.createTextNode($txt);		}	\\n" + 
						"\\n" +
						"function removeElement(element) {						\\n" + 
						"	if (!element) return false;						\\n" + 
						"	element.parentNode.removeChild(element);				\\n" + 
						"	delete element;								\\n" + 
						"	return true;								\\n" + 
						"}										\\n" + 
						"" + 
						"";
	
	private final Exposure	$power;
	
	/**
	 * <p>
	 * Executes a javascript string in the context of the browser window containing
	 * our applet.
	 * </p>
	 */
	public Object eval(String... $strs) {
		return $power.eval($strs);
	}
	
	/**
	 * Convenience method for setting attributes on objects (useful only for DOM
	 * objects that already have IDs assigned and for values that are strings).
	 */
	public void setAttribute(String $domObjId, String $attrib, String $value) {
		$power.eval("gebi(\"", $domObjId, "\").", $attrib, "=\"", $value, "\";");
	}
	
	/**
	 * Convenience method for getting attributes on objects (useful only for DOM
	 * objects that already have IDs assigned and for values that are strings).
	 */
	public String getAttribute(String $domObjId, String $attrib) {
		return $power.eval("gebi(", $domObjId, ").", $attrib, "\";").toString();
	}
	
	/** Convenience method to set the "innerHTML" of a DOM object. */
	public void setContent(String $domObjId, String $body) {
		setAttribute($domObjId, "innerHTML", $body);
	}
	
	/** Convenience method to get the "innerHTML" of a DOM object. */
	public String getContent(String $domObjId) {
		return getAttribute($domObjId, "innerHTML");
	}
	
	/** Convenience method to append content to the "innerHTML" of a DOM object. */
	public void appendContent(String $domObjId, String $body) {
		eval("gebi(\"", $domObjId, "\").innerHTML+=\"", $body, "\";");
	}

	/** Convenience method to clear all content of a DOM object. */
	public void clearContent(String $domObjId) {
		setAttribute($domObjId, "innerHTML", "");
	}
	
	/**
	 * Grabs the "window location" from a browser window and parses the GET part of
	 * the it to produce a Map<String,String> that maps keys to values; if the GET
	 * contains the same key multiple times, the map will contain the value set in the
	 * last instance.
	 */
	public Map<String,String> getQueryGet() {
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
	private static final String PAT_AMP = Pattern.quote("&");
	private static final String PAT_EQ = Pattern.quote("=");
	
	
	
	public static interface Exposure {
		public Object eval(String... $strs);
	}
}

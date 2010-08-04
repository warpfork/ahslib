package ahs.applet;

import java.applet.*;
import java.util.*;

import netscape.javascript.*;

public class DomContactJso implements DomContact {
	public void init(Applet $applet) {
		if ($jso != null) throw new IllegalStateException("we already did that!");
		$jso = JSObject.getWindow($applet);
		$precommand = new StringBuffer();
		eval("alert('init');");
		insertCrit();
		learnGet();
		//$applet.add(new java.awt.Label("Applet loaded successfully."));
	}
	
	private JSObject	$jso;
	private StringBuffer	$precommand;
	private static final String CRIT = 
		"function gebi($id)  {	return document.getElementById($id);		}	\\n" + 
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
		"" +  
		"" + 
		"" + 
		"" + 
		"" + 
		"" + 
		"" + 
		"" + 
		"" + 
		"" + 
		"" + 
		"" + 
		"" + 
		"";
	private void insertCrit() {
		eval("dS = document.createElement('script'); dS.type = 'text/javascript'; dS.innerHTML = \""+CRIT+"\"; document.getElementsByTagName('head')[0].appendChild(dS);");
		eval("gebt('head')[0].id = 'head';");
		eval("dB = newe('div'); dB.id = 'body'; gebt('body')[0].appendChild(dB);");
		eval("dB = newe('div'); dB.id = 'dev'; gebt('body')[0].appendChild(dB);");
	}
	private void learnGet() {
		String $get = (String)eval("window.location.search.substring(1)");
		String[] $entries = $get.split(";");
		Map<String,String> $map = new HashMap<String,String>();
		for (String $ent : $entries) {
			String[] $split = $ent.split("=", 1);
			if ($split.length == 1)
				$map.put($split[0], "");
			else
				$map.put($split[0], $split[1]);
		}
		$query = Collections.unmodifiableMap($map);
	}
	private Map<String,String> $query;
	
	private synchronized Object eval(String... $strs) {
		$precommand.setLength(0);
		for (String $s : $strs) $precommand.append($s);
		return $jso.eval($precommand.toString());
	}
	
	/**
	 * Sets the value of innerHTML of the specified object in the DOM.
	 * 
	 * @param $id
	 *                the DOM id of the element to affect
	 * @param $body
	 *                will be enclosed in double quotes when evaluated into the page;
	 *                escape accordingly (and do not assume sanitization).
	 */
	public void setContent(String $id, String $body) {
		//eval("gebi('",$id,"').innerHTML=\"",$body,"\";");
		setAttribute($id, "innerHTML", $body);
	}
	
	/**
	 * Returns the current value of innerHTML of the specified object in the DOM.
	 * 
	 * @param $id
	 *                the DOM id of the element to affect
	 */
	public String getContent(String $id) {
		//return eval("gebi('",$id,"').innerHTML;")+"";
		return getAttribute($id, "innerHTML");
	}
	
	/**
	 * @param $id
	 *                the DOM id of the element to affect
	 * @param $body
	 *                will be enclosed in double quotes when evaluated into the page;
	 *                escape accordingly (and do not assume sanitization).
	 */
	public void appendContent(String $id, String $body) {
		eval("gebi('",$id,"').innerHTML+=\"",$body,"\";");
	}
	
	/**
	 * @param $id
	 *                the DOM id of the element to affect
	 */
	public void clearContent(String $id) {
		eval("gebi('",$id,"').innerHTML='';");
	}
	
	/**
	 * Returns the current value of the attribute specified as a String, or an empty
	 * string if the attribute is not specified.
	 * 
	 * @param $id
	 *                the DOM id of the element to affect
	 * @param $key
	 *                the property name to examine
	 */
	public String getAttribute(String $id, String $key) {
		return eval("gebi('",$id,"').",$key,";")+"";
	}
	
	/**
	 * Returns the current value of the attribute specified as a String, or an empty
	 * string if the attribute is not specified.
	 * 
	 * @param $id
	 *                the DOM id of the element to affect
	 * @param $key
	 *                the property name to examine
	 * @param $value
	 *                the value to assign to the property.  will be enclosed in double quotes when evaluated into the page;
	 *                escape accordingly (and do not assume sanitization).
	 */
	public void setAttribute(String $id, String $key, String $value) {
		eval("gebi('",$id,"').",$key,"=\"",$value,"\";");
	}
}

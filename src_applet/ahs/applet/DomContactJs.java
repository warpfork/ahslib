package ahs.applet;

import ahs.util.*;

import java.applet.*;
import java.util.*;
import java.util.regex.*;

/**
 * Decorates any JsContact to become a full DomContact.
 * 
 * @author hash
 *
 */
public class DomContactJs implements DomContact {
	public DomContactJs(JsContact $jsc) {
		this.$jsc = $jsc;
		insertCrit();
		learnGet();
	}
	
	private JsContact $jsc;
	
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
		"";
	private void insertCrit() {
		eval("dS = document.createElement('script'); dS.type = 'text/javascript'; dS.innerHTML = \""+CRIT+"\"; document.getElementsByTagName('head')[0].appendChild(dS);");
		eval("gebt('head')[0].id = 'head';");
		eval("gebt('body')[0].id = 'body';");
		eval("dB = newe('div'); dB.id = 'main'; gebi('body').appendChild(dB);");
		eval("dB = newe('div'); dB.id = 'dev'; dB.style.visibility = 'hidden'; gebi('body').appendChild(dB);");
		eval("gebi('main').focus()");
	}
	private static final String LG_AND = Pattern.quote("&");
	private static final String LG_EQ = Pattern.quote("=");
	private void learnGet() {
		String $get = (String)eval("window.location.search.substring(1)");
		String[] $entries = $get.split(LG_AND);
		Map<String,String> $map = new HashMap<String,String>();
		for (String $ent : $entries) {
			String[] $split = $ent.split(LG_EQ, 2);
			if ($split.length == 1)
				$map.put($split[0], "");
			else
				$map.put($split[0], $split[1]);
		}
		$query = Collections.unmodifiableMap($map);
	}
	private Map<String,String> $query;
	
	
	
	public Object eval(String... $strs) {
		return $jsc.eval($strs);
	}
	
	public Map<String,String> getQueryMap() {
		return $query;
	}
	
	public void setContent(String $id, String $body) {
		setAttribute($id, "innerHTML", $body);
	}
	
	public String getContent(String $id) {
		return getAttribute($id, "innerHTML");
	}
	
	public void appendContent(String $id, String $body) {
		eval("gebi('",$id,"').innerHTML+=\"",$body,"\";");
	}
	
	public void clearContent(String $id) {
		setAttribute($id, "innerHTML", "");	//XXX:AHS:APPLET this might actually need to be smarter to let browser's gc happen.  not sure.
	}
	
	public String getAttribute(String $id, String $key) {
		return eval("gebi('",$id,"').",$key,";")+"";
	}
	
	public void setAttribute(String $id, String $key, String $value) {
		eval("gebi('",$id,"').",$key,"=\"",$value,"\";");
	}
}

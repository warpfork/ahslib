package ahs.applet;

import java.applet.*;
import java.util.*;

import netscape.javascript.*;

public class DomContactJso implements DomContact {
	public void init(Applet $applet) {
		$jso = JSObject.getWindow($applet);
		$precommand = new StringBuffer();
		eval("alert('init');");
		insertCrit();
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
	}
	private synchronized Object eval(String... $strs) {
		$precommand.setLength(0);
		for (String $s : $strs) $precommand.append($s);
		return $jso.eval($precommand.toString());
	}
	
	/**
	 * @param $ID
	 * @param $body
	 *                will be enclosed in double quotes when evaluated into the page;
	 *                escape accordingly (and do not assume sanitization).
	 */
	public void setContent(String $ID, String $body) {
		eval("gebi('",$ID,"').innerHTML=\"",$body,"\";");
	}
	public String getContent(String $ID) {
		return eval("gebi('",$ID,"').innerHTML;")+"";
	}
	public void appendContent(String $ID, String $body) {
		eval("gebi('",$ID,"').innerHTML+=\"",$body,"\";");
	}
	public void clearContent(String $ID) {
		eval("gebi('",$ID,"').innerHTML='';");
	}
	
	public String getAttribute(String $ID, String $key) {
		return eval("gebi('",$ID,"').",$key,";")+"";	// this hack is actually safer than toString. :/
	}
	public void setAttribute(String $ID, String $key, String $value) {
		eval("gebi('",$ID,"').",$key,"=\"",$value,"\";");
	}
}

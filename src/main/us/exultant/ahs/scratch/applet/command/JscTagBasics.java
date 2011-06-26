package us.exultant.ahs.scratch.applet.command;

import us.exultant.ahs.scratch.applet.DomContact.Exposure;

/**
 * Sets the ID of the first Body tag to "body" and the ID of the first Head tag to "head";
 * creates two divs and puts them in id:body called "main" and "dev" (and sets id:dev to
 * invisible before adding it); adds the gebi, gebt, newe, and newt functions to the
 * world; and finally attempts to set the focus on to id:main.
 * 
 * @author hash
 * 
 */
public class JscTagBasics extends JsCommand.Adapter<Object> {
	public JscTagBasics() {
		// we're easy
	}
	protected Object execute(Exposure $power) throws Exception {
		$power.eval("dS = document.createElement('script'); dS.type = 'text/javascript'; dS.innerHTML = \""+CRIT+"\"; document.getElementsByTagName('head')[0].appendChild(dS);");
		$power.eval("gebt('head')[0].id = 'head';");
		$power.eval("gebt('body')[0].id = 'body';");
		$power.eval("dB = newe('div'); dB.id = 'main'; gebi('body').appendChild(dB);");
		$power.eval("dB = newe('div'); dB.id = 'dev'; dB.style.visibility = 'hidden'; gebi('body').appendChild(dB);");
		$power.eval("gebi('main').focus()");
		return null;	// yarly
	}
	
	private static final String CRIT = 	// I assume everyone can love a function called "gebi".
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
}

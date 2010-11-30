package ahs.applet;

import ahs.applet.command.*;

import java.applet.*;
import java.util.regex.*;

import netscape.javascript.*;

public class DomContactJso implements DomContact {
	public DomContactJso(Applet $applet) {
		$jso = JSObject.getWindow($applet);
		$precommand = new StringBuffer();
	}
	
	private JSObject	$jso;
	private StringBuffer	$precommand;
	
	public synchronized Object execute(JsCommand $jsc) {
		return eval($jsc.toEvalString());
	}
	
	public synchronized Object eval(String... $strs) {
		return $jso.eval(condense($strs));
	}
	private static final String SSN = Matcher.quoteReplacement("\\n");
	private synchronized String condense(String... $strs) {
		$precommand.setLength(0);
		for (String $s : $strs) $precommand.append($s.replaceAll("\n", SSN));
		//FIXME:AHS:APPLET: the above replace stuff deals with issues arrising from println stuff, and it's extremely dubious whether or not it should go here.
		return $precommand.toString();
	}
}

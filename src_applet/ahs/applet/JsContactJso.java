package ahs.applet;

import java.applet.*;
import java.util.regex.*;

import netscape.javascript.*;

public class JsContactJso implements JsContact {
	public JsContactJso(Applet $applet) {
		$jso = JSObject.getWindow($applet);
		$precommand = new StringBuffer();
	}
	
	private JSObject	$jso;
	private StringBuffer	$precommand;
	
	public synchronized Object eval(String... $strs) {
		return $jso.eval(condense($strs));
	}
	private static final String SSN = Matcher.quoteReplacement("\\n");
	private synchronized String condense(String... $strs) {
		$precommand.setLength(0);
		for (String $s : $strs) $precommand.append($s.replaceAll("\n", SSN));	// this replace stuff deals with issues arrising from println stuff, and it's dubious whether or not it should go here
		return $precommand.toString();
	}
}

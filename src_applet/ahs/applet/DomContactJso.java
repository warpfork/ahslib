package ahs.applet;

import ahs.applet.command.*;

import java.applet.*;
import java.util.regex.*;

import netscape.javascript.*;

public class DomContactJso implements DomContact.Exposure {
	public DomContactJso(Applet $applet) {
		$jso = JSObject.getWindow($applet);
		$precommand = new StringBuffer(1024);
	}
	
	private JSObject	$jso;
	private StringBuffer	$precommand;
	
	public synchronized Object eval(String... $strs) {
		if ($strs.length == 0) return null;
		if ($strs.length == 1) $jso.eval($strs[0]);
		return $jso.eval(condense($strs));
	}
	private synchronized String condense(String... $strs) {
		$precommand.setLength(0);
		for (String $s : $strs) $precommand.append($s);
		return $precommand.toString();
	}
	
	
	
	//private static final String SSN = Matcher.quoteReplacement("\\n");
	//private synchronized String condense(String... $strs) {
	//	$precommand.setLength(0);
	//	for (String $s : $strs) $precommand.append($s.replaceAll("\n", SSN));
	//	//FIX-ME:AHS:APPLET: the above replace stuff deals with issues arrising from println stuff, and it's extremely dubious whether or not it should go here.
	//	return $precommand.toString();
	//}
}

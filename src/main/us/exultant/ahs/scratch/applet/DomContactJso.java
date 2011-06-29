package us.exultant.ahs.scratch.applet;

import java.applet.*;
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
		if ($strs.length == 1) return $jso.eval($strs[0]);
		return $jso.eval(condense($strs));
	}
	private synchronized String condense(String... $strs) {
		$precommand.setLength(0);
		for (String $s : $strs) $precommand.append($s);
		return $precommand.toString();
	}
}

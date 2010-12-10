package ahs.applet.command;

import ahs.applet.DomContact.Exposure;

public class JscSetAttribute extends JsCommand.Adapter<Object> {
	public JscSetAttribute(String $domObjId, String $attrib, String $value) {
		this.$domObjId = $domObjId;
		this.$attrib = $attrib;
		this.$value = $value;
	}
	
	public String $domObjId;
	public String $attrib;
	public String $value;

	protected Object execute(Exposure $power) throws Exception {
		$power.eval("document.getElementById(", $domObjId, ").", $attrib, "=\"", $value, "\";");
		return null;	// yarly
	}
}

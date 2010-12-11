package ahs.applet.command;

import ahs.applet.DomContact.Exposure;

public class JscGetAttribute extends JsCommand.Adapter<Object> {
	public JscGetAttribute(String $domObjId, String $attrib, String $value) {
		this.$domObjId = $domObjId;
		this.$attrib = $attrib;
	}
	
	private String $domObjId;
	private String $attrib;

	protected Object execute(Exposure $power) throws Exception {
		return $power.eval("document.getElementById(", $domObjId, ").", $attrib, "\";");
	}
}

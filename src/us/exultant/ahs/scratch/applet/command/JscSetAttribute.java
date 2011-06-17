package us.exultant.ahs.scratch.applet.command;

import us.exultant.ahs.scratch.applet.DomContact.Exposure;

public class JscSetAttribute extends JsCommand.Adapter<Object> {
	public JscSetAttribute(String $domObjId, String $attrib, String $value) {
		this.$domObjId = $domObjId;
		this.$attrib = $attrib;
		this.$value = $value;
	}
	
	private String $domObjId;
	private String $attrib;
	private String $value;

	protected Object execute(Exposure $power) throws Exception {
		$power.eval("document.getElementById(\"", $domObjId, "\").", $attrib, "=\"", $value, "\";");
		return null;	// yarly
	}
}

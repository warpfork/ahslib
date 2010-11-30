package ahs.applet.command;

public class JscSetAttribute implements JsCommand {
	public JscSetAttribute(String $domObjId, String $attrib, String $value) {
		this.$domObjId = $domObjId;
		this.$attrib = $attrib;
		this.$value = $value;
	}
	
	public String $domObjId;
	public String $attrib;
	public String $value;
	
	public static String compose(String $domObjId, String $attrib, String $value) {
		return "document.getElementById("+$domObjId+")."+$attrib+"=\""+$value+"\";";
	}
	
	public String toEvalString() {
		return compose($domObjId, $attrib, $value);
	}
}

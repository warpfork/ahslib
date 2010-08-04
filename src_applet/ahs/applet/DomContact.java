package ahs.applet;

import java.applet.*;

/**
 * The "<code>$id</code>
 * " string arguments to all these methods should match the DOM's concept of "
 * <code>id</code>
 * ".  The root pane intended for content insertion should be accessible by the id "
 * <code>body</code>"; this object need not necessarily be the actual <code>body</code>
 * tag (and in fact in practice probably shouldn't be; overwritting the applet itself is
 * dangerously error-prone).
 * 
 * @author hash
 * 
 */
public interface DomContact {
	public void init(Applet $applet);
	
	public void setContent(String $id, String $body);
	public String getContent(String $id);
	public void appendContent(String $id, String $body);
	public void clearContent(String $id);
	
	public String getAttribute(String $id, String $key);
	public void setAttribute(String $id, String $key, String $value);
}

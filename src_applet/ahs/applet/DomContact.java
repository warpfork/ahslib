package ahs.applet;

import java.applet.*;
import java.util.*;

/**
 * The "<code>$id</code>
 * " string arguments to all these methods should match the DOM's concept of "
 * <code>id</code>".
 * 
 * The body tag should be accessible by the id "<code>body</code>";
 * 
 * The applet itself should be accessible by the id "<code>app</code>";
 * 
 * The element intended for content insertion on this page should be accessible by the id
 * " <code>body</code>" and must be an immediate child of the body (this is to avoid
 * situations involving overwritting the applet itself).
 * 
 * An (initially hidden) element intended for dev logging on this page should be
 * accessible by the id "<code>dev</code>" and must be an immediate child of the body.
 * 
 * @author hash
 * 
 */
public interface DomContact {
	public void init(Applet $applet);
	public String getAppletId();
	
	public Map<String,String> getQueryMap();
	
	public void setContent(String $id, String $body);
	public String getContent(String $id);
	public void appendContent(String $id, String $body);
	public void clearContent(String $id);
	
	public String getAttribute(String $id, String $key);
	public void setAttribute(String $id, String $key, String $value);
}

package ahs.applet;

import java.applet.*;

public interface DomContact {
	public void init(Applet $applet);
	
	public void setContent(String $ID, String $body);
	public String getContent(String $ID);
	public void appendContent(String $ID, String $body);
	public void clearContent(String $ID);
	
	public String getAttribute(String $ID, String $key);
	public void setAttribute(String $ID, String $key, String $value);
}

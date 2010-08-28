package ahs.applet;

import java.applet.*;
import java.util.*;

/**
 * <p>
 * This interface provides a unified basic standard for 
 * </p>
 * 
 * <p>
 * Certain strictures on the meaning of IDs in the DOM should hold true after a DomContact
 * has been initialized:
 * <ul>
 * <li>The "{@code $id}" string arguments to all these methods should match the DOM's
 * concept of "<code>id</code>" (in that if a client adds an element with a new ID
 * directly via script or innerHTML, that element is accessible through this interface
 * without ado).
 * <li>The body tag should be accessible by the ID "<code>body</code>";
 * <li>The applet itself should be accessible by the ID "<code>app</code>";
 * <li>The element intended for content insertion on this page should be accessible by the
 * ID "<code>body</code>" and must be an immediate child of the body (this is to avoid
 * situations involving overwriting the applet itself).
 * <li>An element intended for dev logging on this page (presumably initially hidden)
 * should be accessible by the ID "<code>dev</code>" and must be an immediate child of the
 * body.
 * </ul>
 * </p>
 * 
 * @author hash
 * 
 */
public interface DomContact extends JsContact {

	
	/**
	 * Returns the values of HTTP GET of the containing page.
	 * 
	 * @return a Map of keys to values from the HTTP GET query of the page the applet
	 *         is contained within. i.e. for the page
	 *         <code>example.com/dir/page.ext?key=val&covered=inbees</code>, the map
	 *         would contain <code>{"key" => "value", "covered" => "inbees"}</code>
	 */
	public Map<String,String> getQueryMap();
	
	
	
	/**
	 * Returns the current value of the attribute specified as a String, or an empty
	 * string if the attribute is not specified.
	 * 
	 * @param $id
	 *                the DOM id of the element to affect
	 * @param $key
	 *                the property name to examine
	 */
	public String getAttribute(String $id, String $key);
	
	/**
	 * Returns the current value of the attribute specified as a String, or an empty
	 * string if the attribute is not specified.
	 * 
	 * @param $id
	 *                the DOM id of the element to affect
	 * @param $key
	 *                the property name to examine
	 * @param $value
	 *                the value to assign to the property.  will be enclosed in double quotes when evaluated into the page;
	 *                escape accordingly (and do not assume sanitization).
	 */
	public void setAttribute(String $id, String $key, String $value);
	
	/**
	 * Sets the value of innerHTML of the specified object in the DOM.
	 * 
	 * @param $id
	 *                the DOM id of the element to affect
	 * @param $body
	 *                will be enclosed in double quotes when evaluated into the page;
	 *                escape accordingly (and do not assume sanitization).
	 */
	public void setContent(String $id, String $body);
	
	/**
	 * Returns the current value of innerHTML of the specified object in the DOM.
	 * 
	 * @param $id
	 *                the DOM id of the element to affect
	 */
	public String getContent(String $id);
	
	/**
	 * @param $id
	 *                the DOM id of the element to affect
	 * @param $body
	 *                will be enclosed in double quotes when evaluated into the page;
	 *                escape accordingly (and do not assume sanitization).
	 */
	public void appendContent(String $id, String $body);
	
	/**
	 * @param $id
	 *                the DOM id of the element to affect
	 */
	public void clearContent(String $id);
}

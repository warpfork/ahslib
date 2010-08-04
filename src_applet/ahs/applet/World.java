package ahs.applet;

import ahs.log.*;
import ahs.util.*;
import java.applet.*;
import java.io.*;
import java.util.*;

public class World implements DomContact {
	public World(DomContact $d) {
		$dom = $d;

		DEV_CHANNEL_STDOUT = new PrintStream(new OutputStream() {
			public void write(int $arg) {
				$dom.appendContent(ID_DEV_CHANNEL_STDOUT, (char)$arg+"");
			}
			
			public void write(byte[] b, int off, int len) {
				$dom.appendContent(ID_DEV_CHANNEL_STDOUT, new String(b, off, len, Strings.UTF_8));
			} 
			
			public void write(byte[] b) {
				$dom.appendContent(ID_DEV_CHANNEL_STDOUT, new String(b, Strings.UTF_8));
			} 
		});
		DEV_CHANNEL_STDERR = new PrintStream(new OutputStream() {
			public void write(int $arg) {
				$dom.appendContent(ID_DEV_CHANNEL_STDERR, (char)$arg+"");
			}
			
			public void write(byte[] b, int off, int len) {
				$dom.appendContent(ID_DEV_CHANNEL_STDERR, new String(b, off, len, Strings.UTF_8));
			} 
			
			public void write(byte[] b) {
				$dom.appendContent(ID_DEV_CHANNEL_STDERR, new String(b, Strings.UTF_8));
			} 
		});

		LOG = new Logger(Logger.LEVEL_DEBUG, DEV_CHANNEL_STDERR);
	}
	
	private final DomContact $dom;
	
	public final Logger LOG;
	private static final String ID_DEV_CHANNEL_STDOUT = "dev.channel.stdout";
	private static final String ID_DEV_CHANNEL_STDERR = "dev.channel.stderr";
	public final PrintStream DEV_CHANNEL_STDOUT;
	public final PrintStream DEV_CHANNEL_STDERR;
	private void initializeDevChannels() {
		$dom.appendContent("dev",
				"<hr><div id='"+ID_DEV_CHANNEL_STDOUT+"' style='border:1px solid #000099; white-space: pre-wrap;'></div>"+
				"<hr><div id='"+ID_DEV_CHANNEL_STDERR+"' style='border:1px solid #990000; white-space: pre-wrap;'></div>");
		// wouldn't you know, you don't generally have permission to do this in applets?
		//System.setOut(DEV_CHANNEL_STDOUT);
		//System.setErr(DEV_CHANNEL_STDERR);
	}
	
	private void initializeBodyBetter() {
		setAttribute ("body", "style.margin",		"0");
		setAttribute ("body", "style.padding",		"0");
		setAttribute ("Main", "style.visibility",	"hidden");
		setAttribute ("main", "style.border",		"3px dotted #000000");
		setAttribute ("main", "style.position",		"absolute");
		setAttribute ("main", "style.top",		"0");
		setAttribute ("main", "style.width",		"100%");
	}
	
	public void init(Applet $applet) {
		this.$dom.init($applet);
		initializeDevChannels();
		initializeBodyBetter();
	}
	public Map<String,String> getQueryMap() {
		return this.$dom.getQueryMap();
	}
	public void setContent(String $id, String $body) {
		this.$dom.setContent($id, $body);
	}
	public String getContent(String $id) {
		return this.$dom.getContent($id);
	}
	public void appendContent(String $id, String $body) {
		this.$dom.appendContent($id, $body);
	}
	public void clearContent(String $id) {
		this.$dom.clearContent($id);
	}
	public String getAttribute(String $id, String $key) {
		return this.$dom.getAttribute($id, $key);
	}
	public void setAttribute(String $id, String $key, String $value) {
		this.$dom.setAttribute($id, $key, $value);
	}
}

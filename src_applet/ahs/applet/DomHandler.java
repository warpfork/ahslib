package ahs.applet;

import ahs.util.*;

import java.io.*;
import java.util.*;

public class DomHandler {
	//public void makeDataAvailable();	// can be implemented via the interesting hack of actually embedding in css.  sometimes.  maybe.  images can do it, anyway.  // background: url(data:image/png;base64,iVBORw0KGgoAAAAN...
	//public public List<String> getDataAvailable();
	
	public static void getEmbedableData(String $mime, byte[] $what) {
		// data:application/x-oleobject;base64, ...base64 data...
		StringBuilder $sb = new StringBuilder();
		$sb.append("data:").append($mime).append("base64,");
		$sb.append(Base64.encode($what));
		$sb.toString();
	}
}

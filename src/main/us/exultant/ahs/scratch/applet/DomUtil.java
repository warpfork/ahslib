package us.exultant.ahs.scratch.applet;

import us.exultant.ahs.util.*;

public class DomUtil {
	public static String formatDataForEmbed(String $mime, byte[] $what) {
		// data:application/x-oleobject;base64, ...base64 data...
		StringBuilder $sb = new StringBuilder();
		$sb.append("data:").append($mime).append("base64,");
		$sb.append(Base64.encode($what));
		return $sb.toString();
	}
}

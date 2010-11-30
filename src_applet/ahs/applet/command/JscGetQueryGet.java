package ahs.applet.command;

import java.util.*;
import java.util.regex.*;

public class JscGetQueryGet {
	private static final String PAT_AMP = Pattern.quote("&");
	private static final String PAT_EQ = Pattern.quote("=");
	public Map<String,String> getGet() {
		String $get = (String)eval("window.location.search.substring(1)");
		String[] $entries = $get.split(PAT_AMP);
		Map<String,String> $map = new HashMap<String,String>();
		for (String $ent : $entries) {
			String[] $split = $ent.split(PAT_EQ, 2);
			if ($split.length == 1)
				$map.put($split[0], "");
			else
				$map.put($split[0], $split[1]);
		}
		return $map;
	}
	
	// So some commands would desperately like to be able to return sane data to other parts of the program when eval'd.
	// This is easy only when the js eval can be invoked by the command (as opposed to giving the command to the DomContact and DomContact just eval'ing a string the Command produces).
	//
}

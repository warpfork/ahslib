package ahs.applet.command;

import ahs.applet.DomContact.Exposure;

import java.util.*;
import java.util.regex.*;

public class JscGetQueryGet extends JsCommand.Adapter<Map<String,String>> {
	public JscGetQueryGet() {
		// we're pretty easy.
	}
	
	private static final String PAT_AMP = Pattern.quote("&");
	private static final String PAT_EQ = Pattern.quote("=");
	
	protected Map<String,String> execute(Exposure $power) throws Exception {
		String $get = (String)$power.eval("window.location.search.substring(1)");
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
}

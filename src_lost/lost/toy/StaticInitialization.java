package ahs.lost.toy;

import ahs.util.*;

public class StaticInitialization {
	public static void main(String[] args) {
		X.say("starting.");
		TouchMe.class.equals(null);
		X.say("continuing.");
		new TouchMe();
		TouchMe.touch();
		new TouchMe();
		new TouchMe();
		X.say("done.");
	}
	
	private static class TouchMe {
		static {
			X.say("ooh, i've been touched for the first time");
		}
		
		{
			X.say("i've been touched again");
		}
		
		public static void touch() {};
	}
}

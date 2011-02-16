package ahs.view;

import ahs.util.*;

public class Konsole {
	public static class Color {
		public static final String NO = "\033[0m";
		//public static final String RED = "[38m[K";
		public static final String RED = "\033[38m";
		public static final String GREEN = "\033[32m";
		public static final String CYAN = "\033[36m";
		
		/*
		   30    black foreground
		   31    red foreground
		   32    green foreground
		   33    brown foreground
		   34    blue foreground
		   35    magenta (purple) foreground
		   36    cyan (light blue) foreground
		   37    gray foreground

		   40    black background
		   41    red background
		   42    green background
		   43    brown background
		   44    blue background
		   45    magenta background
		   46    cyan background
		   47    white background

		   0     reset all attributes to their defaults
		   1     set bold
		   4     set underscore
		   5     set blink
		   7     set reverse video
		   22    set normal intensity
		   24    underline off
		   25    blink off
		   27    reverse video off
		*/
	}
	
	public static void main(String[] args) {
		for (int $a = 0; $a < 50; $a++) {
			for (int $b = $a+1; $b < 50; $b++) {
				System.out.print("\033["+$a+";"+$b+"m"+$a+";"+$b+Color.NO+" ");				
			}
			System.out.print("\n");
		}
	}

	public static void out(String $s) {
		System.out.print(Color.CYAN+$s+Color.NO);
	}
	
	public static void err(String $s) {
		System.out.print(Color.RED+$s+Color.NO);
	}
}

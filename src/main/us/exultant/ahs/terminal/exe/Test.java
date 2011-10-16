package us.exultant.ahs.terminal.exe;

import us.exultant.ahs.util.*;
import us.exultant.ahs.terminal.*;
import us.exultant.ahs.terminal.Terminal.Color;
import java.io.*;

public class Test {
	private static final Terminal T = StandardTerminal.get();
	public static void main(String... args) {
		final int $w = T.getWidth();
		final int $h = T.getHeight();
		
		
		
		// make a giant freakin' box
		T.print(BoxChars.NSDN+Strings.repeat(BoxChars.NSNS, $w-2)+BoxChars.NNDS);
		T.cursor().lineNext(1);
		for (int $i = 2; $i < $h-1; $i++) {
			T.print(BoxChars.DNDN+Strings.repeat(' ', $w-2)+BoxChars.DNDN);
			T.cursor().lineNext(1);
		}
		T.print(BoxChars.DSNN+Strings.repeat(BoxChars.NSNS, $w-2)+BoxChars.DNNS);
		T.cursor().place(0, 3);
		final int $inset = 3;
		
		// print top line, listing the background colors
		T.cursor().shiftRight($inset);
		T.print(Strings.padLeftToWidth("",' ',13));
		for (Color $bg : Color.values()) {
			T.setPalette(new Palette(Color.CLEAR, $bg, true, true));
			T.print(Strings.padRightToWidth("  bg: "+$bg+" ",14));
		}
		
		T.cursor().lineNext(1);
		T.cursor().shiftRight($inset);
		for (Color $fg : Color.values()) {
			// print front of line, listing the forground color
			T.setPalette(new Palette($fg, Color.CLEAR, true, null));
			T.print(Strings.padLeftToWidth(" fg: "+$fg+" ",13));
			
			// print the table body
			for (Color $bg : Color.values()) {
				T.setPalette(new Palette($fg, $bg));
				T.print("     word     ");
			}
			T.cursor().lineNext(1);
			T.cursor().shiftRight($inset);
		}
		
		T.setPalette(new Palette(Color.CLEAR));
		
		
		
		Reader $r = System.console().reader();
		char[] $cbuf = new char[1];
		try {
			while (true) {
				if ($r.ready()) {
					$r.read($cbuf);
					T.print("\r");
					T.cursor().shiftRight(2);
					T.print(new String($cbuf));
				}
			}
		} catch (IOException $e) {
			$e.printStackTrace();
		}
		
		StandardTerminal.get().clear();
	}
}

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
		
		
		// print top line, listing the background colors
		T.print(Strings.padLeftToWidth(""," ",13));
		for (Color $bg : Color.values()) {
			T.setPalette(new Palette(Color.CLEAR, $bg));
			T.print(Strings.padRightToWidth("  bg: "+$bg+" ",14));
		}
		
		T.cursor().lineNext(1);
		for (Color $fg : Color.values()) {
			// print front of line, listing the forground color
			T.setPalette(new Palette($fg, Color.CLEAR));
			T.print(Strings.padLeftToWidth(" fg: "+$fg+" ",13));
			
			// print the table body
			for (Color $bg : Color.values()) {
				T.setPalette(new Palette($fg, $bg));
				T.print("     word     ");
			}
			T.cursor().lineNext(1);
		}
		
		
		Reader $r = System.console().reader();
		char[] $cbuf = new char[1];
		try {
			while (true) {
				if ($r.ready()) {
					$r.read($cbuf);
					System.console().printf(new String($cbuf));
				}
			}
		} catch (IOException $e) {
			$e.printStackTrace();
		}
		
		StandardTerminal.get().clear();
	}
}

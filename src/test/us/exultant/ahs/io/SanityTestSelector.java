package us.exultant.ahs.io;

import us.exultant.ahs.util.*;
import java.io.*;
import java.nio.channels.*;

public class SanityTestSelector {
	public static void main(String... $args) throws IOException {
		Selector selector1 = Selector.open();
		Thread.currentThread().interrupt();
		selector1.select(0);
		if (!Thread.interrupted())
			System.err.println("How the hell did you return if you weren't responding to the interrupt?");
		System.err.println("ta-da!  You survived!  A single thread with preinterruption is fine.");

		final Selector selector = Selector.open();
		final Thread selthread = new Thread() {
			public void run() {
				int count = 0;
				while (true) {
					long start = X.time();
					try {
						X.sayet("selecting...");
						Thread.yield();
						selector.select(10000);
						X.sayet("selected.");
						count++;
					} catch (IOException $e) {
						System.err.println("this is not what I'm testing for.");
						System.exit(10);
					}
					if (Thread.interrupted()) {
						if (X.time() - start > 7000) {
							System.err.println("this should never occur!");
							System.exit(20);
						} else {
							System.err.println("woke from interrupt correctly.");
						}
					}
					if (count % 7 == 0) for (int waste = 0; waste < 10000000; waste++);
				}
			}
		};
		selthread.start();
		int count = 0;
		while (true) {
			X.sayet("interrupting...");
			selthread.interrupt();
			X.sayet("interrupted.");
			count++;
			if (count % 8 == 0) for (int waste = 0; waste < 10000000; waste++);
		}
	}
}

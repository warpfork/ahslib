package ahs.jmx;

import ahs.util.*;
import ahs.view.gui.simple.*;
import ahs.view.gui.simple.Multibuffer.*;

public class Console {
	public Console(String $windowTitle) {
		final long $start = System.currentTimeMillis();
		final Tab $memory = new Tab("memory");
		final Tab $thread = new Tab("threads");
		final StringBuilder $sb = new StringBuilder();
		
		new Multibuffer($windowTitle).addTab($memory).addTab($thread).update();
		
		new Thread("ConsoleUpdateThread") {
			public void run() {
				long $delta;
				String $header;
				for (int $cycles = 0; true; $cycles++) {
					$delta = (System.currentTimeMillis() - $start);
					$sb.delete(0, $sb.length());
					$sb.append("run time: ").append($cycles).append("cycles; ").append($delta).append("ms; ").append($delta/1000).append("seconds");
					$header = $sb.toString();
					
					$memory.getTextArea().setText(
							"        --------    MEMORY    --------        " + $header + "\n\n" +
							MemoryMonitor.getAccumulatedUsageReport(MemoryMonitor.Resolution.MEGA) + "\n\n" +
							MemoryMonitor.getAllReport(MemoryMonitor.Resolution.KILO)
					);
					
					$thread.getTextArea().setText(
							"        --------    THREADS    --------        " + $header + "\n\n" +
							ThreadMonitor.getReport()
					);
					
					X.chill(500);
				}
			}
		}.start();
	}
	
	public Console(String $windowTitle, Runnable $callback) {
		this($windowTitle);
		$callback.run();
	}
	
	
	
	
	public static void main(String... $args) {
		new Console("console");
	}
}

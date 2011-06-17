package us.exultant.ahs.scratch.view.gui.simple;

import us.exultant.ahs.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.List;

// http://www.javaworld.com/javaworld/jw-09-2004/jw-0906-tabbedpane.html
//	not a bad tutorial for f'ing with the guts of swing components, if i ever get into that sort of thing.  some simple animation tricks, too.


public class Multibuffer {
	public Multibuffer(String $title) {
		$window = new Window($title);
		$window.add(new RootPanel());
		$window.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent $evt) {
				for (Tab $t : $tabs) $t.size($window);
				$window.validate();
			}
			public void componentHidden(ComponentEvent $evt) {}
			public void componentMoved(ComponentEvent $evt) {}
			public void componentShown(ComponentEvent $evt) {}
			
		});
		$window.setVisible(true);
		$window.setBounds(700,200,900,600);
		update();
	}
	
	private static final Icon	$icon		= null;
	private static final String	$tooltip	= "";
	private final Window		$window;
	private final JTabbedPane	$pane		= new JTabbedPane(SwingConstants.LEFT);
	private final List<Tab>		$tabs		= new ArrayList<Tab>(5);
	
	public Multibuffer addTab(Tab $t) {
		$tabs.add($t);
		$pane.addTab($t.getName(), $icon, $t.getTextArea(), $tooltip);
		return this;
	}
	
	public Multibuffer update() {
		$window.validate();
		return this;
	}
	
	
	
	public static class Tab {
		public Tab(String $name) {
			this.$name = $name;
			//$ta.setPreferredSize(new Dimension(99,99));
			$ta.setEditable(false);
			$ta.setFont(new Font("monospaced", Font.PLAIN, 10));
		}
		
		private final String	$name;
		private JTextArea	$ta	= new JTextArea();
		
		public String getName() {
			return $name;
		}
		
		public JTextArea getTextArea() {
			return $ta;
		}
		
		public PrintStream getPrintStream() {
			throw new ImBored();
			//TODO:AHS: make a damn print stream
			// this is actually hard, because of the stupid constructor private default constructor on printstream.
			// i don wanna go to bytes and back!
		}
		
		private void size(Container $x) {
			$ta.setPreferredSize(new Dimension($x.getWidth()-200, $x.getHeight()-70));
			$ta.invalidate();
		}
	}
	
	private static class Window extends JFrame {
		public Window(String $title) {
			super($title);
			//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			//this.setBounds(700,200,900,600);
			//this.setResizable(false);
			this.setExtendedState(Frame.MAXIMIZED_BOTH);
			//this.setUndecorated(true);
		}
	}
	
	private class RootPanel extends JPanel {
		public RootPanel() {
			super();
			setBackground(new Color(0x77,0x77,0xAA));
			add($pane, BorderLayout.CENTER);
		}
		
	}

	////////////////////////////////////////////////////////////////
	
	public static void main(String... args) {
		Tab $stdout = new Tab("stdout");
		Tab $stderr = new Tab("stderr");
		new Multibuffer("title").addTab($stdout).addTab($stderr).update();
	}
}

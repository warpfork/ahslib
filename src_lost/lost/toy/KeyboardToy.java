package ahs.lost.toy;

import java.awt.Color;
//import java.awt.Graphics;
//import java.awt.event.ComponentEvent;
//import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
//import java.awt.event.WindowEvent;
//import java.awt.event.WindowListener;

import javax.swing.JFrame;

import ahs.view.*;

public class KeyboardToy {
	public static void main(String... args) {
		new KeyboardToy();
	}
	
	public KeyboardToy() {
		$ct = new ConsoleTable(3);
		$ct.setSizes(7,5,5);
		$ct.setPrefixes("char: ","code: ","loc: ","BEES");
		new Fram().setVisible(true);
	}
	
	private ConsoleTable $ct;
	
	public void hearKey(KeyEvent $e) {
		//X.say("char:"+$e.getKeyChar()+"\tcode:"+$e.getKeyCode()+"\tloc:"+$e.getKeyLocation());
		$ct.println($e.getKeyChar()+"", $e.getKeyCode()+"", $e.getKeyLocation()+"");
	}
	
	private class Fram extends JFrame {
		public Fram() {  
			setBackground(new Color(0,0,0));
			setFocusTraversalKeysEnabled(false);
			addKeyListener(new KeyFucker());
			setBounds(10,10,100,100);
		}
		
		private class KeyFucker implements KeyListener {
			public void keyPressed(KeyEvent $e) {
				hearKey($e);
			}
			public void keyReleased(KeyEvent $arg0) {
				;;
			}
			public void keyTyped(KeyEvent $arg0) {
				;;
				
			}
		}
	}
}

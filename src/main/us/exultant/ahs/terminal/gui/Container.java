package us.exultant.ahs.terminal.gui;

import java.util.*;

public class Container implements Component {
	private final List<Component>	$children	= new ArrayList<Component>();
	
	public Container() {}
	
	public void paint(Window $buffer) {
		paintSelf($buffer);
		paintChildren($buffer);
	}
	protected void paintSelf(Window $buffer) {}
	private final void paintChildren(Window $buffer) {
		for (int $n = $children.size() - 1; $n >= 0; $n--) {
			Component $x = $children.get($n);
			//$g3.specialize($x.size(this.getBound().getSize())).paintComponent($x);
		}
	}
}

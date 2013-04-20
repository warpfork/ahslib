/*
 * Copyright 2010 - 2013 Eric Myhre <http://exultant.us>
 * 
 * This file is part of AHSlib.
 *
 * AHSlib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License, or
 * (at the original copyright holder's option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package us.exultant.ahs.scratch.view.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.text.AbstractDocument.*;

public class HTD extends JEditorPane {
	public HTD() {
		super("text/html","");
		setMargin(new java.awt.Insets(0,0,0,0));
	}
	public HTD(String $html) {
		super("text/html",$html);
		setMargin(new java.awt.Insets(0,0,0,0));
	}
	
	protected void paintComponent(Graphics g) {
		Document $doc = this.getDocument();
//		String $shit = "\t";
		if ($doc instanceof HTMLDocument) {
//			HTMLDocument $htdoc = (HTMLDocument) $doc;
//			HTMLDocument.Iterator $itr = $htdoc.getIterator(HTML.Tag.BODY);
//			$itr.
			AbstractElement $dom_html = (AbstractElement) $doc.getDefaultRootElement();
			AbstractElement $dom_body = null;
			for (int $i = 0; $i < $dom_html.getChildCount(); $i++) {
				AbstractElement $temp = (AbstractElement)$dom_html.getChildAt($i);
				if ("body".equals($temp.getName())) {
					$dom_body = $temp;
					break;
				}
			}
			
			HTMLDocument.BlockElement $poo = (HTMLDocument.BlockElement) $dom_body;
			
			//$poo.addAttribute("randomshit", "fuck");
			//System.out.println($dom_body.getClass());
			
//			java.util.Enumeration<?> $attribnames = $dom_body.getAttributes().getAttributeNames();
//			while ($attribnames.hasMoreElements())
//				$shit += $attribnames.nextElement()+",";
//			System.out.println($shit);
		}
		
		super.paintComponent(g);
		System.out.println(getBounds());
	}
	
	public Dimension getPreferredSize() {
		if (isPreferredSizeSet()) { return super.getPreferredSize(); }
		Dimension size = null;
		if (ui != null) {
			size = ui.getPreferredSize(this);
		}
		return (size != null) ? size : super.getPreferredSize();
	}
}

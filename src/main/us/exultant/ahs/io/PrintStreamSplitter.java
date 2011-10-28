/*
 * Copyright 2010, 2011 Eric Myhre <http://exultant.us>
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

package us.exultant.ahs.io;

import java.io.*;
import java.util.*;

public class PrintStreamSplitter extends PrintStream {
	public PrintStreamSplitter(PrintStream $a, PrintStream $b) {
		super($a);
		this.$b = $b;
	}
	
	private PrintStream $b;

	public PrintStream append(char $c) {
		super.append($c);
		$b.append($c);
		return this;
	}

	public PrintStream append(CharSequence $csq, int $start, int $end) {
		super.append($csq, $start, $end);
		$b.append($csq, $start, $end);
		return this;
	}

	public PrintStream append(CharSequence $csq) {
		super.append($csq);
		$b.append($csq);
		return this;
	}

	public boolean checkError() {
		return super.checkError() || $b.checkError();
	}

	public void close() {
		super.close();
		$b.close();
	}

	public void flush() {
		super.flush();
		$b.flush();
	}

	public PrintStream format(Locale $l, String $format, Object... $args) {
		super.format($l, $format, $args);
		$b.format($l, $format, $args);
		return this;
	}

	public PrintStream format(String $format, Object... $args) {
		super.format($format, $args);
		$b.format($format, $args);
		return this;
	}

	public void print(boolean $b) {
		super.print($b);
		this.$b.print($b);
	}

	public void print(char $c) {
		super.print($c);
		$b.print($c);
	}

	public void print(char[] $s) {
		super.print($s);
		$b.print($s);
	}

	public void print(double $d) {
		super.print($d);
		$b.print($d);
	}

	public void print(float $f) {
		super.print($f);
		$b.print($f);
	}

	public void print(int $i) {
		super.print($i);
		$b.print($i);
	}

	public void print(long $l) {
		super.print($l);
		$b.print($l);
	}

	public void print(Object $obj) {
		super.print($obj);
		$b.print($obj);
	}

	public void print(String $s) {
		super.print($s);
		$b.print($s);
	}

	public PrintStream printf(Locale $l, String $format, Object... $args) {
		super.printf($l, $format, $args);
		$b.printf($l, $format, $args);
		return this;
	}

	public PrintStream printf(String $format, Object... $args) {
		super.printf($format, $args);
		$b.printf($format, $args);
		return this;
	}

	public void println() {
		super.println();
		$b.println();
	}

	public void println(boolean $x) {
		super.println($x);
		$b.println($x);
	}

	public void println(char $x) {
		super.println($x);
		$b.println($x);
	}

	public void println(char[] $x) {
		super.println($x);
		$b.println($x);
	}

	public void println(double $x) {
		super.println($x);
		$b.println($x);
	}

	public void println(float $x) {
		super.println($x);
		$b.println($x);
	}

	public void println(int $x) {
		super.println($x);
		$b.println($x);
	}

	public void println(long $x) {
		super.println($x);
		$b.println($x);
	}

	public void println(Object $x) {
		super.println($x);
		$b.println($x);
	}

	public void println(String $x) {
		super.println($x);
		$b.println($x);
	}
	
	public void write(byte[] $buf, int $off, int $len) {
		super.write($buf, $off, $len);
		$b.write($buf, $off, $len);
	}

	public void write(byte[] $arg0) throws IOException {
		super.write($arg0);
		$b.write($arg0);
	}

	public void write(int $b) {
		super.write($b);
		this.$b.write($b);
	}
}

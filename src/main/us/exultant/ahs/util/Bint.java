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

package us.exultant.ahs.util;

import java.math.BigInteger;

public class Bint extends BigInteger {
	public Bint(long $n, int $radix) {
		super(String.valueOf($n), $radix);
	}
	/**
	 * Use of Bint.valueof($n) should be preferred.
	 */
	public Bint(long $n) {
		super(String.valueOf($n));
	}
	public Bint(String $n, int $radix) {
		super($n, $radix);
	}
	public Bint(String $n) {
		super($n);
	}
	
	public int compareTo(long $n) {
		return super.compareTo(Bint.valueOf($n)); 
	}
	public int compareTo(double $n) {
		return super.compareTo(Bint.valueOf((long)$n)); 
	}
	public boolean isGreaterThan(BigInteger $n) {
		return (this.compareTo($n) > 0);
	}
	public boolean isGreaterThan(long $n) {
		return this.isGreaterThan(Bint.valueOf($n));
	}
	public boolean isGreaterThanZero() {	// these methods are more GC-sparing than the otherwise equivalent call to this.isGreaterThan(0);
		return this.isGreaterThan(super.ZERO);
	}
	public boolean isLessThan(BigInteger $n) {
		return (this.compareTo($n) < 0);
	}
	public boolean isLessThan(long $n) {
		return this.isLessThan(Bint.valueOf($n));
	}
	public boolean isLessThanZero() {
		return this.isLessThan(super.ZERO);
	}
	public boolean isEqualTo(BigInteger $n) {
		return (this.compareTo($n) == 0);
	}
	public boolean isEqualTo(long $n) {
		return this.isEqualTo(Bint.valueOf($n));
	}
	public boolean isEqualToZero() {
		return this.isEqualTo(super.ZERO);
	}
	public boolean equalsZero() {
		return this.isEqualToZero();
	}

	public Bint add(double $n) {
		return (Bint)this.add(Bint.valueOf((long)$n));
	}
	public Bint subtract(double $n) {
		return (Bint)this.subtract(Bint.valueOf((long)$n));
	}
	public Bint multiply(double $n) {
		return (Bint)this.multiply(Bint.valueOf((long)$n));
	}
	public Bint divide(double $n) {
		return (Bint)this.divide(Bint.valueOf((long)$n));
	}
}

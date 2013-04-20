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

package us.exultant.ahs.codec;

import us.exultant.ahs.core.*;
import us.exultant.ahs.codec.json.*;
import us.exultant.ahs.test.*;
import java.util.*;

public class CodecJsonTest extends CodecEonTest {
	public static void main(String... $args) { new CodecJsonTest().run(); }
	
	public CodecJsonTest() {
		super(new JsonCodec());
	}
	
	public List<Unit> getUnits() {
		List<Unit> $tests = super.getUnits();
		$tests.add(new TestBasicSerialMatch());
		return $tests;
	}
	
	
	
	private class TestBasicSerialMatch extends TestCase.Unit {
		public void call() throws TranslationException {
			JsonCodec $jc = new JsonCodec();
			$jc.putHook(TobjPrimitive.class, new TobjPrimitive.Den());
			
			TobjPrimitive $x1 = new TobjPrimitive("whip it");
			JsonObject $c = $jc.encode($x1);
			
			assertEquals("{\"#\":\"TobjPrimitive\",\"$\":\"dat\",\"%\":\"whip it\"}", $c.toString());
		}
	}
}

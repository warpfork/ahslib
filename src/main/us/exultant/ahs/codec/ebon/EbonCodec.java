/*
 * Copyright 2010 - 2013 Eric Myhre <http://exultant.us>
 *
 * This file is part of AHSlib.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.exultant.ahs.codec.ebon;

import us.exultant.ahs.core.*;
import us.exultant.ahs.codec.eon.*;

/**
 * Implements {@link EonCodec} with maps and arrays provided by {@link EonObject} and
 * {@link EonArray}.
 *
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 */
public class EbonCodec extends EonCodec {
	public static final EbonCodec X = new EbonCodec();

	public EbonCodec() {
		super(OBJPROVIDER, ARRPROVIDER);
	}

	public static final Factory<EbonObject>	OBJPROVIDER	= new Factory<EbonObject>() {
										public EbonObject make() {
											return new EbonObject();
										}
									};
	public static final Factory<EbonArray>	ARRPROVIDER	= new Factory<EbonArray>() {
										public EbonArray make() {
											return new EbonArray();
										}
									};

	public <$TARG> EbonObject encode($TARG $datclr, Class<$TARG> $datclrclass) throws TranslationException { return (EbonObject)super.encode($datclr, $datclrclass); }
	public <$TARG> EbonObject encode($TARG $datclr) throws TranslationException { return (EbonObject)super.encode($datclr); }
	//public <$TARG> $TARG decode(EbonObject $datenc, Class<$TARG> $datclrclass) throws TranslationException { return super.decode($datenc, $datclrclass); }	// pointless.  return type doesn't change.  just provides another function with a more specific argument that does the same thing; doesn't mask the more general one.

	public EbonObject simple(Object $class, String $name, EonObject $data) { return (EbonObject)super.simple($class,$name,$data); }
	public EbonObject simple(String $class, String $name, EonObject $data) { return (EbonObject)super.simple($class,$name,$data); }
	public EbonObject simple(Object $class, String $name, EonArray $data) { return (EbonObject)super.simple($class,$name,$data); }
	public EbonObject simple(String $class, String $name, EonArray $data) { return (EbonObject)super.simple($class,$name,$data); }
	public EbonObject simple(Object $class, String $name, String $data) { return (EbonObject)super.simple($class,$name,$data); }
	public EbonObject simple(String $class, String $name, String $data) { return (EbonObject)super.simple($class,$name,$data); }
	public EbonObject simple(Object $class, String $name, byte[] $data) { return (EbonObject)super.simple($class,$name,$data); }
	public EbonObject simple(String $class, String $name, byte[] $data) { return (EbonObject)super.simple($class,$name,$data); }
}

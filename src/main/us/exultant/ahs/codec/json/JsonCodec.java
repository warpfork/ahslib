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

package us.exultant.ahs.codec.json;

import us.exultant.ahs.core.*;
import us.exultant.ahs.codec.eon.*;

/**
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 */
public class JsonCodec extends EonCodec {
	public static final JsonCodec X = new JsonCodec();
	
	public JsonCodec() {
		super(OBJPROVIDER, ARRPROVIDER);
	}
	
	public static final Factory<JsonObject>	OBJPROVIDER	= new Factory<JsonObject>() {
										public JsonObject make() {
											return new JsonObject();
										}
									};
	public static final Factory<JsonArray>	ARRPROVIDER	= new Factory<JsonArray>() {
										public JsonArray make() {
											return new JsonArray();
										}
									};
	
	public <$TARG> JsonObject encode($TARG $datclr, Class<$TARG> $datclrclass) throws TranslationException { return (JsonObject)super.encode($datclr, $datclrclass); }
	public <$TARG> JsonObject encode($TARG $datclr) throws TranslationException { return (JsonObject)super.encode($datclr); }
	//public <$TARG> $TARG decode(JsonObject $datenc, Class<$TARG> $datclrclass) throws TranslationException { return super.decode($datenc, $datclrclass); }	// pointless.  return type doesn't change.  just provides another function with a more specific argument that does the same thing; doesn't mask the more general one.
	
	public JsonObject simple(Object $class, String $name, EonObject $data) { return (JsonObject)super.simple($class,$name,$data); }
	public JsonObject simple(String $class, String $name, EonObject $data) { return (JsonObject)super.simple($class,$name,$data); }
	public JsonObject simple(Object $class, String $name, EonArray $data) { return (JsonObject)super.simple($class,$name,$data); }
	public JsonObject simple(String $class, String $name, EonArray $data) { return (JsonObject)super.simple($class,$name,$data); }
	public JsonObject simple(Object $class, String $name, String $data) { return (JsonObject)super.simple($class,$name,$data); }
	public JsonObject simple(String $class, String $name, String $data) { return (JsonObject)super.simple($class,$name,$data); }
	public JsonObject simple(Object $class, String $name, byte[] $data) { return (JsonObject)super.simple($class,$name,$data); }
	public JsonObject simple(String $class, String $name, byte[] $data) { return (JsonObject)super.simple($class,$name,$data); }
}

package us.exultant.ahs.codec.json;

import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;
import us.exultant.ahs.codec.eon.*;

import java.util.*;


/**
 * @author hash
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












// THIS SHIT IS OLD:

	// example of typical code body found within a class that provides its own codec.
	// note that often the encode and decode blocks will also involve references the private fields of TheTargetClass.
	
	///* BEGIN JSON CODEC BLOCK */
	//public static final Encoder<JsonObject,IdGlob> ENCODER_JSON;
	//public static final Decoder<JsonObject,IdGlob> DECODER_JSON;
	//static { JsonDencoder $t = new JsonDencoder(); ENCODER_JSON = $t; DECODER_JSON = $t; }
	//public static class JsonDencoder implements Dencoder<JsonObject,IdGlob> {
	//	public JsonObject encode(Codec<JsonObject> $codec, IdGlob $x) throws TranslationException {
	//		JsonObject $jo = new JsonObject();
	//		$jo.putKlass($x);
	//		$jo.put("p", $codec.encode($x.$pse));
	//		$jo.put("n", $codec.encode($x.$nth));
	//		$jo.put("k", Codec_Json.encodeList($codec, $x.$pkr));
	//		return $jo;
	//	}
	//	public IdGlob decode(Codec<JsonObject> $codec, JsonObject $jo) throws TranslationException {
	//		$jo.assertKlass(IdGlob.class);
	//		try {
	//			return new IdGlob(
	//					$codec.decode($jo.getJsonObject("p"), Pseudonym.class),
	//					$codec.decode($jo.getJsonObject("n"), Nethernym.class),
	//					new ArrayList<IbeKeyPrivate>(Codec_Json.decodeList($codec, $jo.getJsonArray("k"), IbeKeyPrivate.class))
	//			);
	//		} catch (Exception $e) {
	//			throw new TranslationException($e);
	//		}
	//	}
	//}
	///* END JSON CODEC BLOCK */

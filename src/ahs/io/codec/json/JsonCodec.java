package ahs.io.codec.json;

import ahs.io.*;
import ahs.io.codec.Codec.*;
import ahs.io.codec.*;
import ahs.util.*;

import java.util.*;


/**
 * @author hash
 */
@Deprecated
public class JsonCodec extends CodecImpl<JsonObject> {
	public JsonCodec() {
		super();
	}
	
	
	
	public static byte[] serialize(JsonObject $jo) throws TranslationException {
		return $jo.toString().getBytes(Strings.UTF_8);
	}
	public <$TARG> byte[] serialize($TARG $datclr) throws TranslationException {
		return serialize(encode($datclr));
	}
	
	public static JsonObject deserialize(byte[] $bar) throws TranslationException {
		return new JsonObject(new String($bar, Strings.UTF_8));
	}
	public <$TARG> $TARG deserialize(byte[] $bar, Class<$TARG> $datclrclass) throws TranslationException {
		return decode(deserialize($bar), $datclrclass);
	}
	
	public static <$TYPE> JsonArray encodeList(Codec<JsonObject> $codec, List<$TYPE> $list) throws TranslationException {
		JsonArray $ja = new JsonArray();
		int $size = $list.size();
		for (int $i = 0; $i < $size; $i++)
			$ja.put($codec.encode($list.get($i)));
		return $ja;
	}
	
	public static <$TYPE> List<$TYPE> decodeList(Codec<JsonObject> $codec, JsonArray $list, Class<$TYPE> $datclrclass) throws TranslationException {
		int $size = $list.length();
		List<$TYPE> $v = new ArrayList<$TYPE>($size);
		for (int $i = 0; $i < $size; $i++)
			$v.add($codec.decode($list.getObj($i), $datclrclass));
		return $v;
	}
}


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

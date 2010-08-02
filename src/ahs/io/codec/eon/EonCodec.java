package ahs.io.codec.eon;

import ahs.io.*;
import ahs.io.codec.*;
import ahs.util.*;

import java.util.*;

public class EonCodec extends CodecImpl<EonObject> {
	public EonCodec() {
		super();
	}
	
	public static byte[] serialize(EonObject $eo) throws TranslationException {
		return $eo.serialize();
	}
	public <$TARG> byte[] serialize($TARG $datclr) throws TranslationException {
		return serialize(encode($datclr));
	}
	
	public static EonObject deserialize(EonObject $eo, byte[] $bar) throws TranslationException {
		$eo.deserialize($bar);
		return $eo;
	}
	public <$TARG> $TARG deserialize(EonObject $eo, byte[] $bar, Class<$TARG> $datclrclass) throws TranslationException {
		return decode(deserialize($eo, $bar), $datclrclass);
	}
	
	public static <$TYPE> EonArray encodeList(EonArray $ea, Codec<EonObject> $codec, List<$TYPE> $list) throws TranslationException {
		int $size = $list.size();
		for (int $i = 0; $i < $size; $i++)
			$ea.put($i, $codec.encode($list.get($i)));
		return $ea;
	}
	
	public static <$TYPE> List<$TYPE> decodeList(Codec<EonObject> $codec, EonArray $ea, Class<$TYPE> $datclrclass) throws TranslationException {
		int $size = $ea.size();
		List<$TYPE> $v = new ArrayList<$TYPE>($size);
		for (int $i = 0; $i < $size; $i++)
			$v.add($codec.decode($ea.getObj($i), $datclrclass));
		return $v;
	}
}

package ahs.io.codec.eon;

import ahs.io.*;
import ahs.io.codec.*;
import ahs.util.*;

import java.util.*;

/**
 * @author hash
 */
public class EonCodec extends CodecImpl<EonCodec,EonObject> {
	/**
	 * Constructs a new EonCodec that contains no encode or decode hooks and uses the
	 * given factories to produce new empty objects and arrays for storage.
	 */
	public EonCodec(Factory<? extends EonObject> $objProvider, Factory<? extends EonArray> $arrProvider) {
		super();
		this.$objProvider = $objProvider;
		this.$arrProvider = $arrProvider;
	}
	
	/**
	 * Constructs a new EonCodec that contains no encode or decode hooks and using the
	 * same (pointer-equality!) factories to produce new empty objects and arrays for
	 * storage.
	 */
	public EonCodec(EonCodec $other) {
		super();
		this.$objProvider = $other.$objProvider;
		this.$arrProvider = $other.$arrProvider;
	}
	
	private final Factory<? extends EonObject> $objProvider;
	private final Factory<? extends EonArray> $arrProvider;
	
	public static byte[] serialize(EonObject $eo) throws TranslationException {
		return $eo.serialize();
	}
	public <$TARG> byte[] serialize($TARG $datclr) throws TranslationException {
		return encode($datclr).serialize();
	}
	public <$TARG> byte[] serialize($TARG $datclr, Class<$TARG> $class) throws TranslationException {
		return encode($datclr, $class).serialize();
	}
	
	public EonObject deserialize(byte[] $bar) throws TranslationException {
		EonObject $eo = $objProvider.make();
		$eo.deserialize($bar);
		return $eo;
	}
	public <$TARG> $TARG deserialize(byte[] $bar, Class<$TARG> $datclrclass) throws TranslationException {
		return decode(deserialize($bar), $datclrclass);
	}
	
	
	
	
	
	public EonArray newArr() {
		return $arrProvider.make();
	}
	
	public EonObject newObj() {
		return $objProvider.make();
	}
	
	
	
	
	
	public <$TYPE> EonArray encodeList(List<$TYPE> $list) throws TranslationException {
		EonArray $ea = $arrProvider.make();
		int $size = $list.size();
		for (int $i = 0; $i < $size; $i++)
			$ea.put($i, this.encode($list.get($i)));
		return $ea;
	}
	
	public <$TYPE> List<$TYPE> decodeList(EonArray $ea, Class<$TYPE> $datclrclass) throws TranslationException {
		int $size = $ea.size();
		List<$TYPE> $v = new ArrayList<$TYPE>($size);
		for (int $i = 0; $i < $size; $i++)
			$v.add(this.decode($ea.getObj($i), $datclrclass));
		return $v;
	}
	
	
	
	
	
	public EonObject simple(Object $class, String $name, EonObject $data) {
		EonObject $holder = $objProvider.make();
		if ($class != null) $holder.putKlass($class);
		if ($name != null)  $holder.putName($name);
		if ($data != null)  $holder.putData($data);
		return $holder;
	}
	public EonObject simple(String $class, String $name, EonObject $data) {
		EonObject $holder = $objProvider.make();
		if ($class != null) $holder.putKlass($class);
		if ($name != null)  $holder.putName($name);
		if ($data != null)  $holder.putData($data);
		return $holder;
	}
	public EonObject simple(Object $class, String $name, EonArray $data) {
		EonObject $holder = $objProvider.make();
		if ($class != null) $holder.putKlass($class);
		if ($name != null)  $holder.putName($name);
		if ($data != null)  $holder.putData($data);
		return $holder;
	}
	public EonObject simple(String $class, String $name, EonArray $data) {
		EonObject $holder = $objProvider.make();
		if ($class != null) $holder.putKlass($class);
		if ($name != null)  $holder.putName($name);
		if ($data != null)  $holder.putData($data);
		return $holder;
	}
	public EonObject simple(Object $class, String $name, String $data) {
		EonObject $holder = $objProvider.make();
		if ($class != null) $holder.putKlass($class);
		if ($name != null)  $holder.putName($name);
		if ($data != null)  $holder.putData($data);
		return $holder;
	}
	public EonObject simple(String $class, String $name, String $data) {
		EonObject $holder = $objProvider.make();
		if ($class != null) $holder.putKlass($class);
		if ($name != null)  $holder.putName($name);
		if ($data != null)  $holder.putData($data);
		return $holder;
	}
	public EonObject simple(Object $class, String $name, byte[] $data) {
		EonObject $holder = $objProvider.make();
		if ($class != null) $holder.putKlass($class);
		if ($name != null)  $holder.putName($name);
		if ($data != null)  $holder.putData($data);
		return $holder;
	}
	public EonObject simple(String $class, String $name, byte[] $data) {
		EonObject $holder = $objProvider.make();
		if ($class != null) $holder.putKlass($class);
		if ($name != null)  $holder.putName($name);
		if ($data != null)  $holder.putData($data);
		return $holder;
	}
}

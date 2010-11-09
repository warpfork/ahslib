package ahs.io.codec.ebon;

import ahs.io.*;
import ahs.io.codec.eon.*;
import ahs.io.codec.*;
import ahs.util.*;

import java.util.*;


/**
 * @author hash
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

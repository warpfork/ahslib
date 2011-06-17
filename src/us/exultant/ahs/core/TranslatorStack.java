package us.exultant.ahs.core;

import java.util.*;

/**
 * Helper class to construct a type-safe chain of Translator instances at runtime that can
 * then act as a single coherent Translator from end to end.
 * 
 * @author hash
 * 
 * @param <$FROM>
 * @param <$TO>
 */
public class TranslatorStack<$FROM, $TO> implements Translator<$FROM, $TO> {
	// these factory methods are ugly in their repetitiveness, but also perfect in their spectacular genericness.
	// and if you want more than six Translator in a chain, then fuck you.
	public static <$I,$O,$T1> TranslatorStack<$I,$O> make(Translator<$I,$T1> $t0, Translator<$T1,$O> $t1) {
		TranslatorStack<$I,$O> $v = new TranslatorStack<$I,$O>(2);
		$v.$dat.add($t0);
		$v.$dat.add($t1);
		return $v;
	}
	public static <$I,$O,$T1,$T2> TranslatorStack<$I,$O> make(Translator<$I,$T1> $t0, Translator<$T1,$T2> $t1, Translator<$T2,$O> $t2) {
		TranslatorStack<$I,$O> $v = new TranslatorStack<$I,$O>(3);
		$v.$dat.add($t0);
		$v.$dat.add($t1);
		$v.$dat.add($t2);
		return $v;
	}
	public static <$I,$O,$T1,$T2,$T3> TranslatorStack<$I,$O> make(Translator<$I,$T1> $t0, Translator<$T1,$T2> $t1, Translator<$T2,$T3> $t2, Translator<$T3,$O> $t3) {
		TranslatorStack<$I,$O> $v = new TranslatorStack<$I,$O>(4);
		$v.$dat.add($t0);
		$v.$dat.add($t1);
		$v.$dat.add($t2);
		$v.$dat.add($t3);
		return $v;
	}
	public static <$I,$O,$T1,$T2,$T3,$T4> TranslatorStack<$I,$O> make(Translator<$I,$T1> $t0, Translator<$T1,$T2> $t1, Translator<$T2,$T3> $t2, Translator<$T3,$T4> $t3, Translator<$T4,$O> $t4) {
		TranslatorStack<$I,$O> $v = new TranslatorStack<$I,$O>(5);
		$v.$dat.add($t0);
		$v.$dat.add($t1);
		$v.$dat.add($t2);
		$v.$dat.add($t3);
		$v.$dat.add($t4);
		return $v;
	}
	public static <$I,$O,$T1,$T2,$T3,$T4,$T5> TranslatorStack<$I,$O> make(Translator<$I,$T1> $t0, Translator<$T1,$T2> $t1, Translator<$T2,$T3> $t2, Translator<$T3,$T4> $t3, Translator<$T4,$T5> $t4, Translator<$T5,$O> $t5) {
		TranslatorStack<$I,$O> $v = new TranslatorStack<$I,$O>(6);
		$v.$dat.add($t0);
		$v.$dat.add($t1);
		$v.$dat.add($t2);
		$v.$dat.add($t3);
		$v.$dat.add($t4);
		$v.$dat.add($t5);
		return $v;
	}
	
	private TranslatorStack(int $size) {
		$dat = new ArrayList<Translator<?,?>>($size);
	}
	private List<Translator<?,?>> $dat;
	
	@SuppressWarnings("unchecked")	// runtime safety invariants are enforced by factory methods.
	public $TO translate($FROM $x) throws TranslationException {
		Object $v = $x;
		for (Translator<?,?> $t : $dat) {
			$v = ((Translator<Object,Object>)$t).translate($v);
			if ($v == null) break;
		}
		return ($TO)$v;
	}
	
	/**
	 * DO NOT USE.
	 * 
	 * I honestly wish I didn't have to have this method. It's intended for the really
	 * nasty case in WriteHeadAdapter where sometimes I need to be able to see the
	 * bottom guy on the stack to do a little extra configuration on him so he can get
	 * his base channel. Really, really icky. If I can find a way to refactor this out
	 * someday, I'd love to.
	 */
	@Deprecated
	public List<Translator<?,?>> expose() {
		return Collections.unmodifiableList($dat);
	}
}

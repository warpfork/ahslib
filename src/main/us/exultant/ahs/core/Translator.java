package us.exultant.ahs.core;

public interface Translator<$FROM, $TO> {
	public $TO translate($FROM $x) throws TranslationException;
}

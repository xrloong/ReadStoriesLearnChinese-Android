package idv.gaozao.readstory.activity;

import java.util.Locale;

class LocaleItem {
	private Locale mLocale;
	LocaleItem(String langCode) {
		mLocale = new Locale(langCode);
	}

	public Locale getLocale() {
		return mLocale;
	}

	@Override
	public String toString() {
		return String.format("%s(%s)", mLocale.getDisplayLanguage(mLocale), mLocale.getDisplayLanguage());
	}
}
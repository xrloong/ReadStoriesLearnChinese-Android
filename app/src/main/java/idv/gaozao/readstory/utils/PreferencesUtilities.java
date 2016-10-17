package idv.gaozao.readstory.utils;

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesUtilities {
	private static final String SHARED_PREFERENCES__FILE__CONFIG = "config";

	private static final String SHARED_PREFERENCES__KEY__SHOW_HELP_WHEN_LAUNCH = "showHelpWhenLauncher";
	private static final boolean SHARED_PREFERENCES__DEFAULT_VALUE__SHOW_HELP_WHEN_LAUNCH = true;

	private static final String SHARED_PREFERENCES__KEY__LANGUAGE = "lang";
	private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

	public static String getArticleLanguage(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES__FILE__CONFIG, Context.MODE_PRIVATE);
		String langCode = prefs.getString(SHARED_PREFERENCES__KEY__LANGUAGE, DEFAULT_LOCALE.getLanguage());
		return langCode;
	}

	public static void setArticleLanguage(Context context, String langCode) {
		SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES__FILE__CONFIG, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(SHARED_PREFERENCES__KEY__LANGUAGE, langCode);
		editor.commit();
	}

	public static void setDefaultArticleLanguage(Context context) {
		setArticleLanguage(context, DEFAULT_LOCALE.getLanguage());
	}

	public static boolean isArticleLanguageSet(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES__FILE__CONFIG, Context.MODE_PRIVATE);
		return prefs.contains(SHARED_PREFERENCES__KEY__LANGUAGE);
	}

	public static boolean getShowHelp(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES__FILE__CONFIG, Context.MODE_PRIVATE);

		boolean showHelpWhenLauncher = prefs.getBoolean(SHARED_PREFERENCES__KEY__SHOW_HELP_WHEN_LAUNCH, SHARED_PREFERENCES__DEFAULT_VALUE__SHOW_HELP_WHEN_LAUNCH);
		return showHelpWhenLauncher;

	}

	public static void setShowHelp(Context context, boolean toShow) {
		SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES__FILE__CONFIG, Context.MODE_PRIVATE);

		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(SHARED_PREFERENCES__KEY__SHOW_HELP_WHEN_LAUNCH, false);
		editor.commit();
	}
}

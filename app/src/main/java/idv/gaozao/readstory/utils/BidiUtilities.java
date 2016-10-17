package idv.gaozao.readstory.utils;

public class BidiUtilities {

	public static final String LTR = "\u200E";
	public static final String RTL = "\u200F";
	public static final String LRO = "\u202D";
	public static final String RLO = "\u202E";
	public static final String PDF = "\u202C";

	public static String getTextInRLO(String str) {
		return new StringBuilder(str).reverse().toString();
	}

	public static String getTextWithBidiChinese(String s, boolean isLTR) {
		if(isLTR) {
			return s;
		} else {
			return getTextInRLO(s);
//			return new StringBuilder(s).reverse().toString();
//			return RLO + s + PDF;
		}

	}

	public static String getTextWithBidiGravity(String s, boolean isLTR) {
		if(!isLTR) {
			s = BidiUtilities.RTL + s + BidiUtilities.PDF;
		}

		return s;
	}
}

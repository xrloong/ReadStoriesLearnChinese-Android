package idv.gaozao.readstory;

import java.text.Bidi;

import android.text.SpannableString;

public class AnnotationString {
	/*
	private enum BidiDirection {
		LTR, RTL
	}
	*/
	private String mString;
	private int mBidiDirection = Bidi.DIRECTION_LEFT_TO_RIGHT;
//	Bidi.

	public AnnotationString() {
		this("");
	}

	AnnotationString(String string) {
		mString = string;
	}

	public void toRTL() {
		mBidiDirection = Bidi.DIRECTION_RIGHT_TO_LEFT;
	};

	public void toLTR() {
		mBidiDirection = Bidi.DIRECTION_LEFT_TO_RIGHT;
	}

	public String getString() {
		return mString;
	}

	public SpannableString getSpannableString(boolean showAnnotation) {
		return UpperAnnotatorSpan.toSpan(mString, showAnnotation, mBidiDirection == Bidi.DIRECTION_LEFT_TO_RIGHT);
	}
}

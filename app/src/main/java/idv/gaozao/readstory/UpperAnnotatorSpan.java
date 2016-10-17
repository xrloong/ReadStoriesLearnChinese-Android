package idv.gaozao.readstory;

import idv.gaozao.readstory.utils.BidiUtilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ReplacementSpan;

public class UpperAnnotatorSpan extends ReplacementSpan {

	private static final String PATTERN_EXPRESSION = "\\{([^|]*)\\|([^}]*)\\}";
	private static final float ANNOTATION_PORTION = 0.5f;
	private boolean mShowAnnotation = true;

	private boolean mIsLTR = false;
	private UpperAnnotatorSpan(boolean isLTR, boolean showAnnotation) {
		mShowAnnotation = showAnnotation;
		mIsLTR = isLTR;
	}

	public static SpannableString toSpan(String t, boolean showAnnotation, boolean isLtr) {
		t = BidiUtilities.getTextWithBidiGravity(t, isLtr);

		SpannableString span = new SpannableString(t);
		Pattern p = Pattern.compile(UpperAnnotatorSpan.PATTERN_EXPRESSION);
		Matcher m = p.matcher(span);
		while(m.find()) {
			UpperAnnotatorSpan upperAnnotatorSpan = new UpperAnnotatorSpan(isLtr, showAnnotation);
			span.setSpan(upperAnnotatorSpan, m.start(), m.end(), 0);
		}

		return span;
	}

	@Override
	public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
		TextPaint annotationPaint = new TextPaint(paint);
		annotationPaint.setTextSize(paint.getTextSize() * ANNOTATION_PORTION);

		CharSequence expression = text.subSequence(start, end);
		CharSequence annotation = getAnnotation(expression);
		CharSequence word = getWord(expression);

		int width = computeSize(paint, word, annotationPaint, annotation);

		Rect wordBounds = new Rect();
		paint.getTextBounds(word.toString(), 0, word.length(), wordBounds);
		canvas.save();
		canvas.translate((width - wordBounds.width()) / 2, 0);
		canvas.drawText(word, 0, word.length(), x, y, paint);
		canvas.restore();

		if(mShowAnnotation) {
			float textSize = paint.getTextSize();

			Rect annotationBounds = new Rect();
			annotationPaint.getTextBounds(annotation.toString(), 0, annotation.length(), annotationBounds);
			canvas.save();
			canvas.translate((width - annotationBounds.width()) / 2,  -textSize);
			canvas.drawText(annotation, 0, annotation.length(), x, y, annotationPaint);
			canvas.restore();
		}
		float textSize = paint.getTextSize();

		if(false) {
			String phone = "ㄓㄨㄢ";
		Rect phoneBounds = new Rect();
		annotationPaint.getTextBounds(phone.toString(), 0, phone.length(), phoneBounds);
		canvas.save();
		canvas.translate((width - phoneBounds.width()) / 2,  textSize  * ANNOTATION_PORTION);
		canvas.drawText(phone, 0, phone.length(), x, y, annotationPaint);
		canvas.restore();
		}
	}

	@Override
	public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
		TextPaint annotationPaint = new TextPaint(paint);
		annotationPaint.setTextSize(paint.getTextSize() * ANNOTATION_PORTION);

		CharSequence expression = text.subSequence(start, end);
		CharSequence annotation = getAnnotation(expression);
		CharSequence word = getWord(expression);

		Rect wordBounds = new Rect();
		paint.getTextBounds(word.toString(), 0, word.length(), wordBounds);

		Rect annotationBounds = new Rect();
		paint.getTextBounds(annotation.toString(), 0, annotation.length(), annotationBounds);
		if (fm != null) {
			float textSize = paint.getTextSize();

			fm.top = -(int)(textSize + textSize *  ANNOTATION_PORTION);
//			fm.bottom = wordBounds.bottom;
			fm.bottom = (int)(textSize *  ANNOTATION_PORTION);

			fm.ascent = fm.top;
			fm.descent = fm.bottom;
		}

		return computeSize(paint, word, annotationPaint, annotation);
	}

	private int computeSize(Paint wordPaint, CharSequence word,
			Paint annotationPaint, CharSequence annotation) {
		float wordWidth = wordPaint.measureText(word, 0, word.length());
		float annotationWidth = annotationPaint.measureText(
				annotation.toString(), 0, annotation.length());
		return Math.round(Math.max(wordWidth, annotationWidth));
	}

	private CharSequence getAnnotation(CharSequence expression) {
		Pattern p = Pattern.compile(PATTERN_EXPRESSION);
		Matcher m = p.matcher(expression);
		m.find();
		String s = m.group(2);
		return s;
	}

	private CharSequence getWord(CharSequence expression) {
		Pattern p = Pattern.compile(PATTERN_EXPRESSION);
		Matcher m = p.matcher(expression);
		m.find();
		String s = m.group(1);

		return BidiUtilities.getTextWithBidiChinese(s, mIsLTR);
	}
}

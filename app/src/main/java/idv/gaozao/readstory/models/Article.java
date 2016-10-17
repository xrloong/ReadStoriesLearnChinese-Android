package idv.gaozao.readstory.models;

import idv.gaozao.readstory.AnnotationString;


public class Article {
	private AnnotationString mTitle = new AnnotationString();
	private AnnotationString mTranslation = new AnnotationString();
	private AnnotationString mMeaning = new AnnotationString();
	private AnnotationString mContent = new AnnotationString();

	private boolean mIsRTL;
	public Article() {
	}

	public void setTitle(AnnotationString title) {
		mTitle = title;
	}

	public void setTranslation(AnnotationString translation) {
		mTranslation = translation;
	}

	public void setMeaning(AnnotationString meaning) {
		mMeaning = meaning;
	}

	public void setContent(AnnotationString content) {
		mContent = content;
	}

	public AnnotationString getTitle() {
		return mTitle;
	}

	public AnnotationString getTranslation() {
		return mTranslation;
	}

	public AnnotationString getMeaning() {
		return mMeaning;
	}

	public AnnotationString getContent() {
		return mContent;
	}

	public void toRTL() {
		if(mTitle != null) {
			mTitle.toRTL();
		}
		if(mTranslation != null) {
			mTranslation.toRTL();
		}
		if(mMeaning != null) {
			mMeaning.toRTL();
		}
		if(mContent != null) {
			mContent.toRTL();
		}
		mIsRTL = true;
	}

	public boolean isRTL() {
		return mIsRTL;
	}
}

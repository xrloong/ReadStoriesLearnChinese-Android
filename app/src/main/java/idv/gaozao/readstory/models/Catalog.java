package idv.gaozao.readstory.models;

import idv.gaozao.readstory.AnnotationString;

import java.util.ArrayList;
import java.util.List;

import android.text.SpannableString;

public class Catalog {

	public static class CatalogItem {
		private AnnotationString mTitle = new AnnotationString();
		private String mKey = "";
		public CatalogItem(AnnotationString title, String key) {
			mTitle = title;
			mKey = key;
		}

		@Override
		public String toString() {
			return mTitle.getString();
		}

		public String getKey() {
			return mKey;
		}

		public SpannableString getTitle() {
			return mTitle.getSpannableString(true);
		}
	}

	private List<CatalogItem> mList = new ArrayList<CatalogItem>();

	public void clear() {
		mList.clear();
	}
/*
	public void add(AnnotationString title, String key) {
		mList.add(new CatalogItem(title, key));
	}
*/
	public void add(CatalogItem item) {
		mList.add(item);
	}

	public List<CatalogItem> getList() {
		return mList;
	}
}

package idv.gaozao.readstory.models;

public class ReadStories {
	private Article mArticle;
	private Catalog mCatalog;
	private ReadStoriesMessage mMessage;

	public void setArticle(Article article) {
		mArticle = article;
	}

	public void setCatalog(Catalog catalog) {
		mCatalog = catalog;
	}

	public void setMessage(ReadStoriesMessage message) {
		mMessage = message;
	}

	public Article getArticle() {
		return mArticle;
	}

	public Catalog getCatalog() {
		return mCatalog;
	}

	public ReadStoriesMessage getMessage() {
		return mMessage;
	}

	public boolean isNotSupported() {
		if(mMessage != null && mMessage.isNotSupport()) {
			return true;
		}

		return false;
	}
}

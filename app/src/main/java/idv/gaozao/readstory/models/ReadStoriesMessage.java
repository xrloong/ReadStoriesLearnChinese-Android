package idv.gaozao.readstory.models;

public class ReadStoriesMessage {
	private static final String MESSAGE_TYPE_NOT_SUPPORT = "NOT-SUPPORT";
	private static final String MESSAGE_TYPE_SUCCESS = "SUCCESS";

	private String mType = MESSAGE_TYPE_NOT_SUPPORT;

	public void setType(String type) {
		mType = type;
	}

	public boolean isSuccess() {
		return MESSAGE_TYPE_SUCCESS.equals(mType);
	}

	public boolean isNotSupport() {
		return MESSAGE_TYPE_NOT_SUPPORT.equals(mType);
	}
}

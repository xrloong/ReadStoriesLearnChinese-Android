package idv.gaozao.readstory.widget;

import idv.gaozao.readstory.XmlDataParser;
import idv.gaozao.readstory.models.ReadStories;
import idv.gaozao.readstory.utils.ReadStoriesUtilities;
import android.os.AsyncTask;

public class ReadStoriesAsyncTask extends AsyncTask<String, Object, ReadStories> {
	@Override
	protected ReadStories doInBackground(String... urls) {
		String url = urls[0];
		String pageContent = ReadStoriesUtilities.getOnlinePage(url);

		ReadStories readStories = XmlDataParser.loadReadStories(pageContent);
		return readStories;
	}

	@Override
	protected void onPostExecute(ReadStories readStories) {
	}

}

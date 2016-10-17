package idv.gaozao.readstory.widget;

import idv.gaozao.readstory.utils.ReadStoriesUtilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;

public class RetrieveOnlinePageAndWriteFileAsyncTask extends AsyncTask<Void, Void, Void> {
	private String mUrl;
	private File mFile;

	public RetrieveOnlinePageAndWriteFileAsyncTask(Context context, String url, File file) {
		mUrl = url;
		mFile = file;
	}

	@Override
	protected Void doInBackground(Void... urls) {
		String pageContent = ReadStoriesUtilities.getOnlinePage(mUrl);
		try {
			FileWriter fileWriter = new FileWriter(mFile);
			fileWriter.write(pageContent);
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
package idv.gaozao.readstory.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.FileObserver;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import idv.gaozao.readstory.Logger;
import idv.gaozao.readstory.R;
import idv.gaozao.readstory.XmlDataParser;
import idv.gaozao.readstory.models.Catalog;
import idv.gaozao.readstory.models.Catalog.CatalogItem;
import idv.gaozao.readstory.models.ReadStories;
import idv.gaozao.readstory.utils.PreferencesUtilities;
import idv.gaozao.readstory.utils.ReadStoriesUtilities;
import idv.gaozao.readstory.utils.Utilities;
import idv.gaozao.readstory.widget.RetrieveOnlinePageAndWriteFileAsyncTask;

public class SelectArticleActivity extends AppCompatActivity {
	private static final String LOG_TAG = Logger.getLogTag(SelectArticleActivity.class);

	private static class CatalogAdapter extends BaseAdapter {
		private Catalog mCatalog;
		public CatalogAdapter(Catalog catalog) {
			mCatalog = catalog;
		}

		@Override
		public int getCount() {
			return mCatalog.getList().size();
		}

		@Override
		public Catalog.CatalogItem getItem(int position) {
			return mCatalog.getList().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Catalog.CatalogItem item = getItem(position);
			Context context = parent.getContext();
			TextView view = new TextView(context);
			view.setText(item.getTitle());
			return view;
		}
		
	};


	@BindView(R.id.toolbar)			Toolbar mToolbar;
	@BindView(android.R.id.list)	ListView mListView;
	private Unbinder mUnbinder;

	private FileObserver mArticleObserver = null;
	private Dialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.select_article);

		ButterKnife.setDebug(true);
		mUnbinder = ButterKnife.bind(this);

		ProgressDialog progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_HORIZONTAL);
		mDialog = progressDialog;

		setupToolbar();
		showHelpWhenLaunchIfFirstTime();
		setupLanguageIfNoSet();
	}

	@Override
	protected void onDestroy (){
		unregisterFileObserver();

		if(mUnbinder != null) {
			mUnbinder.unbind();
			mUnbinder = null;
		}

		super.onDestroy();
	}

	private void setupToolbar() {
		Toolbar toolbar = mToolbar;
		onCreateToolbarMenu(toolbar);
		onPrepareToolbarMenu(toolbar.getMenu());
		toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				return onToolbarItemSelected(item);
			}
		});
		mToolbar.setTitle(R.string.app_name);
	}

	private void setupLanguageIfNoSet() {
		if(PreferencesUtilities.isArticleLanguageSet(this)) {
			String langCode = PreferencesUtilities.getArticleLanguage(this);
			registerFileObserver(langCode);
			loadDataAndUpdateUI();
		} else {
			chooseArticleLanaguage();
		}
	}

	private void registerFileObserver(final String fileName) {
		File file = getFile(fileName);
		mArticleObserver = new FileObserver(file.getParent()) {
			@Override
			public void onEvent(int event, String path) {
				if(event == FileObserver.CLOSE_WRITE && (path != null && path.equals(fileName))) {
					loadDataAndUpdateUI();
				}
			}
		};
		mArticleObserver.startWatching();
	}

	private void unregisterFileObserver() {
		if(mArticleObserver != null) {
			mArticleObserver.stopWatching();
			mArticleObserver = null;
		}
	}

	private void loadDataAndUpdateUI() {
		String langCode = PreferencesUtilities.getArticleLanguage(this);
		File file = getFile(langCode);
//		mDialog.show();
		final ReadStories readStories = XmlDataParser.loadReadStories(file);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				setupView(readStories);
				mDialog.dismiss();
			}
		});
	}

	private void requestToRetrieveArticle() {
		if(Utilities.isNetworkAvailable(this)) {
			String langCode = PreferencesUtilities.getArticleLanguage(this);

			mDialog.show();
			String url = ReadStoriesUtilities.getUrlArticleList(langCode);
			RetrieveOnlinePageAndWriteFileAsyncTask task = new RetrieveOnlinePageAndWriteFileAsyncTask(this, url, getFile(langCode));
			task.execute();
		} else {
			Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.dialog_title_network_not_available);
			builder.setMessage(R.string.dialog_message_network_not_available);
			builder.setPositiveButton(android.R.string.ok, null);
			builder.create().show();
		}
	}

	private void setupView(ReadStories readStories) {
		if(readStories.isNotSupported()) {
			Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.dialog_title_not_supported);
			builder.setMessage(R.string.dialog_message_not_supported);
			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			builder.create().show();
		}

		Catalog catalog = readStories.getCatalog();

		if(catalog == null) {
			return;
		}

		final List<CatalogItem> list = catalog.getList();

		CatalogAdapter adapter = new CatalogAdapter(catalog);
		mListView.setAdapter(adapter);

		ListView listView = mListView;
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CatalogItem item = list.get(position);

				Intent intent = new Intent(SelectArticleActivity.this, ViewArticleActivity.class);
				intent.putExtra(ViewArticleActivity.ARTICLE_KEY, item.getKey());
				startActivity(intent);
			}
		});
	}

	private void showHelpWhenLaunchIfFirstTime() {
		boolean showHelpWhenLauncher = PreferencesUtilities.getShowHelp(this);

		if(showHelpWhenLauncher) {
			HelpActivity.startHelp(this);
		}

		PreferencesUtilities.setShowHelp(this, false);
	}

	private void onCreateToolbarMenu(Toolbar toolbar) {
		toolbar.inflateMenu(R.menu.activity_select_article);
	}

	private void onPrepareToolbarMenu(Menu menu) {
	}

	private boolean onToolbarItemSelected(MenuItem item) {
		boolean isHandled = false;
		switch(item.getItemId()) {
			case R.id.action_help:
				HelpActivity.startHelp(this);
				break;
			case R.id.action_change_article_language:
				chooseArticleLanaguage();
				break;
			case R.id.action_refresh:
				requestToRetrieveArticle();
				break;
		}
		return isHandled;
	}

	private static List<LocaleItem> getSupportedLanguages(Context context) {
		final String[] langCodes = context.getResources().getStringArray(R.array.locales);
		List<LocaleItem> languageList = new ArrayList<>();
		for(String langCode : langCodes) {
			languageList.add(new LocaleItem(langCode));
		}
		return languageList;
	}

	private void chooseArticleLanaguage() {
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.menu_change_article_language);

		final List<LocaleItem> localeList = getSupportedLanguages(this);
		ArrayAdapter<LocaleItem> adapter = new ArrayAdapter<LocaleItem>(this, android.R.layout.simple_list_item_1, localeList);
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which < 0 ||  localeList.size() <= which) {
					which = 0;
				}
				Locale locale = localeList.get(which).getLocale();
				PreferencesUtilities.setArticleLanguage(SelectArticleActivity.this, locale.getLanguage());

				onArticleLanguageChanged();

				dialog.dismiss();
			}			
		});
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				if(!PreferencesUtilities.isArticleLanguageSet(SelectArticleActivity.this)) {
					PreferencesUtilities.setDefaultArticleLanguage(SelectArticleActivity.this);
					onArticleLanguageChanged();
				}
			}
		});
		builder.create().show();
	}

	private void onArticleLanguageChanged() {
		Logger.i(LOG_TAG, "onArticleLanguageChanged()");

		String langCode = PreferencesUtilities.getArticleLanguage(this);

		Logger.i(LOG_TAG, "the current article locale is: %s", langCode);

		if(mArticleObserver != null) {
			unregisterFileObserver();
		}
		registerFileObserver(langCode);

		File file = getFile(langCode);
		if(!file.exists()) {
			requestToRetrieveArticle();
		} else {
			loadDataAndUpdateUI();
		}

//		requestToRetrieveArticle();
	}

	private File getFile(String fileName) {
		File file = new File(getCacheDir(), fileName);
		return file;
	}
}

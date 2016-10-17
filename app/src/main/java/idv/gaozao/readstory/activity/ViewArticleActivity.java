package idv.gaozao.readstory.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.FileObserver;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import idv.gaozao.readstory.Logger;
import idv.gaozao.readstory.R;
import idv.gaozao.readstory.XmlDataParser;
import idv.gaozao.readstory.models.Article;
import idv.gaozao.readstory.models.ReadStories;
import idv.gaozao.readstory.utils.ReadStoriesUtilities;
import idv.gaozao.readstory.utils.Utilities;
import idv.gaozao.readstory.widget.RetrieveOnlinePageAndWriteFileAsyncTask;

public class ViewArticleActivity extends AppCompatActivity {
	private static final String LOG_TAG = Logger.getLogTag(ViewArticleActivity.class);

	public static final String ARTICLE_RESOURCE = "article_resource";
	public static final String ARTICLE_KEY = "article_key";

	@BindView(R.id.toolbar)			Toolbar mToolbar;
	@BindView(R.id.title_content)	TextView mTextViewTitle;
	@BindView(R.id.translation_content)	TextView mTextViewTranslation;
	@BindView(R.id.meaning_content)	TextView mTextViewMeaning;
	@BindView(R.id.text_content)	TextView mTextViewContent;
	private Unbinder mUnbinder;

	private String mKey;
	private File mFile;
	private Article mArticle;
	private boolean mShowAnnotation = true;

	private FileObserver mArticleObserver = null;
	private Dialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.read_article);

		ButterKnife.setDebug(true);
		mUnbinder = ButterKnife.bind(this);

		Intent intent = getIntent();
		mKey = intent.getStringExtra(ARTICLE_KEY);
		mFile = Utilities.getArticleFile(this, mKey);

		mArticleObserver = new FileObserver(mFile.getParent()) {
			@Override
			public void onEvent(int event, String path) {
				Logger.i(LOG_TAG, "onEvent(%d, %s)", event, path);
				if(event == FileObserver.CLOSE_WRITE && (path != null && path.equals(mKey))) {
					loadDataAndUpdateUI();
					mDialog.dismiss();
				}
			}
		};
		mArticleObserver.startWatching();

		ProgressDialog progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_HORIZONTAL);
		mDialog = progressDialog;

		setupToolbar();

		if(!mFile.exists()) {
			requestToRetrieveArticle();
		} else {
			loadDataAndUpdateUI();
		}
	}

	@Override
	protected void onDestroy (){
		if(mArticleObserver == null) {
			mArticleObserver.stopWatching();
			mArticleObserver = null;
		}

		if(mUnbinder != null) {
			mUnbinder.unbind();
			mUnbinder = null;
		}

		super.onDestroy();
	}

	private void setupToolbar() {
		final Toolbar toolbar = mToolbar;
		onCreateToolbarMenu(toolbar);
		onPrepareToolbarMenu(toolbar.getMenu());
		toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				boolean isHandled = onToolbarItemSelected(item);
				onPrepareToolbarMenu(toolbar.getMenu());
				return isHandled;
			}
		});
	}

	private void loadDataAndUpdateUI() {
//		mDialog.show();
		final ReadStories readStories = XmlDataParser.loadReadStories(mFile);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				setupView(readStories);
//				mDialog.dismiss();
			}
		});

	}

	private void requestToRetrieveArticle() {
		if(mKey == null) {
			Logger.i(LOG_TAG, "loadData(), mKey == null.");
		}

		if(Utilities.isNetworkAvailable(this)) {
			mDialog.show();
	
			String url = ReadStoriesUtilities.getUrlArticle(mKey);
			RetrieveOnlinePageAndWriteFileAsyncTask task = new RetrieveOnlinePageAndWriteFileAsyncTask(this, url, mFile);
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

		mArticle = readStories.getArticle();
		updateText();
	}

	private void onCreateToolbarMenu(Toolbar toolbar) {
		toolbar.inflateMenu(R.menu.activity_view_article);
	}

	private void onPrepareToolbarMenu(Menu menu) {
		MenuItem item = null;
		if(mShowAnnotation) {
			item = menu.findItem(R.id.action_show_annotation);
			item.setVisible(false);
			item = menu.findItem(R.id.action_hide_annotation);
			item.setVisible(true);
		} else {
			item = menu.findItem(R.id.action_show_annotation);
			item.setVisible(true);
			item = menu.findItem(R.id.action_hide_annotation);
			item.setVisible(false);
		}
	}

	private boolean onToolbarItemSelected(MenuItem item) {
		boolean isHandled = false;
		switch(item.getItemId()) {
			case R.id.action_show_annotation:
				mShowAnnotation = true;
				updateText();
				isHandled = true;
				break;
			case R.id.action_hide_annotation:
				mShowAnnotation = false;
				updateText();
				isHandled = true;
				break;
			case R.id.action_help:
				HelpActivity.startHelp(this);
				break;
			case R.id.action_refresh:
				requestToRetrieveArticle();
				break;
		}
		return isHandled;
	}

	private void updateText() {
		if(mArticle == null) {
			Log.d("", "updateText() mArticle == null");
			return;
		}

		mToolbar.setTitle(mArticle.getTitle().getSpannableString(mShowAnnotation));

		mTextViewTitle.setText(mArticle.getTitle().getSpannableString(mShowAnnotation), TextView.BufferType.SPANNABLE);
		if(mArticle.isRTL()) {
			mTextViewTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.rtl_ttb, 0);
//			imageView.setBackgroundResource(R.drawable.rtl_ttb);
		}
		else {
			mTextViewTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ltr_ttb, 0, 0, 0);
//			imageView.setBackgroundResource(R.drawable.ltr_ttb);
		}

		mTextViewTranslation.setText(mArticle.getTranslation().getSpannableString(mShowAnnotation), TextView.BufferType.SPANNABLE);

		mTextViewMeaning.setText(mArticle.getMeaning().getSpannableString(mShowAnnotation), TextView.BufferType.SPANNABLE);

		mTextViewContent.setText(mArticle.getContent().getSpannableString(mShowAnnotation), TextView.BufferType.SPANNABLE);
	}
}

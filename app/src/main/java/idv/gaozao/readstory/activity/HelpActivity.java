package idv.gaozao.readstory.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.BindView;
import idv.gaozao.readstory.AnnotationString;
import idv.gaozao.readstory.R;
import idv.gaozao.readstory.XmlDataParser;

public class HelpActivity extends AppCompatActivity {
	@BindView(R.id.help)		TextView mTextView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);

		AnnotationString helpMessage = XmlDataParser.readHelpText(this);
		mTextView.setText(helpMessage.getSpannableString(true), TextView.BufferType.SPANNABLE);
	}

	public static void startHelp(Context context) {
		Intent intent = new Intent(context, HelpActivity.class);
		context.startActivity(intent);
	}
}

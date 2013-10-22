package au.com.jtribe.conferencelist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class EventDetailActivity extends Activity {
	private static final String TAG = "EventDetailActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		setContentView(R.layout.activity_event_detail);
		
		Intent intent = getIntent();
		String title = intent.getExtras().getString("title");
		setTitle(title);
		
		TextView descTextView = (TextView) findViewById(R.id.desc);
		String desc = intent.getStringExtra("desc");
		descTextView.setText(desc);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

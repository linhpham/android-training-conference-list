package au.com.jtribe.conferencelist;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import au.com.jtribe.conferencelist.adapter.ConferenceListAdapter;
import au.com.jtribe.conferencelist.domain.ConferenceItem;

public class MainActivity extends ListActivity {

	private static final String TAG = "MainActivity";
	
	private List<ConferenceItem> items = new ArrayList<ConferenceItem>();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        //Display the activity_main: Hello World!
        //setContentView(R.layout.activity_main);
        
        //Display a list
        //String[] elements = {"Line 1", "Line 2"};
        //setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, elements));
        
        //Display ConferenceItem
//        List<ConferenceItem> items = new ArrayList<ConferenceItem>();
//        for(int i = 0; i < 20; i++) {
//        	items.add(createConferenceItem(i));
//        }

        //List<ConferenceItem> items = loadConferenceListItems();
        //setListAdapter(new ConferenceListAdapter(this, R.layout.custom_list_item, items));
        
        //Practical session: tap on the item in the list, sends info to the detail view
        ListView listView = getListView();
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d(TAG, "onItemClick: position: " + position);
				ConferenceItem selectedItem = items.get(position);
				Log.d(TAG, "onItemClick: item title: " + selectedItem.getTitle());
				
				Intent intent = new Intent(MainActivity.this, EventDetailActivity.class);
				intent.putExtra("title", selectedItem.getTitle());
				intent.putExtra("desc", selectedItem.getDescription());
				startActivity(intent);
			}
		});
        
        new LoadJSONTask().execute();
    }
    
    private class LoadJSONTask extends AsyncTask<Void, Void, Void> {
    	private ProgressDialog progressDialog;
    	
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(MainActivity.this, "", "Loading. Please wait...", true);
		}
		
		private String loadYowJSONFromFile() throws IOException {
			InputStream is = getAssets().open("YOW-Melbourne.json");
    		String result = readInputStreamAsString(is);
    		return result;
		}
		
		private String loadYowJSONFromInternet() throws ClientProtocolException, IOException {
			//Practical session: Load the file from the Internet
    		//-> needs to declare the Internet permission in the Application Manifest
				HttpClient hc = new DefaultHttpClient();
	    		HttpGet get = new HttpGet("https://s3.amazonaws.com/training-android/YOW-Melbourne.json");
	    		HttpResponse response = hc.execute(get);
				String result = EntityUtils.toString(response.getEntity());
				return result;
		}

		@Override
		protected Void doInBackground(Void... params) {
			items = new ArrayList<ConferenceItem>();
	    	try {
	    		String result = loadYowJSONFromFile();
	    		
	    		JSONObject root = new JSONObject(result);
	    		JSONArray sessions = root.getJSONArray("sessions");
	    		for (int i = 0; i < sessions.length(); i++) {
	    			JSONObject session = sessions.getJSONObject(i);
	    			ConferenceItem item = new ConferenceItem();
	    			item.setTitle(session.getString("title"));
	    			if (session.has("stream")) {
	    				item.setStream(session.getString("stream"));
	    			}
	    			
	    			if (session.has("speaker")) {
	    				JSONArray speakers = session.getJSONArray("speakers");
	    				item.setPresenter(speakers.getJSONObject(0).getString("name"));
	    			}
	    			
	    			if (session.has("abstract")) {
	    				item.setDescription(session.getString("abstract"));
	    			}
	    			
	    			items.add(item);
	    		}
	    	} catch (Exception e) {
	    		Log.e(TAG, "Problem loading ConferenceItem JSON.", e);
	    	}
	    	
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			Log.d(TAG, "onPostExecute");
			progressDialog.dismiss();
			setListAdapter(new ConferenceListAdapter(MainActivity.this, R.layout.custom_list_item, items));
		}
    }
    
    private ConferenceItem createConferenceItem(int value) {
    	ConferenceItem i = new ConferenceItem();
    	i.setPresenter("Daniel " + value);
    	i.setTitle("Title " + value);
    	
    	return i;
    }
    
    private List<ConferenceItem> loadConferenceListItems() {
    	items = new ArrayList<ConferenceItem>();
    	try {
    		InputStream is = getAssets().open("YOW-Melbourne.json");
    		String result = readInputStreamAsString(is);
    		
    		JSONObject root = new JSONObject(result);
    		JSONArray sessions = root.getJSONArray("sessions");
    		for (int i = 0; i < sessions.length(); i++) {
    			JSONObject session = sessions.getJSONObject(i);
    			ConferenceItem item = new ConferenceItem();
    			item.setTitle(session.getString("title"));
    			if (session.has("stream")) {
    				item.setStream(session.getString("stream"));
    			}
    			
    			if (session.has("speakers")) {
    				JSONArray speakers = session.getJSONArray("speakers");
    				item.setPresenter(speakers.getJSONObject(0).getString("name"));
    			}
    			
    			items.add(item);
    		}
    	} catch (Exception e) {
    		Log.e(TAG, "Problem loading ConferenceItem JSON.", e);
    	}
    	
    	return items;
    }
    
	public static String readInputStreamAsString(InputStream is)
			throws IOException {
		final char[] buffer = new char[0x10000];
		StringBuilder out = new StringBuilder();
		Reader in = new InputStreamReader(is, "UTF-8");
		int read;
		do {
			read = in.read(buffer, 0, buffer.length);
			if (read > 0) {
				out.append(buffer, 0, read);
			}
		} while (read >= 0);
		return out.toString();
	}

    @Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
	}


	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d(TAG, "onPause");
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG, "onResume");
	}


	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.d(TAG, "onStart");
	}


	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d(TAG, "onStop");
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.d(TAG, "onRestart");
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(this, EventDetailActivity.class);
		startActivity(intent);
		return true;
	}
    
}

package au.com.jtribe.conferencelist.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import au.com.jtribe.conferencelist.R;
import au.com.jtribe.conferencelist.domain.ConferenceItem;

public class ConferenceListAdapter extends ArrayAdapter<ConferenceItem> {

	private List<ConferenceItem> items;
	private Context context;
	private int conferenceListViewResource;
	
	public ConferenceListAdapter(Context context, int conferenceListViewResource,
			List<ConferenceItem> objects) {
		super(context, conferenceListViewResource, objects);
		this.context = context;
		this.items = objects;
		this.conferenceListViewResource = conferenceListViewResource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//return super.getView(position, convertView, parent);
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(conferenceListViewResource, null);
		}
		
		ConferenceItem o = items.get(position);
		
		TextView tt = (TextView) v.findViewById(R.id.topText);
		TextView bt = (TextView) v.findViewById(R.id.bottomText);
		
		tt.setText(o.getTitle());
		bt.setText(o.getPresenter());
		
		return v;
	}
}

package in.digitrack.eventfinder;

import java.util.ArrayList;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EventListFragment extends ListFragment {
	
	ArrayList<Event> mEvents;
	ThumbnailDownloader<ImageView> mThumbnailThread;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
		mEvents = new ArrayList<Event>();
		updateItems();

		mThumbnailThread = new ThumbnailDownloader<ImageView>(new Handler());
		mThumbnailThread.setListener(new ThumbnailDownloader.Listener<ImageView>() {
			@Override
			public void onThumbnailDownloaded(ImageView imageView, Bitmap thumbNail, String url) {
				if(isVisible() && thumbNail != null) {
					if(((BitmapDrawable)imageView.getDrawable()).getBitmap() == null)
						imageView.setImageBitmap(thumbNail);
				}
				ImageCache.getInstance().addToCache(url, thumbNail);
			}
		});
		mThumbnailThread.start();
		mThumbnailThread.getLooper();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setAdapter();
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	public void updateItems() {
		new FetchEventsTask().execute();
	}
	
	private void setAdapter() {
		if(getActivity() == null) {
			return;
		}
		if(mEvents != null) {
			setListAdapter(new EventListAdapter(mEvents));
		}
		else {
			setListAdapter(null);
		}
	}
	
	private class EventListAdapter extends ArrayAdapter<Event> {

		public EventListAdapter(ArrayList<Event> events) {
			super(getActivity(), 0, events);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.eventlist_item, parent, false);
			}
			
			ImageView imageView = (ImageView)convertView.findViewById(R.id.event_imgView);
			TextView eventNameTxtView = (TextView)convertView.findViewById(R.id.eventName_txtView);
			TextView eventDateTxtView = (TextView)convertView.findViewById(R.id.eventDate_txtView);
			TextView eventPlaceTxtView = (TextView)convertView.findViewById(R.id.eventPlace_txtView);
			TextView eventFreeTxtView = (TextView)convertView.findViewById(R.id.eventFree_txtView);
			
			Event event = getItem(position);
			
			eventNameTxtView.setText(event.getName());
			eventDateTxtView.setText(event.getStartTime());
			eventPlaceTxtView.setText(event.getVenueName() + ", " + event.getVenueCity());
			String eventTicketStatus = "Paid";
			if(event.isFree())
				eventTicketStatus = "Free";
			eventFreeTxtView.setText(eventTicketStatus);
			
			String logoUrl = event.getLogoUrl();
			if(logoUrl != null && !logoUrl.equals("null") && !logoUrl.trim().equals("")) {
				Bitmap bitmap = ImageCache.getInstance().getFromCache(logoUrl);
			    if (bitmap != null) {
			    	imageView.setImageBitmap(bitmap);
			    } else {
			    	imageView.setImageBitmap(null);
			    	mThumbnailThread.queueThumbnail(imageView, logoUrl);
			    }
			} else {
				imageView.setImageBitmap(null);
			}
			
			return convertView;
		}
	}
	
	private class FetchEventsTask extends AsyncTask<Void, Void, String> {
		private ProgressDialog dialog;
		
		@Override
		protected String doInBackground(Void... params) {
			if(getActivity() == null) return null;
			return new DataFetchr().fetchData();
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(getActivity());
	        dialog.setMessage("Loading");
	        dialog.show();
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			EventData.getInstance().setData(result);
			mEvents = EventData.getInstance().getData();
			setAdapter();
		}
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mThumbnailThread.clearQueue();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mThumbnailThread.quit();
	}
}

package in.digitrack.eventfinder;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EventListFragment extends ListFragment {
	
	ArrayList<Event> mEvents;
	ThumbnailDownloader<ImageView> mThumbnailThread;
	
	public static final String ADAPTER_PARAM = "in.digitrack.eventfinder.adapterParam";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
		mEvents = EventData.getInstance().getData();

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
		setDefaultAdapter();
		getActivity().setTitle(R.string.discover_events);
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.event_menu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.discover_menu:
			setDefaultAdapter();
			return true;
		case R.id.favourite_menu:
			setFavouritesAdapter();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {
	        if (resultCode == Activity.RESULT_OK) {
	            String param = data.getStringExtra(ADAPTER_PARAM);
	            if(param.equals(getString(R.string.discover_events))) {
	            	setDefaultAdapter();
	            } else {
	            	setFavouritesAdapter();
	            }
	        }
	    }
	}
	
	private void setFavouritesAdapter() {
		ArrayList<Event> favouriteEvents = new ArrayList<Event>();
		if(getActivity() != null) {
			favouriteEvents = Favourites.getInstance(getActivity()).getEvents();
			if(favouriteEvents != null) {
				setListAdapter(new EventListAdapter(favouriteEvents));
				getActivity().setTitle(R.string.favourite_events);
			} else {
				Toast.makeText(getActivity(), "Favourites list is empty", Toast.LENGTH_SHORT).show();
				setDefaultAdapter();
			}
		}
	}
	
	private void setDefaultAdapter() {
		if(getActivity() == null) {
			return;
		}
		if(mEvents != null) {
			setListAdapter(new EventListAdapter(mEvents));
			getActivity().setTitle(R.string.discover_events);
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
			
			final Event event = getItem(position);
			
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
			
			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), EventDetailsActivity.class);
					intent.putExtra(EventDetailsFragment.EXTRA_DATA, event);
					startActivityForResult(intent, 0);
				}
			});
			
			return convertView;
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

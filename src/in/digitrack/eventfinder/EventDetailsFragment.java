package in.digitrack.eventfinder;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class EventDetailsFragment extends Fragment {
	public static final String EXTRA_DATA = "in.digitrack.eventfinder.extraData";
	private Event mEvent;
	private ImageView imgView;
	private Button favouriteBtn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mEvent = (Event) getActivity().getIntent().getSerializableExtra(EXTRA_DATA);
		setRetainInstance(true);
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.eventdetails_fragment, container, false);
		if(mEvent == null) return v;
		
		imgView = (ImageView) v.findViewById(R.id.eventDetail_imgView);
		Bitmap bmp = ImageCache.getInstance().getFromCache(mEvent.getLogoUrl());
		if(bmp != null)
			imgView.setImageBitmap(bmp);
		else {
			new FetchImageTask().execute();
		}
		
		
		TextView nameTxtView = (TextView) v.findViewById(R.id.eventDetail_nameTxtView);
		TextView organizerTxtView = (TextView) v.findViewById(R.id.eventDetail_organizerTxtView);
		TextView venueTxtView = (TextView) v.findViewById(R.id.eventDetail_venueTxtView);
		TextView addressTxtView = (TextView) v.findViewById(R.id.eventDetail_addressTxtView);
		TextView startTimeTxtView = (TextView) v.findViewById(R.id.eventDetail_startTimeTxtView);
		TextView endTimeTextView = (TextView) v.findViewById(R.id.eventDetail_endTimeTxtView);
		TextView ticketPriceTxtView = (TextView) v.findViewById(R.id.eventDetail_ticketPriceTxtView);
		favouriteBtn = (Button) v.findViewById(R.id.favourite_btn);
		
		nameTxtView.setText(mEvent.getName());
		organizerTxtView.setText(mEvent.getOrganizer());
		venueTxtView.setText(mEvent.getVenueName());
		addressTxtView.setText(mEvent.getVenueAddress());
		startTimeTxtView.setText("Starts on: " + mEvent.getStartTime());
		endTimeTextView.setText("Ends on: " + mEvent.getEndTime());
		if(mEvent.isFree())
			ticketPriceTxtView.setText("Free");
		else
			ticketPriceTxtView.setText(mEvent.getTicketPrice());
		
		
		setFavouriteBtn();
		favouriteBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Favourites favourites = Favourites.getInstance(getActivity());
				if(favourites.isFavourite(mEvent.getId())) {
					favourites.removeEvent(mEvent.getId());
				} else {
					favourites.addEvent(mEvent);
				}
				setFavouriteBtn();
			}
		});
		
		return v;
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
			finishActivity(getString(R.string.discover_events));
			return true;
		case R.id.favourite_menu:
			finishActivity(getString(R.string.favourite_events));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void finishActivity(String str) {
		Bundle args = new Bundle();
		args.putString(EventListFragment.ADAPTER_PARAM, str);
		Intent intent = getActivity().getIntent(); 
		intent.putExtras(args);
		getActivity().setResult(Activity.RESULT_OK, intent);
		getActivity().finish();
	}
	
	private void setFavouriteBtn() {
		if(Favourites.getInstance(getActivity()).isFavourite(mEvent.getId())){
			favouriteBtn.setText(getString(R.string.unfavourite));
		} else {
			favouriteBtn.setText(getString(R.string.favourite));
		}
	}
	
	private class FetchImageTask extends AsyncTask<Void, Void, Bitmap> {
		
		@Override
		protected Bitmap doInBackground(Void... params) {
			if(getActivity() == null) return null;
			return BitmapFactory.decodeStream(fetch(mEvent.getLogoUrl()));
		}
		
		private InputStream fetch(String urlString) {
			try {
		        DefaultHttpClient httpClient = new DefaultHttpClient();
		        HttpGet request = new HttpGet(urlString);
		        HttpResponse response = httpClient.execute(request);
		        return response.getEntity().getContent();
			}catch(Exception ex) {}
			return null;
	    }
		
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			super.onPostExecute(bitmap);
			if(imgView != null && bitmap != null) {
				imgView.setImageBitmap(bitmap);
				ImageCache.getInstance().addToCache(mEvent.getLogoUrl(), bitmap);
			}
		}
	}
}

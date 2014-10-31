package in.digitrack.eventfinder;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class EventDetailsFragment extends Fragment {
	public static final String EXTRA_DATA = "in.digitrack.eventfinder.extraData";
	private Event mEvent;
	ThumbnailDownloader<ImageView> mThumbnailThread;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mEvent = (Event) getActivity().getIntent().getSerializableExtra(EXTRA_DATA);
		mThumbnailThread = new ThumbnailDownloader<ImageView>(new Handler());
		mThumbnailThread.setListener(new ThumbnailDownloader.Listener<ImageView>() {
			@Override
			public void onThumbnailDownloaded(ImageView imageView, Bitmap thumbNail, String url) {
				if(isVisible() && thumbNail != null) {
					imageView.setImageBitmap(thumbNail);
				}
				ImageCache.getInstance().addToCache(url, thumbNail);
			}
		});
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.eventdetails_fragment, container, false);
		if(mEvent == null) return v;
		
		ImageView imgView = (ImageView) v.findViewById(R.id.eventDetail_imgView);
		Bitmap bmp = ImageCache.getInstance().getFromCache(mEvent.getLogoUrl());
		if(bmp != null)
			imgView.setImageBitmap(bmp);
		else {
			mThumbnailThread.start();
			mThumbnailThread.getLooper();
			mThumbnailThread.queueThumbnail(imgView, mEvent.getLogoUrl());
		}
		
		
		TextView nameTxtView = (TextView) v.findViewById(R.id.eventDetail_nameTxtView);
		TextView organizerTxtView = (TextView) v.findViewById(R.id.eventDetail_organizerTxtView);
		TextView venueTxtView = (TextView) v.findViewById(R.id.eventDetail_venueTxtView);
		TextView addressTxtView = (TextView) v.findViewById(R.id.eventDetail_addressTxtView);
		TextView startTimeTxtView = (TextView) v.findViewById(R.id.eventDetail_startTimeTxtView);
		TextView endTimeTextView = (TextView) v.findViewById(R.id.eventDetail_endTimeTxtView);
		TextView ticketPriceTxtView = (TextView) v.findViewById(R.id.eventDetail_ticketPriceTxtView);
		
		nameTxtView.setText(mEvent.getName());
		organizerTxtView.setText(mEvent.getOrganizer());
		venueTxtView.setText(mEvent.getVenueName());
		addressTxtView.setText(mEvent.getVenueAddress());
		startTimeTxtView.setText(mEvent.getStartTime());
		endTimeTextView.setText(mEvent.getEndTime());
		if(mEvent.isFree())
			ticketPriceTxtView.setText("Free");
		else
			ticketPriceTxtView.setText(mEvent.getTicketPrice());
		
		return v;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if(mThumbnailThread.isAlive())
			mThumbnailThread.clearQueue();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mThumbnailThread.isAlive())
			mThumbnailThread.quit();
	}
	
}

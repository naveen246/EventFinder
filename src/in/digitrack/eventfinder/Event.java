package in.digitrack.eventfinder;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Event {
	private String mName, mLogoUrl, mId, mEventUrl, mVenueName, mVenueAddress, mVenueCity, mStartTime, mEndTime, mTicketPrice, mOrganizer;
	private boolean mIsFree;
	
	public String getName() {
		return mName;
	}
	public void setName(String name) {
		mName = name;
	}
	public String getLogoUrl() {
		return mLogoUrl;
	}
	public void setLogoUrl(String logoUrl) {
		mLogoUrl = logoUrl;
	}
	public String getId() {
		return mId;
	}
	public void setId(String id) {
		mId = id;
	}
	public String getEventUrl() {
		return mEventUrl;
	}
	public void setEventUrl(String url) {
		mEventUrl = url;
	}
	public String getVenueName() {
		return mVenueName;
	}
	public void setVenueName(String venueName) {
		mVenueName = venueName;
	}
	public String getVenueAddress() {
		return mVenueAddress;
	}
	public void setVenueAddress(String venueAddress) {
		mVenueAddress = venueAddress;
	}
	public String getStartTime() {
		return mStartTime;
	}
	
	private String formatDate(String oldDateStr) {
		String newDateStr = oldDateStr;
		try {
			SimpleDateFormat originalFmt = new SimpleDateFormat ("yyyy-MM-dd'T'hh:mm:ss");
			Date date = originalFmt.parse(oldDateStr);
			SimpleDateFormat newFmt = new SimpleDateFormat("MMMM dd, yyyy kk:mm");
			newDateStr = newFmt.format(date);
		} catch(Exception ex) {}
		return newDateStr;
	}
	
	public void setStartTime(String startTime) {
		mStartTime = formatDate(startTime);
	}
	public String getEndTime() {
		return mEndTime;
	}
	public void setEndTime(String endTime) {
		mEndTime = formatDate(endTime);
	}
	public String getTicketPrice() {
		return mTicketPrice;
	}
	public void setTicketPrice(String ticketPrice) {
		mTicketPrice = ticketPrice;
	}
	public boolean isFree() {
		return mIsFree;
	}
	public void setIsFree(boolean isFree) {
		mIsFree = isFree;
	}
	public String getOrganizer() {
		return mOrganizer;
	}
	public void setOrganizer(String organizer) {
		mOrganizer = organizer;
	}
	
	public String toString() {
		return getName();
	}
	public String getVenueCity() {
		return mVenueCity;
	}
	public void setVenueCity(String venueCity) {
		mVenueCity = venueCity;
	}
}

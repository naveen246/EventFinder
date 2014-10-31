package in.digitrack.eventfinder;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public final class EventData {
	private ArrayList<Event> data;
	private static EventData sEventData;
	private Date receivedDate;
	
	private EventData() {
		data = null;
	}
	
	public static EventData getInstance() {
		if(sEventData == null) {
			sEventData = new EventData();
		}
		return sEventData;
	}
	
	public boolean isDataAvailable() {
		if(data == null) return false;
		return true;
	}
	
	public long getLastUpdatedTime() {
		return receivedDate.getTime();
	}
	
	private String getString(JSONObject obj, String... keys) {
		String str = null;
		for(int i = 0; i < keys.length && obj != null && keys[i] != null; i++) {
			if(i == keys.length - 1) {
				str = obj.optString(keys[i]);
				break;
			}
			obj = obj.optJSONObject(keys[i]);
		}
		return str;
	}
	
	private ArrayList<Event> parseEventBriteData(String result) {
		ArrayList<Event> eventList = new ArrayList<Event>();
		try {
			JSONObject jsonObj = (JSONObject) new JSONTokener(result).nextValue();
			JSONArray jsonArr = jsonObj.optJSONArray("events");
			if(jsonArr != null) {
				for(int i = 0; i < jsonArr.length(); i++) {
					Event event = new Event();
					JSONObject eventObj = jsonArr.optJSONObject(i);
					if(eventObj != null) {
						event.setName(getString(eventObj, "name", "text"));
						event.setId(getString(eventObj, "id"));
						event.setEventUrl(getString(eventObj, "url"));
						event.setLogoUrl(getString(eventObj, "logo_url"));
						event.setStartTime(getString(eventObj, "start", "local"));
						event.setEndTime(getString(eventObj, "end", "local"));
						event.setVenueName(getString(eventObj, "venue", "name"));
						String address = getString(eventObj, "venue", "address", "address_1") + " " + 
										 getString(eventObj, "venue", "address", "address_2") + " " +
										 getString(eventObj, "venue", "address", "city") + " " + 
										 getString(eventObj, "venue", "address", "region") + " " +
										 getString(eventObj, "venue", "address", "country");
						event.setVenueAddress(address);
						event.setVenueCity(getString(eventObj, "venue", "address", "city"));
						event.setIsFree(false);
						JSONArray ticketClassArr = eventObj.optJSONArray("ticket_classes");
						if(ticketClassArr != null) {
							JSONObject ticketObj = ticketClassArr.optJSONObject(0);
							if(ticketObj != null) {
								event.setIsFree(ticketObj.optBoolean("free"));
								event.setTicketPrice(getString(ticketObj, "cost", "display"));
							}
						}
						eventList.add(event);
					}
				}
			}
		} catch(JSONException ex) {}
		return eventList;
	}
	
	public void setData(String result) {
		if(result != null) {
			receivedDate = new Date();
			data = parseEventBriteData(result);
		}
	}
	
	public ArrayList<Event> getData() {
		return data;
	}
}

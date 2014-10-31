package in.digitrack.eventfinder;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

public final class Favourites {
	private static Favourites sFavourites;
	private Context mAppContext;
	private SharedPreferences mPrefs;
	
	private Favourites(Context appContext) {
		mAppContext = appContext;
		mPrefs = PreferenceManager.getDefaultSharedPreferences(mAppContext);
	}
	
	public static Favourites getInstance(Context context) {
		if(sFavourites == null) {
			sFavourites = new Favourites(context.getApplicationContext());
		}
		return sFavourites;
	}
	
	public void addEvent(Event event) {
		Editor prefsEditor = mPrefs.edit();
		Gson gson = new Gson();
		String json = gson.toJson(event);
		prefsEditor.putString(event.getId(), json);
		prefsEditor.commit();
	}
	
	public void removeEvent(String eventId) {
		if(mPrefs.contains(eventId)) {
			mPrefs.edit().remove(eventId).commit();
		}
	}
	
	public boolean isFavourite(String eventId) {
		return mPrefs.contains(eventId);
	}
	
	public ArrayList<Event> getEvents() {
		ArrayList<Event> events = new ArrayList<Event>();
		Map<String,?> keys = mPrefs.getAll();

		for(Map.Entry<String,?> entry : keys.entrySet()){
			Gson gson = new Gson();
		    String json = entry.getValue().toString();
		    Event event = gson.fromJson(json, Event.class);
		    events.add(event);
		}
		return events;
	}
	
}

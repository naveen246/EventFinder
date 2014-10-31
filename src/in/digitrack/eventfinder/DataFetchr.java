package in.digitrack.eventfinder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.net.Uri;

public class DataFetchr {
	private static final String ENDPOINT = "https://www.eventbriteapi.com/v3/events/search/";

	private String eventsUrl = Uri.parse(ENDPOINT).buildUpon()
									.appendQueryParameter("venue.city", "Bangalore")
									.appendQueryParameter("token", "****")
									.build().toString();
	
	public String fetchData(){
		return new String(getUrlBytes(eventsUrl));
	}
	
	public byte[] getUrlBytes(String urlString) {
		HttpURLConnection connection = null;
		try {
			URL url = new URL(urlString);
			connection = (HttpURLConnection) url.openConnection();
			InputStream in = connection.getInputStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return null;
			}
			
			byte[] buffer = new byte[1024];
			int bytesRead = 0;
			while((bytesRead = in.read(buffer)) > 0) {
				out.write(buffer, 0, bytesRead);
			}
			out.close();
			return out.toByteArray();
		} catch(IOException ex) {}
		finally {
			if(connection != null) {
				connection.disconnect();
			}
		}
		return null;
	}
}

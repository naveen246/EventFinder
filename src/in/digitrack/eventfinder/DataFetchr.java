package in.digitrack.eventfinder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DataFetchr {
	private static final String EVENTS_URL = "https://www.eventbriteapi.com/v3/events/search/?venue.city=Bangalore&token=****";
	
	public String fetchData(){
		String data = null;
		HttpURLConnection connection = null;
		try {
			URL url = new URL(EVENTS_URL);
			connection = (HttpURLConnection) url.openConnection();
			InputStream in = connection.getInputStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			byte[] buffer = new byte[1024];
			int bytesRead = 0;
			while((bytesRead = in.read(buffer)) > 0) {
				out.write(buffer, 0, bytesRead);
			}
			out.close();
			data = out.toByteArray().toString();
		} catch(IOException ex) {}
		finally {
			if(connection != null) {
				connection.disconnect();
			}
		}
		return data;
	}
}

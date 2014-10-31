package in.digitrack.eventfinder;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

public class ThumbnailDownloader<Token> extends HandlerThread {
	private static final String TAG = "ThumbnailDownloader";
	private static final int MESSAGE_DOWNLOAD = 0;
	
	Handler mHandler;
	Map<Token, String> requestMap = Collections.synchronizedMap(new HashMap<Token, String>());
	Handler mResponseHandler;
	Listener<Token> mListener;
	
	public interface Listener<Token> {
		void onThumbnailDownloaded(Token token, Bitmap thumbNail, String url);
	}
	
	public ThumbnailDownloader(Handler responseHandler) {
		super(TAG);
		mResponseHandler = responseHandler;
	}
	
	public void setListener(Listener<Token> listener) {
		mListener = listener;
	}
	
	@SuppressLint("HandlerLeak")
	@Override
	protected void onLooperPrepared() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == MESSAGE_DOWNLOAD) {
					@SuppressWarnings("unchecked")
					Token token = (Token) msg.obj;
					handleRequest(token);
				}
			}
		};
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

	protected void handleRequest(final Token token) {
		
		final String url = requestMap.get(token);
		if(url == null) return;
	
		final Bitmap bitmap = BitmapFactory.decodeStream(fetch(url));
		
		mResponseHandler.post(new Runnable() {
			@Override
			public void run() {
				if(requestMap.get(token) != url) return;
				requestMap.remove(token);
				mListener.onThumbnailDownloaded(token, bitmap, url);
			}
		});
	}
	
	public void queueThumbnail(Token token, String url) {
		getLooper();
		requestMap.put(token, url);
		mHandler.obtainMessage(MESSAGE_DOWNLOAD, token).sendToTarget();
	}
	
	public void clearQueue() {
		mHandler.removeMessages(MESSAGE_DOWNLOAD);
		requestMap.clear();
	}
}


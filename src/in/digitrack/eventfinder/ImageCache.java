package in.digitrack.eventfinder;

import android.graphics.Bitmap;
import android.util.LruCache;

public final class ImageCache {
	
	private LruCache<String, Bitmap> mMemoryCache;
	private static ImageCache sImageCache;
	
	private ImageCache() {
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 4;

	    mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            // return kilobytes
	            return bitmap.getByteCount() / 1024;
	        }
	    };
	}
	
	public static ImageCache getInstance() {
		if(sImageCache == null) {
			sImageCache = new ImageCache();
		}
		return sImageCache;
	}
	
	public void addToCache(String key, Bitmap bitmap) {
	    if (key != null && bitmap != null && getFromCache(key) == null) {
	        mMemoryCache.put(key, bitmap);
	    }
	}

	public Bitmap getFromCache(String key) {
		if(key != null)
			return mMemoryCache.get(key);
		else return null;
	}
}

package in.digitrack.eventfinder;

import in.digitrack.eventfinder.R;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

public class WelcomeActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.container_activity);
		Fragment fragment = new WelcomeFragment();
		
		FragmentManager fm = getFragmentManager();
		fm.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
	}
}

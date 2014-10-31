package in.digitrack.eventfinder;

import android.app.Fragment;

public class WelcomeActivity extends SingleFragmentActivity {
	
	public Fragment createFragment() {
		return new WelcomeFragment();
	}
}

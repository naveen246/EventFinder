package in.digitrack.eventfinder;

import android.app.Fragment;

public class EventDetailsActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new EventDetailsFragment();
	}

}

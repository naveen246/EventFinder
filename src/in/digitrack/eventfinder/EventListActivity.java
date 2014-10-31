package in.digitrack.eventfinder;

import android.app.Fragment;

public class EventListActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new EventListFragment();
	}

}

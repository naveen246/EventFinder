package in.digitrack.eventfinder;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomeFragment extends Fragment {
	private EditText usrNameEditTxt;
	private Button getInBtn;
	private ProgressDialog dialog;
	private boolean isBtnClicked = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new FetchEventsTask().execute();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.welcome_fragment, container, false);
		
		usrNameEditTxt = (EditText) view.findViewById(R.id.username_editTxt);
		usrNameEditTxt.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN) {
					InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(usrNameEditTxt.getWindowToken(), 0);
				}
				return true;
			}
		});
		
		getInBtn = (Button) view.findViewById(R.id.getin_btn);
		getInBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(usrNameEditTxt.getText().toString().trim().equals("")) {
					Toast.makeText(getActivity(), "Please enter name", Toast.LENGTH_SHORT).show();
					return;
				}
				isBtnClicked = true;
				if(!EventData.getInstance().isDataAvailable()) {
					if(dialog == null) {
						dialog = new ProgressDialog(getActivity());
				        dialog.setMessage("Loading");
				        dialog.show();
					}
				} else {
					startListActivity();
				}
			}
		});
		return view;
	}
	
	private void startListActivity() {
		if(dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		Intent intent = new Intent(getActivity(), EventListActivity.class);
		startActivity(intent);
	}
	
	private class FetchEventsTask extends AsyncTask<Void, Void, String> {
		
		@Override
		protected String doInBackground(Void... params) {
			if(getActivity() == null) return null;
			return new DataFetchr().fetchData();
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			EventData.getInstance().setData(result);
			if(isBtnClicked) {
				startListActivity();
			}
		}
	}
	
}

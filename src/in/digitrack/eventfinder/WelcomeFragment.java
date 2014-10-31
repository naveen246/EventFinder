package in.digitrack.eventfinder;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
				Intent intent = new Intent(getActivity(), EventListActivity.class);
				startActivity(intent);
			}
		});
		return view;
	}
	
	
}

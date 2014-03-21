package bachelor.register;

import java.util.ArrayList;

import com.bachelor.hiofcommuting.R;

import android.app.ActionBar.TabListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import bachelor.database.HandleLogin;
import bachelor.tab.TabList;


public class EmailLoginActivity extends FragmentActivity {
	
	protected int forsok = 5;
	FragmentManager fm = getSupportFragmentManager();
	FragmentTransaction transaction = fm.beginTransaction();
	//private static final int CONTAINER = 2;
	private static final int REGISTER = 0;
	private static final int FORGOTPW = 1;
	private Fragment[] fragments = new Fragment[2];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_login);
		if (savedInstanceState == null) {
			fm = getSupportFragmentManager();
			transaction = fm.beginTransaction();
			transaction.add(R.id.container, new PlaceholderFragment());
			
			//fragments[CONTAINER] = fm.findFragmentById(R.id.container);
			fragments[REGISTER] = fm.findFragmentById(R.id.registerFragment);
			fragments[FORGOTPW] = fm.findFragmentById(R.id.forgotPwFragment);
			for (int i = 0; i < fragments.length; i++) {
				transaction.hide(fragments[i]);
			}
			transaction.commit();
			/*
			Fragment registerFragment = fm.findFragmentById(R.id.registerFragment);
			transaction.hide(registerFragment);
			Fragment forgotPwFragment = fm.findFragmentById(R.id.forgotPwFragment);
			transaction.hide(forgotPwFragment);
			transaction.commit();
			*/
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			System.out.print("Settings clicked");
			return true;
		}
		if (id == R.id.newActivity){
			Intent intent = new Intent(this, bachelor.tab.TabListener.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
	    FragmentManager fm = getSupportFragmentManager();
	    if (fm.getBackStackEntryCount() > 0) {
	        fm.popBackStack();
	    } else {
	        super.onBackPressed();
	    }
	}

	// BRUKEREN TRYKKER P� KNAPPEN: LOGG INN
	public void buttonLogin(View view) {
		// Henter brukerinput
		String epost = ((EditText) findViewById(R.id.login_editText_email))
				.getText().toString();
		String passord = ((EditText) findViewById(R.id.login_editText_passord))
				.getText().toString();

		// Forbereder toast-melding
		CharSequence toastMessage = null;

		// Sjekker om brukeren har fyllt inn data
		if (!epost.isEmpty() && !passord.isEmpty()) {
			// Sjekker om brukeren har pr�vd � logge inn med feil passord for
			// mange ganger
			if (forsok > 0) {
				ArrayList<String> brukerInput = new ArrayList<String>();
				brukerInput.add(epost);
				brukerInput.add(passord);
				new ValiderBruker().execute(brukerInput);
			} else {
				toastMessage = "Glemt passord?";
			}
		} else {
			toastMessage = "Fyll inn brukernavn og passord";
		}

		// Skriver ut toast om noe gikk galt
		if (toastMessage != null) {
			Context context = getApplicationContext();
			int duration = Toast.LENGTH_SHORT;
			Toast.makeText(context, toastMessage, duration).show();
		}
	}

	// N�r ValiderBruker-tr�den er ferdig, blir denne metoden trigget
	private void ValiderBrukerFerdig(String results) {
		// Hvis brukernavn og passord stemte, logges brukeren inn
		if (results == null) {
			Intent intent = new Intent(this, bachelor.tab.TabListener.class);
			startActivity(intent);
			// avslutter denne aktiviteten, s� den ikke ligger p� stack
			finish();
		} else {
			Toast.makeText(getApplicationContext(), results, Toast.LENGTH_SHORT)
					.show();
		}
	}

	public void nybruker(View view) {
		System.out.println("test nybruker text-click");
		fm = getSupportFragmentManager();
		transaction = fm.beginTransaction();
		Fragment container = fm.findFragmentById(R.id.container);
		transaction.hide(container);
		Fragment registerFragment = fm.findFragmentById(R.id.registerFragment);
		transaction.show(registerFragment);
		transaction.addToBackStack(null);
		transaction.commit();
		//showFragment(REGISTER, true);
	}

	public void glemtpassord(View view) {
		System.out.println("test glemtpassord text-click");
		fm = getSupportFragmentManager();
		transaction = fm.beginTransaction();
		Fragment container = fm.findFragmentById(R.id.container);
		transaction.hide(container);
		Fragment forgotPwFragment = fm.findFragmentById(R.id.forgotPwFragment);
		transaction.show(forgotPwFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	private void showFragment(int fragmentIndex, boolean addToBackStack) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		for (int i = 0; i < fragments.length; i++) {
			if (i == fragmentIndex) {
				transaction.show(fragments[i]);
			} else {
				transaction.hide(fragments[i]);
			}
		}
		if (addToBackStack) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}

	/**
	 * 
	 * @author Martin Validerer brukerinput opp mot database. Kj�res i AsyncTask
	 *         da dette er en tyngre oppgave.
	 */
	private class ValiderBruker extends
			AsyncTask<ArrayList<String>, Void, String> {

		private ProgressDialog Dialog = new ProgressDialog(
				EmailLoginActivity.this);

		@Override
		protected void onPreExecute() {
			Dialog.setMessage("Logger inn...");
			Dialog.show();
		}

		@Override
		protected String doInBackground(ArrayList<String>... params) {
			String epost = params[0].get(0);
			String passord = params[0].get(1);

			// Sjekker om eposten ligger i systemet
			if (HandleLogin.checkEmail(epost)) {
				// Sjekker om passordet matcher eposten
				if (HandleLogin.checkPassord(epost, passord)) {
					// Brukeren logget inn
					return null;
				} else {
					return "Feil brukernavn/passord. " + (--forsok)
							+ " fors�k igjen.";
				}
			} else {
				return "Fant ingen bruker med angitt epost i systemet";
			}
		}

		@Override
		protected void onPostExecute(String result) {
			EmailLoginActivity.this.ValiderBrukerFerdig(result);
			Dialog.dismiss();
		}

	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {



		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_login,
					container, false);
			return rootView;
		}
	}
}

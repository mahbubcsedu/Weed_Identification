package com.mpss.weed.id.farmer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.mpss.weed.id.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class FarmerRegisterActivity extends Activity implements
		LocationListener {
	Context context = this;
	ProgressDialog progress;
	EditText user, first, last, password, password2;
	Button register;
	Spinner county;

	LocationManager locationManager;
	Location myLocation = null;

	String tempLat = "0.00", tempLong = "0.00", tempRanking = "1";
	SimpleDateFormat memberSinceFormat = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat lastVisitedFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	
	String firstName, lastName, farmerID, error;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		progress = new ProgressDialog(context);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, this);
		} else {
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, this);
		}

		user = (EditText) findViewById(R.id.username);
		first = (EditText) findViewById(R.id.first_name);
		last = (EditText) findViewById(R.id.last_name);
		password = (EditText) findViewById(R.id.password);
		password2 = (EditText) findViewById(R.id.password2);

		register = (Button) findViewById(R.id.register);
		register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (meetsRequirements() && myLocation != null) {
					attemptRegister();
				}
			}
		});

		// Spinner spinner = (Spinner) findViewById(R.id.spinner);
		county = (android.widget.Spinner) findViewById(R.id.spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.county_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		county.setAdapter(adapter);
	}

	public boolean meetsRequirements() {
		if (user.getText().toString().length() < 5) {
			Toast.makeText(context,
					"Usernames must contain at least 5 characters",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (user.getText().toString().length() > 25) {
			Toast.makeText(context,
					"Usernames must be no longer than 25 characters",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (first.getText().toString().length() <= 0) {
			Toast.makeText(context, "Please enter a first name",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (first.getText().toString().length() > 50) {
			Toast.makeText(context,
					"First names must be no longer than 50 characters",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (last.getText().toString().length() <= 0) {
			Toast.makeText(context, "Please enter a last name",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (last.getText().toString().length() > 50) {
			Toast.makeText(context,
					"Last names must be no longer than 50 characters",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (password.getText().toString().length() < 8) {
			Toast.makeText(context,
					"Passwords must contain at least 8 characters",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (password.getText().toString().length() > 25) {
			Toast.makeText(context,
					"Passwords must be no longer than 25 characters",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (!password.getText().toString()
				.equals(password2.getText().toString())) {
			Toast.makeText(context, "Passwords must match", Toast.LENGTH_SHORT)
					.show();
			return false;
		}

		return true;
	}

	public void attemptRegister() {
		progress.setMessage("Registering account");
		progress.setCancelable(false);
		progress.show();
		String temp = "http://mpss.csce.uark.edu/~mweathers/weedapp/insertfarmer.php?code=54m3xuzm97z30rdfsloegjizvzgga12bshptv59o";

		HttpPost post = new HttpPost(temp);

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("username", user.getText()
				.toString()));
		nameValuePairs.add(new BasicNameValuePair("firstname", first.getText()
				.toString()));
		nameValuePairs.add(new BasicNameValuePair("lastname", last.getText()
				.toString()));
		nameValuePairs.add(new BasicNameValuePair("fpassword", password
				.getText().toString()));
		nameValuePairs.add(new BasicNameValuePair("location", myLocation
				.getLatitude() + "," + myLocation.getLongitude()));
		nameValuePairs.add(new BasicNameValuePair("ranking", tempRanking));
		nameValuePairs.add(new BasicNameValuePair("membersince",
				memberSinceFormat.format(new Date())));
		nameValuePairs.add(new BasicNameValuePair("lastvisited",
				lastVisitedFormat.format(new Date())));
		nameValuePairs.add(new BasicNameValuePair("county", county
				.getSelectedItem().toString()));

		try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		new RegisterTask().execute(post);
	}

	public void finishRegisterAttempt(Boolean successful) {
		progress.cancel();
		if (successful) {
			//Toast.makeText(context, "SUCCESS", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(context, FarmerLoginActivity.class);
			startActivity(intent);
		} else {
			Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
		}
	}

	private class RegisterTask extends AsyncTask<HttpPost, Void, Boolean> {
		@Override
		protected Boolean doInBackground(HttpPost... posts) {
			HttpClient hc = new DefaultHttpClient();
			HttpResponse rp = null;
			try {
				rp = hc.execute(posts[0]);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String response = null;
			if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
				try {
					response = EntityUtils.toString(rp.getEntity());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			JSONObject json = null;
			try {
				json = new JSONObject(response);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if (json.getString("error").equals("")) {
					firstName = json.getString("first_name");
					lastName = json.getString("last_name");
					farmerID = json.getString("farmer_id");
					return Boolean.TRUE;
				} else {
					error = json.getString("error");
					return Boolean.FALSE;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			finishRegisterAttempt(result);
		}
	}

	@Override
	public void onLocationChanged(Location loc) {
		myLocation = loc;
		locationManager.removeUpdates(this);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
}
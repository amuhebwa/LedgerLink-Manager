package org.grameenfoundation.applabs.ledgerlinkmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.Constants;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.Utils;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {
    private EditText inputUsername, inputPasskey;
    private String mResult, mTrainerId, mUserName, serverUrl;
    private Constants constants;
    private Utils utils;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        constants = new Constants();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging In ...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        utils = new Utils();

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        android.content.SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        serverUrl = sharedPreferences.getString("baseurl", constants.DEFAULTURL);

        inputUsername = (EditText) findViewById(R.id.username);
        inputPasskey = (EditText) findViewById(R.id.passkey);

        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateUserDetails(serverUrl, inputUsername.getText().toString().trim(), inputPasskey.getText().toString().trim());
            }
        });
    }

    private void showFlashMessage(String toastMessage) {
        Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
    }

    // Validate user details before Logging in
    private void validateUserDetails(String serverUrl, String username, String passkey) {
        if (username.isEmpty()) {
            inputUsername.setError("Enter Valid Username");
        } else if (passkey.isEmpty()) {
            inputPasskey.setError("Enter Valid PassKey");
        } else {
            inputUsername.setError(null);
            inputPasskey.setError(null);
            if (utils.isInternetOn(getApplicationContext())) {
                validateTrainer(serverUrl, username, passkey);
            } else {
                showFlashMessage("No Internet Connection");
            }
        }
    }

    // Log-in the Technical Trainer
    private void validateTrainer(String url, String username, String passkey) {
        progressDialog.show();
        String request_url = url + constants.validateTrainer + "/" + username + "/" + passkey;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, request_url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();

                try {

                    JSONObject loginDetails = response.getJSONObject("validateTrainerResult");
                    mResult = loginDetails.getString("resultId");
                    mTrainerId = loginDetails.getString("TrainerId");
                    mUserName = loginDetails.getString("userName");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (mResult.equalsIgnoreCase("1")) { //  Success
                    JsonData.getInstance().setTrainerId(mTrainerId);
                    JsonData.getInstance().setUserName(mUserName);
                    Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(loginIntent);
                    finish();

                } else { // Failed
                    progressDialog.dismiss();
                    showFlashMessage("Error Occured . Try again");
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {

                progressDialog.dismiss();
                showFlashMessage("An Error Occurred. Try again");

            }
        });

        VolleySingleton.getIntance().addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, PreferencesActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

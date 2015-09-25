package org.grameenfoundation.applabs.ledgerlinkmanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.LedgerLinkUtils;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.SharedPreferencesUtils;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.UrlConstants;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {
    private EditText Username, Passkey;
    private String loginResult, TechnicalTrainerId = "-1", TTUsername;
    private UrlConstants constants = new UrlConstants();
    private Activity activity = this;
    private LedgerLinkUtils ledgerLinkUtils;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging In ...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        ledgerLinkUtils = new LedgerLinkUtils();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final String serverUrl = sharedPreferences.getString("LedgerLinkBaseUrl", constants.DEFAULTURL);
        Username = (EditText) findViewById(R.id.username);
        Passkey = (EditText) findViewById(R.id.passkey);
        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateUserDetails(serverUrl, Username.getText().toString().trim(), Passkey.getText().toString().trim());
            }
        });
    }

    /**
     * Show toast method
     */
    private void showToastMessage(String toastMessage) {
        Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
    }

    /**
     * Validate user details before Logging in
     */
    private void validateUserDetails(String serverUrl, String username, String passkey) {

        if (username.isEmpty()) {
            Username.setError("Username cannot be empty");
        } else if (passkey.isEmpty()) {
            Passkey.setError("Passkey cannot be empty");
        } else {
            Username.setError(null);
            Passkey.setError(null);
            if (ledgerLinkUtils.isInternetOn(getApplicationContext())) {
                getLoginCredentials(serverUrl, username, passkey);
            } else {
                showToastMessage("No Internet Connection");
            }
        }
    }

    /**
     * Method to Log in a technical trainer to the system
     */

    private void getLoginCredentials(String url, String _username, String _passkey) {
        progressDialog.show();
        String request_url = url + constants.TechnicalTrainer + "/" + _username + "/" + _passkey;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, request_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject loginDetails = response.getJSONObject("technicalTrainerLoginResult");
                            loginResult = loginDetails.getString("result");
                            TechnicalTrainerId = loginDetails.getString("TechnicalTrainerId");
                            TTUsername = loginDetails.getString("Username");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (loginResult.equalsIgnoreCase("1")) { /** Sucess*/
                            SharedPreferencesUtils.saveSharedPreferences(activity, "TechnicalTrainerId", TechnicalTrainerId);
                            Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                            loginIntent.putExtra("TTUsername", TTUsername);
                            loginIntent.putExtra("TechnicalTrainerId", TechnicalTrainerId);
                            startActivity(loginIntent);
                            finish();

                        } else { /** Failed to Login*/
                            progressDialog.dismiss();
                            showToastMessage("Error Occured . Try again");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
                showToastMessage("An Error Occurred. Try again");
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

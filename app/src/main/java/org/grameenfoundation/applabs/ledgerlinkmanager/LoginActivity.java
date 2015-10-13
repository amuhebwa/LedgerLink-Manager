package org.grameenfoundation.applabs.ledgerlinkmanager;

import android.app.Activity;
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

import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.Utils;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.SharedPrefs;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.Constants;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {
    private EditText txtUsername, txtPasskey;
    private String loginResult, technicalTrainerId = "-1", tTUsername;
    private Constants constants = new Constants();
    private Activity activity = this;
    private Utils utils;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging In ...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        utils = new Utils();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        android.content.SharedPreferences sharedPreferences = PreferenceManager.
                getDefaultSharedPreferences(getBaseContext());
        final String serverUrl = sharedPreferences.getString("LedgerLinkBaseUrl", constants.DEFAULTURL);
        txtUsername = (EditText) findViewById(R.id.username);
        txtPasskey = (EditText) findViewById(R.id.passkey);
        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateUserDetails(serverUrl, txtUsername.getText().toString().trim(),
                        txtPasskey.getText().toString().trim());
            }
        });
    }

    /**
     * Show toast method
     */
    private void showFlashMessage(String toastMessage) {
        Toast.makeText(getApplicationContext(),
                toastMessage, Toast.LENGTH_SHORT).show();
    }

    /**
     * Validate user details before Logging in
     */
    private void validateUserDetails(String serverUrl, String username, String passkey) {

        if (username.isEmpty()) {
            txtUsername.setError("Username cannot be empty");
        } else if (passkey.isEmpty()) {
            txtPasskey.setError("Passkey cannot be empty");
        } else {
            txtUsername.setError(null);
            txtPasskey.setError(null);
            if (utils.isInternetOn(getApplicationContext())) {
                getLoginCredentials(serverUrl, username, passkey);
            } else {
                showFlashMessage("No Internet Connection");
            }
        }
    }

    /**
     * Method to Log in a technical trainer to the system
     */

    private void getLoginCredentials(String url, String username, String passkey) {
        progressDialog.show();
        String request_url = url + constants.TechnicalTrainer + "/" + username + "/" + passkey;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, request_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject loginDetails = response.getJSONObject("technicalTrainerLoginResult");
                            loginResult = loginDetails.getString("result");
                            technicalTrainerId = loginDetails.getString("TechnicalTrainerId");
                            tTUsername = loginDetails.getString("Username");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (loginResult.equalsIgnoreCase("1")) { /** Success */
                            SharedPrefs.saveSharedPreferences(activity, "TechnicalTrainerId", technicalTrainerId);
                            Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                            loginIntent.putExtra("TTUsername", tTUsername);
                            loginIntent.putExtra("TechnicalTrainerId", technicalTrainerId);
                            startActivity(loginIntent);
                            finish();

                        } else { /** Failed to Login*/
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

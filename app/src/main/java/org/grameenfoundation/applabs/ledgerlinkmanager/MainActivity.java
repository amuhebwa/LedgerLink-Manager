package org.grameenfoundation.applabs.ledgerlinkmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.grameenfoundation.applabs.ledgerlinkmanager.adapters.RecyclerViewAdapter;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.DatabaseHandler;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.Utils;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.Constants;
import org.grameenfoundation.applabs.ledgerlinkmanager.models.VslaInfo;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity {
    private EditText extGroupSearch;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<VslaInfo> _vslaInfo;
    private LinearLayout empty_view;
    private Utils utils;
    private ProgressDialog progressDialog;
    private Constants constants = new Constants();
    private String vslaName, groupRepresentativeName, groupRepresentativePost, groupRepresentativePhoneNumber,
            groupBankAccount, physicalAddress, regionName, groupPhoneNumber, locationCoordinates, groupSupportType, TechnicalTrainerId;
    private int VslaId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final String serverUrl = sharedPreferences.getString("LedgerLinkBaseUrl", constants.DEFAULTURL);
        TextView TrainerUsername = (TextView) findViewById(R.id.TrainerUsername);
        if (getIntent().getStringExtra("TTUsername") != null) {
            TrainerUsername.setText(getIntent().getStringExtra("TTUsername"));
        }
        if (getIntent().getStringExtra("TechnicalTrainerId") != null) {
            TechnicalTrainerId = getIntent().getStringExtra("TechnicalTrainerId");
        }

        initializeUIComponents();

        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                uploadUnsentData(position);
            }
        });

        extGroupSearch = (EditText) findViewById(R.id.group_search);
        extGroupSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    validateSearchQuery(serverUrl);
                }
                return false;
            }
        });
    }

    /**
     * Initialize UI components
     */
    private void initializeUIComponents() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Searching For Group Name");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        empty_view = (LinearLayout) findViewById(R.id.empty_view);
        utils = new Utils();

        _vslaInfo = new ArrayList<>();

        new queryDatabaseForGroupsAsyncTask().execute();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyleView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter(_vslaInfo);
        recyclerView.setAdapter(recyclerViewAdapter);
    }


    /**
     * If data has not been sent, allow it to be uploaded
     */
    private void uploadUnsentData(int position) {
        String isDataSent = _vslaInfo.get(position).getIsDataSent();
        DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
        VslaId = _vslaInfo.get(position).getId();
        if (!isDataSent.equalsIgnoreCase("1")) {
            VslaInfo vslaInfo = databaseHandler.getGroupData(VslaId);
            vslaName = vslaInfo.getGroupName();
            groupRepresentativeName = vslaInfo.getMemberName();
            groupRepresentativePost = vslaInfo.getMemberPost();
            groupRepresentativePhoneNumber = vslaInfo.getMemberPhoneNumber();
            groupBankAccount = vslaInfo.getGroupAccountNumber();
            physicalAddress = vslaInfo.getPhysicalAddress();
            regionName = vslaInfo.getRegionName();
            groupPhoneNumber = vslaInfo.getIssuedPhoneNumber();
            locationCoordinates = vslaInfo.getLocationCordinates();
            groupSupportType = vslaInfo.getSupportType();

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("GroupSupport", "-NA-");
                jsonObject.put("VslaName", vslaName);
                jsonObject.put("VslaPhoneMsisdn", groupPhoneNumber);
                jsonObject.put("PhysicalAddress", physicalAddress);
                jsonObject.put("GpsLocation", locationCoordinates);
                jsonObject.put("GroupRepresentativeName", groupRepresentativeName);
                jsonObject.put("GroupRepresentativePosition", groupRepresentativePost);
                jsonObject.put("GroupAccountNumber", groupBankAccount);
                jsonObject.put("GroupRepresentativePhonenumber", groupRepresentativePhoneNumber);
                jsonObject.put("RegionName", regionName);
                jsonObject.put("TechnicalTrainerId", TechnicalTrainerId);
                jsonObject.put("Status", "2");
                jsonObject.put("GroupSupport", groupSupportType);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            StringBuilder url = new StringBuilder();
            url.append(constants.DEFAULTURL);
            url.append("createNewVsla");
            new HttpAsyncTaskClass().execute(url.toString(), jsonObject.toString());
        }
    }

    /**
     * Update the group's sent status to true after successfully submitting
     */
    private void updateDatabaseToSent() {
        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        VslaInfo vslaInfo = new VslaInfo();
        vslaInfo.setGroupName(vslaName);
        vslaInfo.setMemberName(groupRepresentativeName);
        vslaInfo.setMemberPost(groupRepresentativePost);
        vslaInfo.setMemberPhoneNumber(groupRepresentativePhoneNumber);
        vslaInfo.setGroupAccountNumber(groupBankAccount);
        vslaInfo.setPhysicalAddress(physicalAddress);
        vslaInfo.setRegionName(regionName);
        vslaInfo.setLocationCordinates(locationCoordinates);
        vslaInfo.setIssuedPhoneNumber(groupPhoneNumber);
        vslaInfo.setIsDataSent("1");
        vslaInfo.setSupportType(groupSupportType);
        databaseHandler.upDateGroupData(vslaInfo, VslaId);
    }

    /**
     * Show toast method
     */
    private void showFlashMessage(String toastMessage) {
        Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
    }

    /**
     * Validate that a search term has been added into the query box
     * Check that there is an internet connection
     **/
    private void validateSearchQuery(String serverUrl) {
        String searchTerm = extGroupSearch.getText().toString().replace(" ", "");
        if (!utils.isInternetOn(this)) {
            showFlashMessage("No Internet Connection");
        } else if (searchTerm.isEmpty()) {
            extGroupSearch.setError("Invalid Search Term");
        } else {
            extGroupSearch.setError(null);
            searchForGroupInformation(searchTerm, serverUrl);
        }
    }

    /**
     * Search the server for a table that matches the supplied string
     */
    private void searchForGroupInformation(String keyWord, String url) {
        progressDialog.show();
        String urlRequest = url + constants.SEARCHVSLA + "/" + keyWord;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlRequest, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                progressDialog.dismiss();
                Intent jsonIntent = new Intent(MainActivity.this, SearchResults.class);
                jsonIntent.putExtra("jsonIntent", jsonObject.toString());
                startActivity(jsonIntent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
                showFlashMessage("Error Occurred");
            }
        });
        VolleySingleton.getIntance().addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Ansynchrounous task class to take off the pressure from the ui thread
     * If any groups are found, add them to the list
     */
    private class queryDatabaseForGroupsAsyncTask extends AsyncTask<Void, Void, List<VslaInfo>> {
        @Override
        protected List<VslaInfo> doInBackground(Void... params) {
            DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
            return databaseHandler.getAllGroups();
        }

        @Override
        protected void onPostExecute(List<VslaInfo> vslaInfos) {
            super.onPostExecute(vslaInfos);
            if (vslaInfos.size() != 0) {
                for (int i = 0; i < vslaInfos.size(); i++) {
                    VslaInfo dataSet = new VslaInfo();
                    dataSet.setId(vslaInfos.get(i).getId());
                    dataSet.setGroupName(vslaInfos.get(i).getGroupName());
                    dataSet.setPhysicalAddress(vslaInfos.get(i).getPhysicalAddress());
                    dataSet.setMemberName(vslaInfos.get(i).getMemberName());
                    dataSet.setVslaId(vslaInfos.get(i).getVslaId());
                    dataSet.setIsDataSent(vslaInfos.get(i).getIsDataSent());
                    /** if the data has not been uploaded, show the cloud upload icon*/
                    if (vslaInfos.get(i).getIsDataSent().equalsIgnoreCase("0")) {
                        dataSet.setUploadDataIcon(R.drawable.ic_cloud_upload);
                    }
                    _vslaInfo.add(dataSet);
                }
                recyclerViewAdapter.notifyDataSetChanged();
                empty_view.setVisibility(View.VISIBLE);
            }

        }
    }

    /**
     * Asynchronous task class to send data to the server.
     */

    private class HttpAsyncTaskClass extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showFlashMessage("Sending Data");
        }

        @Override
        protected String doInBackground(String... params) {
            String postString = params[0];
            String jsonObjectString = params[1];

            String response = "";
            java.net.URL url;
            HttpURLConnection conn;

            try {
                url = new URL(postString);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setFixedLengthStreamingMode(jsonObjectString.getBytes().length);

                //make some HTTP header nicety
                conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                PrintWriter out = new PrintWriter(conn.getOutputStream());
                out.print(jsonObjectString);
                out.close();
                Scanner inStream = new Scanner(conn.getInputStream());
                while (inStream.hasNextLine()) {
                    response += (inStream.nextLine());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String operationType = jsonObject.getString("operation");
                String operationResult = jsonObject.getString("result");
                showResultFeedback(operationType, operationResult);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Show results : Success/Fail
     */
    private void showResultFeedback(String operationType, String operationResult) {
        if (operationType.equalsIgnoreCase("create") && operationResult.equalsIgnoreCase("1")) {
            showFlashMessage("Successfully added new VSLA.");

            updateDatabaseToSent(); /** update the database to sent*/

        } else if (operationResult.equalsIgnoreCase("-1")) {
            showFlashMessage("An Error Occured");

        }
    }
}

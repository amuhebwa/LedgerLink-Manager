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
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.LedgerLinkUtils;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.UrlConstants;
import org.grameenfoundation.applabs.ledgerlinkmanager.models.VslaDataModel;
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
    private EditText groupSearch;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<VslaDataModel> _vslaDataModel;
    private LinearLayout empty_view;
    private LedgerLinkUtils ledgerLinkUtils;
    private ProgressDialog progressDialog;
    private UrlConstants constants = new UrlConstants();
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

        groupSearch = (EditText) findViewById(R.id.group_search);
        groupSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        ledgerLinkUtils = new LedgerLinkUtils();

        _vslaDataModel = new ArrayList<>();

        new queryDatabaseForGroupsAsyncTask().execute();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyleView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter(_vslaDataModel);
        recyclerView.setAdapter(recyclerViewAdapter);
    }


    /** If data has not been sent, allow it to be uploaded */
    private void uploadUnsentData(int position) {
        String isDataSent = _vslaDataModel.get(position).getIsDataSent();
        DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
        VslaId = _vslaDataModel.get(position).getId();
        if (!isDataSent.equalsIgnoreCase("1")) {
            VslaDataModel vslaDataModel = databaseHandler.getGroupData(VslaId);
            vslaName = vslaDataModel.getGroupName();
            groupRepresentativeName = vslaDataModel.getMemberName();
            groupRepresentativePost = vslaDataModel.getMemberPost();
            groupRepresentativePhoneNumber = vslaDataModel.getMemberPhoneNumber();
            groupBankAccount = vslaDataModel.getGroupAccountNumber();
            physicalAddress = vslaDataModel.getPhysicalAddress();
            regionName = vslaDataModel.getRegionName();
            groupPhoneNumber = vslaDataModel.getIssuedPhoneNumber();
            locationCoordinates = vslaDataModel.getLocationCordinates();
            groupSupportType = vslaDataModel.getSupportType();

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

    /** Update the group's sent status to true after successfully submitting */
    private void updateDatabaseToSent() {
        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        VslaDataModel vslaDataModel = new VslaDataModel();
        vslaDataModel.setGroupName(vslaName);
        vslaDataModel.setMemberName(groupRepresentativeName);
        vslaDataModel.setMemberPost(groupRepresentativePost);
        vslaDataModel.setMemberPhoneNumber(groupRepresentativePhoneNumber);
        vslaDataModel.setGroupAccountNumber(groupBankAccount);
        vslaDataModel.setPhysicalAddress(physicalAddress);
        vslaDataModel.setRegionName(regionName);
        vslaDataModel.setLocationCordinates(locationCoordinates);
        vslaDataModel.setIssuedPhoneNumber(groupPhoneNumber);
        vslaDataModel.setIsDataSent("1");
        vslaDataModel.setSupportType(groupSupportType);
        databaseHandler.upDateGroupData(vslaDataModel, VslaId);
    }

    /** Show toast method */
    private void showToastMessage(String toastMessage) {
        Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
    }

    /**
     * Validate that a search term has been added into the query box
     * Check that there is an internet connection
     **/
    private void validateSearchQuery(String serverUrl) {
        String searchTerm = groupSearch.getText().toString().replace(" ","");
        if (!ledgerLinkUtils.isInternetOn(this)) {
            showToastMessage("No Internet Connection");
        } else if (searchTerm.isEmpty()) {
            groupSearch.setError("Invalid Search Term");
        } else {
            groupSearch.setError(null);
            searchForGroupInformation(searchTerm, serverUrl);
        }
    }

    /** Search the server for a table that matches the supplied string */
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
                showToastMessage("Error Occurred");
            }
        });
        VolleySingleton.getIntance().addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Ansynchrounous task class to take off the pressure from the ui thread
     * If any groups are found, add them to the list
     */
    private class queryDatabaseForGroupsAsyncTask extends AsyncTask<Void, Void, List<VslaDataModel>> {
        @Override
        protected List<VslaDataModel> doInBackground(Void... params) {
           DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
            return databaseHandler.getAllGroups();
        }

        @Override
        protected void onPostExecute(List<VslaDataModel> vslaDataModels) {
            super.onPostExecute(vslaDataModels);
            if (vslaDataModels.size() != 0) {
                for (int i = 0; i < vslaDataModels.size(); i++) {
                    VslaDataModel dataSet = new VslaDataModel();
                    dataSet.setId(vslaDataModels.get(i).getId());
                    dataSet.setGroupName(vslaDataModels.get(i).getGroupName());
                    dataSet.setPhysicalAddress(vslaDataModels.get(i).getPhysicalAddress());
                    dataSet.setMemberName(vslaDataModels.get(i).getMemberName());
                    dataSet.setVslaId(vslaDataModels.get(i).getVslaId());
                    dataSet.setIsDataSent(vslaDataModels.get(i).getIsDataSent());
                    /** if the data has not been uploaded, show the cloud upload icon*/
                    if (vslaDataModels.get(i).getIsDataSent().equalsIgnoreCase("0")) {
                        dataSet.setUploadDataIcon(R.drawable.ic_cloud_upload);
                    }
                    _vslaDataModel.add(dataSet);
                }
                recyclerViewAdapter.notifyDataSetChanged();
                empty_view.setVisibility(View.VISIBLE);
            }

        }
    }

    /** Asynchronous task class to send data to the server. */

    private class HttpAsyncTaskClass extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showToastMessage("Sending Data");
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

    /** Show results : Success/Fail */
    private void showResultFeedback(String operationType, String operationResult) {
        if (operationType.equalsIgnoreCase("create") && operationResult.equalsIgnoreCase("1")) {
            showToastMessage("Successfully added new VSLA.");

            updateDatabaseToSent(); /** update the database to sent*/

        } else if (operationResult.equalsIgnoreCase("-1")) {
            showToastMessage("An Error Occured");

        }
    }
}

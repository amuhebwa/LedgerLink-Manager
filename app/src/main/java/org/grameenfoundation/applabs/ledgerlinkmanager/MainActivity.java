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
import android.view.Menu;
import android.view.MenuItem;
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

import org.grameenfoundation.applabs.ledgerlinkmanager.adapters.DataAdapter;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.SQLiteDbHandler;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.RecyclerViewListDivider;
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
    private EditText inputGroupSearch;
    private DataAdapter dataAdapter;
    private ArrayList<VslaInfo> vslaInfo;
    private LinearLayout empty_view;
    private Utils utils;
    private ProgressDialog progressDialog;
    private Constants constants = new Constants();
    private String vslaName, representativeName, representativePost, repPhoneNumber, grpBankAccount,
            physAddress, regionName, grpPhoneNumber, locCoordinates, groupSupportType, TechnicalTrainerId,
            numberOfCycles, TrainerUsername;
    private int VslaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final String serverUrl = sharedPreferences.getString("baseurl", constants.DEFAULTURL);

        TechnicalTrainerId = JsonData.getInstance().getTrainerId();
        TrainerUsername = JsonData.getInstance().getUserName();
        TextView usernameTxt = (TextView) findViewById(R.id.TrainerUsername);
        usernameTxt.setText(TrainerUsername);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Searching For Group Name");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);

        empty_view = (LinearLayout) findViewById(R.id.empty_view);
        utils = new Utils();
        vslaInfo = new ArrayList<>();

        new queryDatabaseForGroupsAsyncTask().execute();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyleView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        dataAdapter = new DataAdapter(vslaInfo);
        recyclerView.setAdapter(dataAdapter);
        RecyclerView.ItemDecoration itemDecoration = new RecyclerViewListDivider(this, RecyclerViewListDivider.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);

        dataAdapter.setOnItemClickListener(new DataAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                uploadUnsentData(position);
            }
        });

        inputGroupSearch = (EditText) findViewById(R.id.group_search);
        inputGroupSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    validateSearchQuery(serverUrl);
                }
                return false;
            }
        });
    }

    // If data has not been sent, allow it to be uploaded
    private void uploadUnsentData(int position) {
        String isDataSent = vslaInfo.get(position).getIsDataSent();
        SQLiteDbHandler SQLiteDbHandler = new SQLiteDbHandler(getApplicationContext());
        VslaId = vslaInfo.get(position).getId();
        if (!isDataSent.equalsIgnoreCase("1")) {
            VslaInfo vslaInfo = SQLiteDbHandler.getGroupData(VslaId);
            vslaName = vslaInfo.getGroupName();
            representativeName = vslaInfo.getMemberName();
            representativePost = vslaInfo.getMemberPost();
            repPhoneNumber = vslaInfo.getMemberPhoneNumber();
            grpBankAccount = vslaInfo.getGroupAccountNumber();
            physAddress = vslaInfo.getPhysicalAddress();
            regionName = vslaInfo.getRegionName();
            grpPhoneNumber = vslaInfo.getIssuedPhoneNumber();
            locCoordinates = vslaInfo.getLocationCordinates();
            groupSupportType = vslaInfo.getSupportType();
            numberOfCycles = vslaInfo.getNumberOfCycles();

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("GroupSupport", "-NA-");
                jsonObject.put("VslaName", vslaName);
                jsonObject.put("grpPhoneNumber", grpPhoneNumber);
                jsonObject.put("PhysicalAddress", physAddress);
                jsonObject.put("GpsLocation", locCoordinates);
                jsonObject.put("representativeName", representativeName);
                jsonObject.put("representativePosition", representativePost);
                jsonObject.put("GroupAccountNumber", grpBankAccount);
                jsonObject.put("repPhoneNumber", repPhoneNumber);
                jsonObject.put("RegionName", regionName);
                jsonObject.put("tTrainerId", TechnicalTrainerId);
                jsonObject.put("Status", "2");
                jsonObject.put("GroupSupport", groupSupportType);
                jsonObject.put("numberOfCycles", numberOfCycles);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            StringBuilder url = new StringBuilder();
            url.append(constants.DEFAULTURL);
            url.append("createNewVsla");
            new HttpAsyncTaskClass().execute(url.toString(), jsonObject.toString());
        }
    }

    // Update the group's sent status to true after successfully submitting
    private void updateDatabaseToSent() {
        SQLiteDbHandler SQLiteDbHandler = new SQLiteDbHandler(this);
        VslaInfo vslaInfo = new VslaInfo();
        vslaInfo.setGroupName(vslaName);
        vslaInfo.setMemberName(representativeName);
        vslaInfo.setMemberPost(representativePost);
        vslaInfo.setMemberPhoneNumber(repPhoneNumber);
        vslaInfo.setGroupAccountNumber(grpBankAccount);
        vslaInfo.setPhysicalAddress(physAddress);
        vslaInfo.setRegionName(regionName);
        vslaInfo.setLocationCordinates(locCoordinates);
        vslaInfo.setIssuedPhoneNumber(grpPhoneNumber);
        vslaInfo.setIsDataSent("1");
        vslaInfo.setSupportType(groupSupportType);
        vslaInfo.setNumberOfCycles(numberOfCycles);
        SQLiteDbHandler.upDateGroupData(vslaInfo, VslaId);
    }

    private void showFlashMessage(String toastMessage) {
        Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
    }

    private void validateSearchQuery(String serverUrl) {
        String searchTerm = inputGroupSearch.getText().toString().replace(" ", "");
        if (!utils.isInternetOn(this)) {
            showFlashMessage("No Internet Connection");
        } else if (searchTerm.isEmpty()) {
            inputGroupSearch.setError("Invalid Search Term");
        } else {
            inputGroupSearch.setError(null);
            searchForGroupInformation(searchTerm, serverUrl);
        }
    }

    private void searchForGroupInformation(String keyWord, String url) {
        progressDialog.show();
        String urlRequest = url + constants.searchVsla + "/" + keyWord;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, urlRequest, null, new Response.Listener<JSONObject>() {
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

    // asynchronous class for seaching the database
    private class queryDatabaseForGroupsAsyncTask extends AsyncTask<Void, Void, List<VslaInfo>> {
        @Override
        protected List<VslaInfo> doInBackground(Void... params) {
            SQLiteDbHandler SQLiteDbHandler = new SQLiteDbHandler(getApplicationContext());
            return SQLiteDbHandler.getAllGroups();
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
                    // if the data has not been uploaded, show the cloud upload icon
                    if (vslaInfos.get(i).getIsDataSent().equalsIgnoreCase("0")) {
                        dataSet.setUploadDataIcon(R.drawable.ic_cloud_upload);
                    }
                    vslaInfo.add(dataSet);
                }
                dataAdapter.notifyDataSetChanged();
                empty_view.setVisibility(View.VISIBLE);
            }
        }
    }

    // asynchronous task class to send data to the server.
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

    // Show results : success/fail
    private void showResultFeedback(String operationType, String operationResult) {
        if (operationType.equalsIgnoreCase("create") && operationResult.equalsIgnoreCase("1")) {
            showFlashMessage("Successfully added new VSLA.");
            updateDatabaseToSent(); // update the database to sent
        } else if (operationResult.equalsIgnoreCase("-1")) {
            showFlashMessage("An Error Occured");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_group) {
            JsonData.getInstance().setIsEditing(false);
            Intent intent = new Intent(MainActivity.this, CreateGroup.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

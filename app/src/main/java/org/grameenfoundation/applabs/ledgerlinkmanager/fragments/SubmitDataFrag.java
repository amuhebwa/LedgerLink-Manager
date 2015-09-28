package org.grameenfoundation.applabs.ledgerlinkmanager.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.grameenfoundation.applabs.ledgerlinkmanager.R;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.DataHolder;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.DatabaseHandler;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.SharedPrefs;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.Constants;
import org.grameenfoundation.applabs.ledgerlinkmanager.models.VslaInfo;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public class SubmitDataFrag extends Fragment {
    private String serverUrl = "";
    private static long currentDatabaseId = 0;
    private DatabaseHandler databaseHandler;
    private String IsEditing, TechnicalTrainerId, vslaId;
    private TextView operationTypeView;
    private Activity activity;
    private String vslaName, groupRepresentativeName, groupRepresentativePost, groupRepresentativePhoneNumber,
            groupBankAccount, physicalAddress, regionName, groupPhoneNumber, locationCoordinates,
            groupSupportType;


    public SubmitDataFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_submit_data, container, false);

        getPreferences();

        operationTypeView = (TextView) view.findViewById(R.id.operationType);
        if (IsEditing.equalsIgnoreCase("1")) {
            operationTypeView.setText("Currently Editing Information for " + vslaName);
        } else {
            operationTypeView.setText("Adding New Group \n Please check that all fields have been filled");
        }
        setHasOptionsMenu(true);
        databaseHandler = new DatabaseHandler(getActivity());
        return view;
    }

    /**
     * Show toast method
     */
    private void showFlashMessage(String toastMessage) {
        Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();
    }

    /**
     * Load preference information & data saved in the singleton class
     */
    private void getPreferences() {
        Constants constants = new Constants();
        android.content.SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        serverUrl = sharedPreferences.getString("LedgerLinkBaseUrl", constants.DEFAULTURL);
        IsEditing = SharedPrefs.readSharedPreferences(activity, "IsEditing", "0");
        TechnicalTrainerId = SharedPrefs.readSharedPreferences(activity, "TechnicalTrainerId", "-1");
        vslaId = SharedPrefs.readSharedPreferences(activity, "vslaId", "-1");
        vslaName = DataHolder.getInstance().getVslaName();
        groupRepresentativeName = DataHolder.getInstance().getGroupRepresentativeName();
        groupRepresentativePost = DataHolder.getInstance().getGroupRepresentativePost();
        groupRepresentativePhoneNumber = DataHolder.getInstance().getGroupRepresentativePhoneNumber();
        groupBankAccount = DataHolder.getInstance().getGroupBankAccount();
        physicalAddress = DataHolder.getInstance().getPhysicalAddress();
        regionName = DataHolder.getInstance().getRegionName();
        groupPhoneNumber = DataHolder.getInstance().getGroupPhoneNumber();
        groupBankAccount = DataHolder.getInstance().getGroupBankAccount();
        locationCoordinates = DataHolder.getInstance().getLocationCoordinates();
        groupSupportType = DataHolder.getInstance().getSupportTrainingType();
    }

    /**
     * Called when you click the send button in the actionbar
     */
    private void dataSubmission() {
        getPreferences(); // First Reload all the data

        String jsonObjectString = createJsonObject();
        StringBuilder url = new StringBuilder();
        url.append(serverUrl);
        if (vslaName == null || groupRepresentativeName == null || groupRepresentativePost == null
                || groupRepresentativePhoneNumber == null || groupBankAccount == null || physicalAddress == null
                || groupPhoneNumber == null || groupSupportType == null) {
            showFlashMessage("Please Fill all Fields");

        } else if (IsEditing.equalsIgnoreCase("1")) {
            url.append("editExistingVsla");
            new HttpAsyncTaskClass().execute(url.toString(), jsonObjectString);
            saveToDatabase();
        } else { /**Creating a new VSLA*/
            url.append("createNewVsla");
            new HttpAsyncTaskClass().execute(url.toString(), jsonObjectString);
            saveToDatabase();
        }

    }

    /**
     * Load information and create a json object
     */
    public String createJsonObject() {
        JSONObject jsonObject = new JSONObject();

        try {
            if (!vslaId.equalsIgnoreCase("-1")) {
                /** Editing an existing VSLA's information */
                jsonObject.put("VslaId", vslaId);
            }

            jsonObject.put("GroupSupport", groupSupportType);
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    /**
     * Insert data into the database
     */
    private void saveToDatabase() {
        if (!databaseHandler.checkIfGroupExists(vslaName)) { /** Group Doesn't exist in the database*/

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
            vslaInfo.setSupportType(groupSupportType);
            vslaInfo.setIsDataSent("0");

            /** Get the Id of the Group just added to the database */
            currentDatabaseId = databaseHandler.addGroupData(vslaInfo);
            showFlashMessage("Data Saved Successfully");

        } else {
            showFlashMessage("Group Already Exists");
        }
    }

    /**
     * Update the group's sent status to true after successfully submitting
     */
    private void updateDatabaseToSent() {
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
        databaseHandler.upDateGroupData(vslaInfo, currentDatabaseId);
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

            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                showResultFeedback(jsonObject.getString("operation"), jsonObject.getString("result"), jsonObject.getString("VslaCode"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * Show results : Success/Fail
     */
    private void showResultFeedback(String operationType, String operationResult, String newVslaCode) {
        if (operationType.equalsIgnoreCase("create") && operationResult.equalsIgnoreCase("1")) {
            showFlashMessage("Successfully added new VSLA.");
            operationTypeView.setText("New Group created with the following VSLA Code : " + newVslaCode);
            updateDatabaseToSent(); /** update the database to sent*/

        } else if (operationType.equalsIgnoreCase("edit") && operationResult.equalsIgnoreCase("1")) {
            operationTypeView.setText("Group Information successfully Edited");
            showFlashMessage("Successfully Edited Details.");
            updateDatabaseToSent(); /** update the database to sent*/

        } else if (operationResult.equalsIgnoreCase("-1")) {
            showFlashMessage("An Error Occured");
            operationTypeView.setText("Error Occured");
        }
        // Then clear the data holder
        DataHolder.getInstance().clearDataHolder();
    }

    @Override
    public void onAttach(Activity a) {
        super.onAttach(a);
        this.activity = a;

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_send_information, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_send) {
            dataSubmission();
        }
        return super.onOptionsItemSelected(item);

    }
}

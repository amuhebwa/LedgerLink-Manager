package org.grameenfoundation.applabs.ledgerlinkmanager.fragments;

import android.content.Context;
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
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.Constants;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.DataHolder;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.DatabaseHandler;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.SharedPrefs;
import org.grameenfoundation.applabs.ledgerlinkmanager.models.VslaInfo;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


public class SubmissionFragment extends Fragment {
    private String serverUrl = "";
    private static long currentDatabaseId = 0;
    private DatabaseHandler databaseHandler;
    private String IsEditing;
    private String tTrainerId;
    private String vslaId;
    private TextView txtOperationType;
    private String vslaName;
    private String representativeName;
    private String representativePost;
    private String repPhoneNumber;
    private String grpBankAccount;
    private String physAddress;
    private String regionName;
    private String grpPhoneNumber;
    private String locCoordinates;
    private String grpSupportType;
    private String numberOfCycles;


    public SubmissionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.submit_data, container, false);

        getPreferences();

        txtOperationType = (TextView) view.findViewById(R.id.operationType);
        if (IsEditing.equalsIgnoreCase("1")) {
            txtOperationType.setText(String.format("Currently Editing Information for %s", vslaName));
        } else {
            txtOperationType.setText("Adding New Group \n Please check that all fields have been filled");
        }

        setHasOptionsMenu(true);
        databaseHandler = new DatabaseHandler(getActivity());

        return view;
    }

    // Show toast method
    private void flashMessage(String toastMessage) {
        Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();
    }

    // Load preference information & data saved in the singleton class
    private void getPreferences() {
        Constants constants = new Constants();
        android.content.SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        serverUrl = sharedPreferences.getString("LedgerLinkBaseUrl", constants.DEFAULTURL);
        IsEditing = SharedPrefs.readSharedPreferences(getActivity(), "IsEditing", "0");
        tTrainerId = SharedPrefs.readSharedPreferences(getActivity(), "ttrainerId", "-1");
        vslaId = SharedPrefs.readSharedPreferences(getActivity(), "vslaId", "-1");

        vslaName = DataHolder.getInstance().getVslaName();
        representativeName = DataHolder.getInstance().getGroupRepresentativeName();
        representativePost = DataHolder.getInstance().getGroupRepresentativePost();
        repPhoneNumber = DataHolder.getInstance().getGroupRepresentativePhoneNumber();
        grpBankAccount = DataHolder.getInstance().getGroupBankAccount();
        physAddress = DataHolder.getInstance().getPhysicalAddress();
        regionName = DataHolder.getInstance().getRegionName();
        grpPhoneNumber = DataHolder.getInstance().getGroupPhoneNumber();
        grpBankAccount = DataHolder.getInstance().getGroupBankAccount();
        locCoordinates = DataHolder.getInstance().getLocationCoordinates();
        grpSupportType = DataHolder.getInstance().getSupportTrainingType();
        numberOfCycles = DataHolder.getInstance().getNumberOfCycles();
    }

    // submit data to the server
    private void dataSubmission() {
        getPreferences(); // reload all the data

        String jsonObjectString = createJsonObject();
        StringBuilder url = new StringBuilder();
        url.append(serverUrl);

        if (vslaName == null || numberOfCycles == null || representativeName == null || representativePost == null
                || repPhoneNumber == null || grpBankAccount == null || physAddress == null
                || grpPhoneNumber == null || grpSupportType == null) {
            flashMessage("Please Fill all Fields");

        } else if (IsEditing.equalsIgnoreCase("1")) {
            url.append("editVsla");
            new HttpAsyncTaskClass().execute(url.toString(), jsonObjectString);
            saveVslaInformation();

        } else { // Creating a new VSLA
            url.append("addNewVsla");
            new HttpAsyncTaskClass().execute(url.toString(), jsonObjectString);
            saveVslaInformation();
        }
    }

    // Load information and create a json object
    public String createJsonObject() {
        JSONObject jsonObject = new JSONObject();

        try {
            if (!vslaId.equalsIgnoreCase("-1")) {
                // editing existing information
                jsonObject.put("VslaId", vslaId);
            }
            jsonObject.put("GroupSupport", grpSupportType);
            jsonObject.put("VslaName", vslaName);
            jsonObject.put("grpPhoneNumber", grpPhoneNumber);
            jsonObject.put("PhysicalAddress", physAddress);
            jsonObject.put("GpsLocation", locCoordinates);
            jsonObject.put("representativeName", representativeName);
            jsonObject.put("representativePosition", representativePost);
            jsonObject.put("GroupAccountNumber", grpBankAccount);
            jsonObject.put("repPhoneNumber", repPhoneNumber);
            jsonObject.put("RegionName", regionName);
            jsonObject.put("tTrainerId", tTrainerId);
            jsonObject.put("Status", "2");
            jsonObject.put("numberOfCycles", numberOfCycles);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    // insert data into the database
    private void saveVslaInformation() {
        if (!databaseHandler.checkIfGroupExists(vslaName)) { // Group Doesn't exist in the database

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
            vslaInfo.setSupportType(grpSupportType);
            vslaInfo.setIsDataSent("0");
            vslaInfo.setNumberOfCycles(numberOfCycles);

            // get the Id of the Group just added to the database
            currentDatabaseId = databaseHandler.addGroupData(vslaInfo);
            flashMessage("Data Saved Successfully");

        } else {
            flashMessage("Group Already Exists");
        }
    }

    // Update the group's sent status to true after successfully submitting
    private void updateVslaInformation() {
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
        vslaInfo.setSupportType(grpSupportType);
        vslaInfo.setNumberOfCycles(numberOfCycles);
        databaseHandler.upDateGroupData(vslaInfo, currentDatabaseId);
    }

    // asynchronous task class to send data to the server.
    private class HttpAsyncTaskClass extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            flashMessage("Sending Data");
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

    // show results : Success/Fail
    private void showResultFeedback(String operationType, String operationResult, String newVslaCode) {
        if (operationType.equalsIgnoreCase("create") && operationResult.equalsIgnoreCase("1")) {

            flashMessage("Added new VSLA.");
            txtOperationType.setText(String.format("New Group created with the following VSLA Code %s",
                    newVslaCode));
            updateVslaInformation(); // update the database to sent
            // Then clear the data holder
            DataHolder.getInstance().clearDataHolder();

        } else if (operationType.equalsIgnoreCase("edit") && operationResult.equalsIgnoreCase("1")) {
            txtOperationType.setText("Group Information Edited");
            flashMessage("Successfully Edited Details.");
            updateVslaInformation(); // update the database to sent
            // Then clear the data holder
            DataHolder.getInstance().clearDataHolder();

        } else if (operationResult.equalsIgnoreCase("-1")) {

            flashMessage("An Error Occured");
            txtOperationType.setText("Error Occured");
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
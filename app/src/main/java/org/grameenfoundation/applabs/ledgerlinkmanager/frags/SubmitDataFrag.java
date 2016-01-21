package org.grameenfoundation.applabs.ledgerlinkmanager.frags;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.grameenfoundation.applabs.ledgerlinkmanager.R;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.DataHolder;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.DatabaseHandler;
import org.grameenfoundation.applabs.ledgerlinkmanager.models.VslaInfo;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


public class SubmitDataFrag extends Fragment {
    private String vslaName, representativeName, representativePost, repPhoneNumber, groupBankAccount,
            physAddress, regionName, groupPhoneNumber, locCoordinates, groupSupportType, numberOfCycles;

    private String serverUrl = "";
    private static long currentDatabaseId = 0;
    private DatabaseHandler databaseHandler;

    public SubmitDataFrag() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.submit_data_frag, container, false);
        databaseHandler = new DatabaseHandler(getActivity());
        loadVslaInformation();
        return view;
    }

    // Load data from the singleton class
    private void loadVslaInformation() {
        vslaName = DataHolder.getInstance().getVslaName();
        representativeName = DataHolder.getInstance().getGroupRepresentativeName();
        representativePost = DataHolder.getInstance().getGroupRepresentativePost();
        repPhoneNumber = DataHolder.getInstance().getGroupRepresentativePhoneNumber();
        groupBankAccount = DataHolder.getInstance().getGroupBankAccount();
        physAddress = DataHolder.getInstance().getPhysicalAddress();
        regionName = DataHolder.getInstance().getRegionName();
        groupPhoneNumber = DataHolder.getInstance().getGroupPhoneNumber();
        locCoordinates = DataHolder.getInstance().getLocationCoordinates();
        groupSupportType = DataHolder.getInstance().getSupportTrainingType();
        numberOfCycles = DataHolder.getInstance().getNumberOfCycles();
    }

    // Show toast method
    private void flashMessage(String toastMessage) {
        Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();
    }

    // Load information and create a json object
    public String createJsonObject() {
        JSONObject jsonObject = new JSONObject();

        try {
            /*if (!vslaId.equalsIgnoreCase("-1")) {
                // editing existing information
                jsonObject.put("VslaId", vslaId);
            }*/
            jsonObject.put("GroupSupport", groupSupportType);
            jsonObject.put("VslaName", vslaName);
            jsonObject.put("grpPhoneNumber", groupPhoneNumber);
            jsonObject.put("PhysicalAddress", physAddress);
            jsonObject.put("GpsLocation", locCoordinates);
            jsonObject.put("representativeName", representativeName);
            jsonObject.put("representativePosition", representativePost);
            jsonObject.put("GroupAccountNumber", groupBankAccount);
            jsonObject.put("repPhoneNumber", repPhoneNumber);
            jsonObject.put("RegionName", regionName);
            // jsonObject.put("tTrainerId", tTrainerId);
            jsonObject.put("tTrainerId", 1);
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
            vslaInfo.setGroupAccountNumber(groupBankAccount);
            vslaInfo.setPhysicalAddress(physAddress);
            vslaInfo.setRegionName(regionName);
            vslaInfo.setLocationCordinates(locCoordinates);
            vslaInfo.setIssuedPhoneNumber(groupPhoneNumber);
            vslaInfo.setSupportType(groupSupportType);
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
        vslaInfo.setGroupAccountNumber(groupBankAccount);
        vslaInfo.setPhysicalAddress(physAddress);
        vslaInfo.setRegionName(regionName);
        vslaInfo.setLocationCordinates(locCoordinates);
        vslaInfo.setIssuedPhoneNumber(groupPhoneNumber);
        vslaInfo.setIsDataSent("1");
        vslaInfo.setSupportType(groupSupportType);
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
            updateVslaInformation();
            // Then clear the data holder
            DataHolder.getInstance().clearDataHolder();
        } else if (operationType.equalsIgnoreCase("edit") && operationResult.equalsIgnoreCase("1")) {
            flashMessage("Successfully Edited Details.");
            updateVslaInformation();
            // Then clear the data holder
            DataHolder.getInstance().clearDataHolder();
        } else if (operationResult.equalsIgnoreCase("-1")) {
            flashMessage("An Error Occured");
        }

    }

}

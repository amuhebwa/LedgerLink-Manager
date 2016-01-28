package org.grameenfoundation.applabs.ledgerlinkmanager.frags;


import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.grameenfoundation.applabs.ledgerlinkmanager.JsonData;
import org.grameenfoundation.applabs.ledgerlinkmanager.R;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.Constants;
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
    private String vslaId, vslaName, representativeName, representativePost, repPhoneNumber, groupBankAccount,
            physAddress, regionName, groupPhoneNumber, locCoordinates, groupSupportType, numberOfCycles,
            trainerId;
    private TextView addEditOperation;
    private ImageView confirmSubmission;
    private TableLayout dataTable;
    private String serverUrl = "";
    private String EditAddTitle;
    private static long currentDatabaseId = 0;
    private DatabaseHandler databaseHandler;
    private Constants constants;
    boolean isEditing = false;

    public SubmitDataFrag() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.submit_data_frag, container, false);
       //  setHasOptionsMenu(true);
        databaseHandler = new DatabaseHandler(getActivity());
        loadVslaInformation();
        showSummaryOfFields(view);
        constants = new Constants();
        android.content.SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        serverUrl = sharedPreferences.getString("LedgerLinkBaseUrl", constants.DEFAULTURL);
        return view;
    }

    private void dataSubmission() {
        // First reload the information
        loadVslaInformation();
        String jsonObjectString = createJsonObject();
        StringBuilder url = new StringBuilder();
        url.append(serverUrl);
        if (isEditing) {
            url.append("editVsla");
        } else {
            url.append("addNewVsla");
        }
        saveVslaInformation();
        new HttpAsyncTaskClass().execute(url.toString(), jsonObjectString);
    }

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
        trainerId = JsonData.getInstance().getTrainerId();
        isEditing = JsonData.getInstance().isEditing();
        vslaId = JsonData.getInstance().getVslaId();
    }

    private void showSummaryOfFields(View view) {
        TextView mVslaName = (TextView) view.findViewById(R.id.vslaName);
        TextView mRepresentativeName = (TextView) view.findViewById(R.id.representativeName);
        TextView mRepresentativePost = (TextView) view.findViewById(R.id.representativePost);
        TextView mRepresentativeNumber = (TextView) view.findViewById(R.id.representativeNumber);
        TextView mGroupBankAccount = (TextView) view.findViewById(R.id.groupBankAccount);
        TextView mPhysicalAddress = (TextView) view.findViewById(R.id.physicalAddress);
        TextView mGroupPhoneNumber = (TextView) view.findViewById(R.id.groupPhoneNumber);
        TextView mLocCoordinates = (TextView) view.findViewById(R.id.locCoordinates);
        TextView mGroupSupportType = (TextView) view.findViewById(R.id.groupSupportType);
        TextView mNumberOfCycles = (TextView) view.findViewById(R.id.numberOfCycles);
        addEditOperation = (TextView) view.findViewById(R.id.add_editOperation);
        confirmSubmission = (ImageView) view.findViewById(R.id.confirmSubmission);
        dataTable = (TableLayout) view.findViewById(R.id.dataTable);

        // set text to the corrrsponding fields
        mVslaName.setText(vslaName);
        mRepresentativeName.setText(representativeName);
        mRepresentativePost.setText(representativePost);
        mRepresentativeNumber.setText(repPhoneNumber);
        mGroupBankAccount.setText(groupBankAccount);
        mPhysicalAddress.setText(physAddress);
        // mRegionName.setText(regionName);
        mGroupPhoneNumber.setText(groupPhoneNumber);
        mLocCoordinates.setText(locCoordinates);
        mGroupSupportType.setText(groupSupportType);
        mNumberOfCycles.setText(numberOfCycles);

        // Set operation type(Adding New Group Or Editing an Existing Group)
        if (isEditing) {
            EditAddTitle = "Editing " + vslaName;
            addEditOperation.setText(EditAddTitle);
        } else {
            EditAddTitle = "Adding New Group " + vslaName;
            addEditOperation.setText(EditAddTitle);
        }
    }

    private void flashMessage(String toastMessage) {
        Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();
    }

    // Load information and create a json object
    public String createJsonObject() {
        JSONObject jsonObject = new JSONObject();

        try {
            if (isEditing) {
                jsonObject.put("VslaId", vslaId);
            }
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
            jsonObject.put("tTrainerId", trainerId);
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

    /**
     * Update the group's sent status to true after successfully
     * submitting
     */

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

    /**
     * asynchronous task class to send data to the server.
     */

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
            DataHolder.getInstance().clearDataHolder();
            addEditOperation.setText("Added New VSLA with VSLA Code : " + newVslaCode);
            confirmSubmission.setVisibility(View.VISIBLE);
            dataTable.setVisibility(View.GONE);
        } else if (operationType.equalsIgnoreCase("edit") && operationResult.equalsIgnoreCase("1")) {
            flashMessage("Successfully Edited Details.");
            updateVslaInformation();
            // Then clear the data holder
            DataHolder.getInstance().clearDataHolder();
            addEditOperation.setText("Successfully Edited Information");
            confirmSubmission.setVisibility(View.VISIBLE);
            dataTable.setVisibility(View.GONE);
        } else if (operationResult.equalsIgnoreCase("-1")) {
            flashMessage("An Error Occured");
            addEditOperation.setText("Error Occured. Try Again");
            confirmSubmission.setImageResource(R.drawable.ic_error_black);
            confirmSubmission.setVisibility(View.VISIBLE);
        } else {
            addEditOperation.setText("Error Occured. Try Again");
            confirmSubmission.setImageResource(R.drawable.ic_error_black);
            confirmSubmission.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_datasubmission_frag, menu);
        setHasOptionsMenu(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem actionViewItem = menu.findItem(R.id.action_save);
        View v = MenuItemCompat.getActionView(actionViewItem);
        Button b = (Button) v.findViewById(R.id.btnSave);
        b.setText("Submit");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataSubmission();
            }
        });

        super.onPrepareOptionsMenu(menu);
    }
}
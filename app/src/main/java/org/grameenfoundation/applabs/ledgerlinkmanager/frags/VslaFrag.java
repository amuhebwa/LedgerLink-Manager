package org.grameenfoundation.applabs.ledgerlinkmanager.frags;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.grameenfoundation.applabs.ledgerlinkmanager.CreateGroup;
import org.grameenfoundation.applabs.ledgerlinkmanager.JsonData;
import org.grameenfoundation.applabs.ledgerlinkmanager.R;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.DataHolder;
import org.grameenfoundation.applabs.ledgerlinkmanager.models.VslaInfo;
import org.json.JSONException;
import org.json.JSONObject;

public class VslaFrag extends Fragment {
    private VslaFragInterface vslaFragInterface;
    private EditText inputGroupName, inputGroupPhoneNumber, inputMemberName, inputMemberPost,
            inputMemberPhoneNumber, inputGroupAccountNumber, inputNumbeOfCycles;

    public VslaFrag() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vsla_frag, container, false);
        inputGroupName = (EditText) view.findViewById(R.id.groupName);
        inputGroupPhoneNumber = (EditText) view.findViewById(R.id.groupPhoneNumber);
        inputMemberName = (EditText) view.findViewById(R.id.memberName);
        inputMemberPost = (EditText) view.findViewById(R.id.memberPost);
        inputMemberPhoneNumber = (EditText) view.findViewById(R.id.memberPhoneNumber);
        inputGroupAccountNumber = (EditText) view.findViewById(R.id.groupAccountNumber);
        inputNumbeOfCycles = (EditText) view.findViewById(R.id.numbeOfCycles);
        Boolean isEditing = JsonData.getInstance().isEditing();
        String jsonData = JsonData.getInstance().getVslaJsonStringData();
        if (isEditing) {
            processVslaInformation(jsonData);
        }
        return view;
    }

    /**
     * Add information attached to one vsla group into a singleton class. This is when a group is being edited
     */
    private void processVslaInformation(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String vslaName = jsonObject.getString("VslaName");
            String groupPhoneNumber = jsonObject.getString("grpPhoneNumber");
            String numberOfCycles = jsonObject.getString("numberOfCycles");
            String memberName = jsonObject.getString("representativeName");
            String memberPost = jsonObject.getString("representativePosition");
            String memberPhoneNumber = jsonObject.getString("repPhoneNumber");
            String bankAccount = jsonObject.getString("GroupAccountNumber");
            inputGroupName.setText(vslaName);
            inputGroupPhoneNumber.setText(groupPhoneNumber);
            inputNumbeOfCycles.setText(numberOfCycles);
            inputMemberName.setText(memberName);
            inputMemberPost.setText(memberPost);
            inputMemberPhoneNumber.setText(memberPhoneNumber);
            inputGroupAccountNumber.setText(bankAccount);

            // Set the title in the actionbar to group name
            ActionBar actionBar = ((CreateGroup)getActivity()).getSupportActionBar();
            actionBar.setTitle(vslaName != null ? vslaName : null);
            actionBar.setDisplayHomeAsUpEnabled(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof VslaFragInterface) {
            vslaFragInterface = (VslaFragInterface) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        vslaFragInterface = null;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem actionViewItem = menu.findItem(R.id.action_save);
        View v = MenuItemCompat.getActionView(actionViewItem);
        Button b = (Button) v.findViewById(R.id.btnSave);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInfoToActivity();
            }
        });
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_vsla_frag, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private boolean validateInputFields() {
        String groupName, groupPhoneNumber, memberName, memberPost, memberPhoneNumber, groupAccountNumber, numberOfCycles;
        groupName = inputGroupName.getText().toString().trim();
        groupPhoneNumber = inputGroupPhoneNumber.getText().toString().trim();
        memberName = inputMemberName.getText().toString().trim();
        memberPost = inputMemberPost.getText().toString().trim();
        memberPhoneNumber = inputMemberPhoneNumber.getText().toString().trim();
        groupAccountNumber = inputGroupAccountNumber.getText().toString().trim();
        numberOfCycles = inputNumbeOfCycles.getText().toString().trim();

        DataHolder.getInstance().setVslaName(groupName);
        DataHolder.getInstance().setGroupPhoneNumber(groupPhoneNumber);
        DataHolder.getInstance().setGroupRepresentativeName(memberName);
        DataHolder.getInstance().setGroupRepresentativePost(memberPost);
        DataHolder.getInstance().setGroupRepresentativePhoneNumber(memberPhoneNumber);
        DataHolder.getInstance().setGroupBankAccount(groupAccountNumber);
        DataHolder.getInstance().setNumberOfCycles(numberOfCycles);

        if (groupName.isEmpty()) {
            return false;
        } else if (groupPhoneNumber.length() < 10 || groupPhoneNumber.length() > 10) {
            return false;
        } else if (memberName.isEmpty()) {
            return false;
        } else if (memberPost.isEmpty()) {
            return false;
        } else if (memberPhoneNumber.length() < 10 || memberPhoneNumber.length() > 10) {
            return false;
        } else if (groupAccountNumber.length() < 10 || groupAccountNumber.length() > 10) {
            return false;
        } else if (numberOfCycles.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private void updateInfoToActivity() {
        String command = "next";
        int fragmentNumber = 1;
        if (validateInputFields()) {
            vslaFragInterface.passInfoToActivity(command, fragmentNumber);
        } else {
            Toast.makeText(getActivity(), "Some Fields have Errors.", Toast.LENGTH_SHORT).show();
        }
    }

    // Interface to communicate with the parent class
    public interface VslaFragInterface {
        void passInfoToActivity(String command, int fragmentNumber);
    }
}

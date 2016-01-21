package org.grameenfoundation.applabs.ledgerlinkmanager.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.grameenfoundation.applabs.ledgerlinkmanager.CreateGroup;
import org.grameenfoundation.applabs.ledgerlinkmanager.R;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.DataHolder;
import org.grameenfoundation.applabs.ledgerlinkmanager.interfaces.VslaInterface;


public class VslaFragment extends Fragment implements VslaInterface {
    private EditText extGroupName;
    private EditText extGroupPhoneNumber;
    private EditText extMemberName;
    private EditText extMemberPost;
    private EditText extMemberPhoneNumber;
    private EditText extGroupAccountName;
    private EditText extNumbeOfCycles;
    private MenuItem cancelMenu;
    private MenuItem editMenu;
    private MenuItem saveMenu;

    public VslaFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group_information, container, false);
        setHasOptionsMenu(true);
        loadUIComponents(view);
        disableEditing();
        return view;
    }

    // Load UI components
    private void loadUIComponents(View view) {
        extGroupName = (EditText) view.findViewById(R.id.groupName);
        extGroupPhoneNumber = (EditText) view.findViewById(R.id.groupPhoneNumber);
        extMemberName = (EditText) view.findViewById(R.id.memberName);
        extMemberPost = (EditText) view.findViewById(R.id.memberPost);
        extMemberPhoneNumber = (EditText) view.findViewById(R.id.memberPhoneNumber);
        extGroupAccountName = (EditText) view.findViewById(R.id.groupAccountNumber);
        extNumbeOfCycles = (EditText) view.findViewById(R.id.numbeOfCycles);
    }

    // Disable text fields to disable text fields
    private void disableEditing() {
        extGroupName.setEnabled(false);
        extMemberName.setEnabled(false);
        extMemberPost.setEnabled(false);
        extMemberPhoneNumber.setEnabled(false);
        extGroupAccountName.setEnabled(false);
        extGroupPhoneNumber.setEnabled(false);
        extNumbeOfCycles.setEnabled(false);
    }

    // Enable text fields
    private void enableEditing() {
        extGroupName.setEnabled(true);
        extMemberName.setEnabled(true);
        extMemberPost.setEnabled(true);
        extMemberPhoneNumber.setEnabled(true);
        extGroupAccountName.setEnabled(true);
        extGroupPhoneNumber.setEnabled(true);
        extNumbeOfCycles.setEnabled(true);
    }

    // Clear error messages from text fields
    public void clearErrorMessages() {
        extGroupName.setError(null);
        extMemberName.setError(null);
        extMemberPost.setError(null);
        extMemberPhoneNumber.setError(null);
        extGroupAccountName.setError(null);
        extGroupPhoneNumber.setError(null);
        extNumbeOfCycles.setError(null);
    }

    // Save data to singleton class
    private void saveInformationToDataHolder() {
        if (extGroupName.getText().toString().isEmpty()) {
            extGroupName.setError("Enter Valid Group Name");
        } else if (extGroupPhoneNumber.getText().toString().isEmpty() || extGroupPhoneNumber.getText().toString().length() < 10 || extGroupPhoneNumber.getText().toString().length() > 10) {
            extGroupPhoneNumber.setError("Phone Number is 10 Digits");
        } else if (extMemberName.getText().toString().isEmpty() || extMemberName.getText().toString().length() < 2) {
            extMemberName.setError("Enter Member Name");
        } else if (extMemberPost.getText().toString().isEmpty()) {
            extMemberPost.setError("Enter Member Post");
        } else if (extMemberPhoneNumber.getText().toString().isEmpty() || extMemberPhoneNumber.getText().toString().length() < 10 || extMemberPhoneNumber.getText().toString().length() > 10) {
            extMemberPhoneNumber.setError("Phone Number is 10 Digits");
        } else if (extGroupAccountName.getText().toString().isEmpty() || extGroupAccountName.getText().toString().length() < 10 || extGroupAccountName.getText().toString().length() > 10) {
            extGroupAccountName.setError("Group Account is 10 Digits");
        }else if (Integer.parseInt(extNumbeOfCycles.getText().toString()) > 100){
            extNumbeOfCycles.setError("Number of cycles should be more than 100");
        }
        else {
            DataHolder.getInstance().setVslaName(extGroupName.getText().toString());
            DataHolder.getInstance().setGroupPhoneNumber(extGroupPhoneNumber.getText().toString());
            DataHolder.getInstance().setGroupRepresentativeName(extMemberName.getText().toString());
            DataHolder.getInstance().setGroupRepresentativePost(extMemberPost.getText().toString());
            DataHolder.getInstance().setGroupRepresentativePhoneNumber(extMemberPhoneNumber.getText().toString());
            DataHolder.getInstance().setGroupBankAccount(extGroupAccountName.getText().toString());
            DataHolder.getInstance().setNumberOfCycles(extNumbeOfCycles.getText().toString());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Context context = getActivity();
       //  ((CreateGroup) context).vslaInterface = this;
    }

    @Override
    public void passGroupInformation(String groupName, String groupPhoneNumber, String memberName, String memberPost, String memberPhoneNumber, String branchName, String numberOfCycles) {

        // Populate the fields with data from the server
        extGroupName.setText(groupName);
        extGroupPhoneNumber.setText(groupPhoneNumber);
        extMemberName.setText(memberName);
        extMemberPost.setText(memberPost);
        extMemberPhoneNumber.setText(memberPhoneNumber);
        extGroupAccountName.setText(branchName);
        extNumbeOfCycles.setText(numberOfCycles);

        // ((CreateGroup) getActivity()).changeActionBarTitle(groupName != null ? groupName : null);
        // Then save data to the data holder
        saveInformationToDataHolder();
    }

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_group_information, menu);

        if (menu != null) {
            cancelMenu = menu.findItem(R.id.action_cancel);
            editMenu = menu.findItem(R.id.action_edit);
            saveMenu = menu.findItem(R.id.action_save);
        }

        cancelMenu.setVisible(false);
        saveMenu.setVisible(false);

        super.onCreateOptionsMenu(menu, inflater);
    }*/

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_edit:
                cancelMenu.setVisible(true);
                saveMenu.setVisible(true);
                editMenu.setVisible(false);
                enableEditing();
                clearErrorMessages();
                break;

            case R.id.action_save:
                cancelMenu.setVisible(false);
                saveMenu.setVisible(false);
                editMenu.setVisible(true);
                saveInformationToDataHolder();
                disableEditing();
                break;

            default:

        }

        return super.onOptionsItemSelected(item);
    }*/
}

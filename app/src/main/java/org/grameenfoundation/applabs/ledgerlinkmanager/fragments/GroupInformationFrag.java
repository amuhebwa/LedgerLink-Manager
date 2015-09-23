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

import org.grameenfoundation.applabs.ledgerlinkmanager.VslaGroupDetails;
import org.grameenfoundation.applabs.ledgerlinkmanager.R;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.DataHolder;
import org.grameenfoundation.applabs.ledgerlinkmanager.interfaces.GroupInformationInterface;


public class GroupInformationFrag extends Fragment implements GroupInformationInterface {
    private EditText GroupName, GroupPhoneNumber, MemberName, MemberPost, MemberPhoneNumber, GroupAccountName;
    private MenuItem cancelMenu, editMenu, saveMenu;

    public GroupInformationFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_information, container, false);
        setHasOptionsMenu(true);
        loadUIComponents(view);
        disableEditing();
        return view;
    }

    /**
     * Load UI components
     */
    private void loadUIComponents(View view) {
        GroupName = (EditText) view.findViewById(R.id.groupName);
        GroupPhoneNumber = (EditText) view.findViewById(R.id.groupPhoneNumber);
        MemberName = (EditText) view.findViewById(R.id.memberName);
        MemberPost = (EditText) view.findViewById(R.id.memberPost);
        MemberPhoneNumber = (EditText) view.findViewById(R.id.memberPhoneNumber);
        GroupAccountName = (EditText) view.findViewById(R.id.groupAccountNumber);
    }

    /**
     * Disable text fields to disable/editing/allow saving
     */
    private void disableEditing() {
        GroupName.setEnabled(false);
        MemberName.setEnabled(false);
        MemberPost.setEnabled(false);
        MemberPhoneNumber.setEnabled(false);
        GroupAccountName.setEnabled(false);
        GroupPhoneNumber.setEnabled(false);
    }

    /**
     * Enable text fields in order to edit/add information
     */
    private void enableEditing() {
        GroupName.setEnabled(true);
        MemberName.setEnabled(true);
        MemberPost.setEnabled(true);
        MemberPhoneNumber.setEnabled(true);
        GroupAccountName.setEnabled(true);
        GroupPhoneNumber.setEnabled(true);
    }

    /**
     * Clear error messages from text fields
     */
    public void clearErrorMessages() {
        GroupName.setError(null);
        MemberName.setError(null);
        MemberPost.setError(null);
        MemberPhoneNumber.setError(null);
        GroupAccountName.setError(null);
        GroupPhoneNumber.setError(null);
    }

    /**
     * Save the text field data to the data holder/singleton class
     */
    private void saveInformationToDataHolder() {
        if (GroupName.getText().toString().isEmpty()) {
            GroupName.setError("Enter Valid Group Name");
        } else if (GroupPhoneNumber.getText().toString().isEmpty() || GroupPhoneNumber.getText().toString().length() < 10 | GroupPhoneNumber.getText().toString().length() > 10) {
            GroupPhoneNumber.setError("Phone Number is 10 Digits");
        } else if (MemberName.getText().toString().isEmpty() || MemberName.getText().toString().length() < 2) {
            MemberName.setError("Enter Member Name");
        } else if (MemberPost.getText().toString().isEmpty()) {
            MemberPost.setError("Enter Member Post");
        } else if (MemberPhoneNumber.getText().toString().isEmpty() || MemberPhoneNumber.getText().toString().length() < 10 || MemberPhoneNumber.getText().toString().length() > 10) {
            MemberPhoneNumber.setError("Phone Number is 10 Digits");
        } else if (GroupAccountName.getText().toString().isEmpty() || GroupAccountName.getText().toString().length() < 10 || GroupAccountName.getText().toString().length() > 10) {
            GroupAccountName.setError("Group Account is 10 Digits");
        } else {
            DataHolder.getInstance().setVslaName(GroupName.getText().toString());
            DataHolder.getInstance().setGroupPhoneNumber(GroupPhoneNumber.getText().toString());
            DataHolder.getInstance().setGroupRepresentativeName(MemberName.getText().toString());
            DataHolder.getInstance().setGroupRepresentativePost(MemberPost.getText().toString());
            DataHolder.getInstance().setGroupRepresentativePhoneNumber(MemberPhoneNumber.getText().toString());
            DataHolder.getInstance().setGroupBankAccount(GroupAccountName.getText().toString());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Context context = getActivity();
        ((VslaGroupDetails) context).groupInformationInterface = this;
    }

    @Override
    public void passGroupInformation(String groupName, String groupPhoneNumber, String memberName, String memberPost, String memberPhoneNumber, String branchName) {

        /** Populate the fields with data from the server */
        GroupName.setText(groupName);
        GroupPhoneNumber.setText(groupPhoneNumber);
        MemberName.setText(memberName);
        MemberPost.setText(memberPost);
        MemberPhoneNumber.setText(memberPhoneNumber);
        GroupAccountName.setText(branchName);

        ((VslaGroupDetails) getActivity()).changeActionBarTitle(groupName != null ? groupName : null);

        /** Then save data to the data holder */
        saveInformationToDataHolder();


    }

    @Override
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            cancelMenu.setVisible(true);
            saveMenu.setVisible(true);
            editMenu.setVisible(false);
            enableEditing();
            clearErrorMessages();
        }
        if (id == R.id.action_save) {
            cancelMenu.setVisible(false);
            saveMenu.setVisible(false);
            editMenu.setVisible(true);
            saveInformationToDataHolder();
            disableEditing();
        }
        return super.onOptionsItemSelected(item);

    }
}
